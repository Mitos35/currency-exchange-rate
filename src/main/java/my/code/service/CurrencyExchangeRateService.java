package my.code.service;

import lombok.RequiredArgsConstructor;
import my.code.database.entity.Currency;
import my.code.database.entity.ExchangeRate;
import my.code.database.repository.CurrencyRepository;
import my.code.database.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeRateService {
    @Value("${exchange.api.url}")
    private String exchangeRateApiUrl;

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final RestTemplate restTemplate;
    private final Map<String, BigDecimal> exchangeRates = new HashMap<>();

    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {

        BigDecimal rateFrom = exchangeRates.get(fromCurrency);
        BigDecimal rateTo = exchangeRates.get(toCurrency);

        if (rateFrom == null || rateTo == null) {
            throw new IllegalArgumentException("Invalid currency code");
        }

        BigDecimal amountInBaseCurrency = amount.divide(rateFrom, 2, BigDecimal.ROUND_HALF_UP);
        return amountInBaseCurrency.multiply(rateTo).setScale(2, BigDecimal.ROUND_HALF_UP);

    }

    @Scheduled(fixedDelay = 3600000)
    @Transactional
    public void updateExchangeRates() {
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                exchangeRateApiUrl,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        Map<String, Object> responseData = responseEntity.getBody();
        String baseCurrencyCode = responseData != null ? (String) responseData.get("base") : null;
        Map<String, Object> rates = (Map<String, Object>) responseData.get("rates");

        Currency baseCurrency = currencyRepository.findByCode(baseCurrencyCode)
                .orElseGet(() -> currencyRepository.save(new Currency(baseCurrencyCode)));

        updateExchangeRatesInMemory(rates);
        updateExchangeRatesInDatabase(baseCurrency, rates);
    }

    private void updateExchangeRatesInMemory(Map<String, Object> rates) {
        rates.forEach((code, rateObj) -> {
            var rate = getRate(rateObj);
            if (rate == null) return;
            exchangeRates.put(code, rate);
        });
    }

    private void updateExchangeRatesInDatabase(Currency baseCurrency, Map<String, Object> rates) {
        rates.forEach((code, rateObj) -> {
            Currency targetCurrency = currencyRepository.findByCode(code)
                    .orElseGet(() -> currencyRepository.save(new Currency(code)));

            var rate = getRate(rateObj);
            if (rate == null) return;

            ExchangeRate exchangeRate = exchangeRateRepository
                    .findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency)
                    .orElseGet(ExchangeRate::new);

            exchangeRate.setBaseCurrency(baseCurrency);
            exchangeRate.setTargetCurrency(targetCurrency);
            exchangeRate.setRate(rate);

            exchangeRateRepository.save(exchangeRate);
        });
    }

    private static BigDecimal getRate(Object rateObj) {
        BigDecimal rate;
        if (rateObj instanceof Integer value) {
            rate = BigDecimal.valueOf(value);
        } else if (rateObj instanceof Double value) {
            rate = BigDecimal.valueOf(value);
        } else {
            return null;
        }
        return rate;
    }
}
