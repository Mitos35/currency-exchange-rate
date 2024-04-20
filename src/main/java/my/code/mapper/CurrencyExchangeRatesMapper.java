package my.code.mapper;

import my.code.database.entity.Currency;
import my.code.database.entity.ExchangeRate;
import my.code.dto.CurrencyDTO;
import my.code.dto.ExchangeRateDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrencyExchangeRatesMapper {
    public List<CurrencyDTO> mapCurrenciesToDTOs(List<Currency> currencies) {
        return currencies.stream()
                .map(this::mapCurrencyToDTO)
                .toList();
    }

    public CurrencyDTO mapCurrencyToDTO(Currency currency) {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setCode(currency.getCode());
        return dto;
    }

    public List<ExchangeRateDTO> mapExchangeRatesToDTOs(List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .map(this::mapExchangeRateToDTO)
                .toList();
    }

    private ExchangeRateDTO mapExchangeRateToDTO(ExchangeRate exchangeRate) {
        ExchangeRateDTO dto = new ExchangeRateDTO();
        dto.setTargetCurrencyCode(exchangeRate.getTargetCurrency().getCode());
        dto.setRate(exchangeRate.getRate());
        return dto;
    }
}
