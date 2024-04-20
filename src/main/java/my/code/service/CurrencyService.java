package my.code.service;

import lombok.RequiredArgsConstructor;
import my.code.database.entity.Currency;
import my.code.database.entity.ExchangeRate;
import my.code.database.repository.CurrencyRepository;
import my.code.database.repository.ExchangeRateRepository;
import my.code.dto.CurrencyDTO;
import my.code.dto.ExchangeRateDTO;
import my.code.mapper.CurrencyExchangeRatesMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyExchangeRatesMapper mapper;

    public List<CurrencyDTO> getAllCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return mapper.mapCurrenciesToDTOs(currencies);
    }

    public List<ExchangeRateDTO> getExchangeRates(String currencyCode) {
        Currency baseCurrency = currencyRepository.findByCode(currencyCode)
                .orElseThrow(() -> new RuntimeException("Currency not found"));
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findByBaseCurrency(baseCurrency);
        if (exchangeRates.isEmpty()) {
            exchangeRates = getDefaultExchangeRates();
        }
        return mapper.mapExchangeRatesToDTOs(exchangeRates);
    }

    @Transactional
    public CurrencyDTO addCurrency(CurrencyDTO currencyDTO) {
        Currency existingCurrency = currencyRepository.findByCode(currencyDTO.getCode()).orElse(null);
        if (existingCurrency != null) {
            throw new RuntimeException("Currency already exists");
        }
        Currency currency = new Currency();
        currency.setCode(currencyDTO.getCode());
        currencyRepository.save(currency);
        return mapper.mapCurrencyToDTO(currency);
    }

    private List<ExchangeRate> getDefaultExchangeRates() {
        var code = currencyRepository.findByCode("USD")
                .orElseThrow(() -> new RuntimeException("Currency not found"));
        return exchangeRateRepository.findByBaseCurrency(code);
    }
}