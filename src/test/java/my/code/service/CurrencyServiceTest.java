package my.code.service;

import my.code.database.entity.Currency;
import my.code.database.entity.ExchangeRate;
import my.code.database.repository.CurrencyRepository;
import my.code.database.repository.ExchangeRateRepository;
import my.code.dto.CurrencyDTO;
import my.code.dto.ExchangeRateDTO;
import my.code.mapper.CurrencyExchangeRatesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CurrencyServiceTest {

    @MockBean
    private CurrencyRepository currencyRepository;

    @MockBean
    private ExchangeRateRepository exchangeRateRepository;

    @MockBean
    private CurrencyExchangeRatesMapper mapper;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        currencyService = new CurrencyService(currencyRepository, exchangeRateRepository, mapper);
    }

    @Test
    void getAllCurrencies_ReturnsListOfCurrencies() {
        List<Currency> currencies = Arrays.asList(new Currency("USD"), new Currency("EUR"));
        List<CurrencyDTO> expectedCurrencies = Arrays.asList(new CurrencyDTO("USD"), new CurrencyDTO("EUR"));
        when(currencyRepository.findAll()).thenReturn(currencies);
        when(mapper.mapCurrenciesToDTOs(currencies)).thenReturn(expectedCurrencies);

        List<CurrencyDTO> result = currencyService.getAllCurrencies();

        assertThat(result).isEqualTo(expectedCurrencies);
        verify(currencyRepository).findAll();
        verify(mapper).mapCurrenciesToDTOs(currencies);
    }

    @Test
    void getExchangeRates_WithValidCurrencyCode_ReturnsListOfExchangeRates() {
        String currencyCode = "USD";
        Currency baseCurrency = new Currency(currencyCode);
        List<ExchangeRate> exchangeRates = Arrays.asList(new ExchangeRate(), new ExchangeRate());
        List<ExchangeRateDTO> expectedExchangeRates = Arrays.asList(new ExchangeRateDTO(), new ExchangeRateDTO());
        when(currencyRepository.findByCode(currencyCode)).thenReturn(Optional.of(baseCurrency));
        when(exchangeRateRepository.findByBaseCurrency(baseCurrency)).thenReturn(exchangeRates);
        when(mapper.mapExchangeRatesToDTOs(exchangeRates)).thenReturn(expectedExchangeRates);

        List<ExchangeRateDTO> result = currencyService.getExchangeRates(currencyCode);

        assertThat(result).isEqualTo(expectedExchangeRates);
        verify(currencyRepository).findByCode(currencyCode);
        verify(exchangeRateRepository).findByBaseCurrency(baseCurrency);
        verify(mapper).mapExchangeRatesToDTOs(exchangeRates);
    }

    @Test
    void addCurrency_WithNewCurrency_ReturnsAddedCurrency() {
        CurrencyDTO currencyDTO = new CurrencyDTO("NEW");
        Currency currency = new Currency(currencyDTO.getCode());
        when(currencyRepository.findByCode(currencyDTO.getCode())).thenReturn(Optional.empty());

        ArgumentCaptor<Currency> currencyCaptor = ArgumentCaptor.forClass(Currency.class);

        when(currencyRepository.save(currencyCaptor.capture())).thenAnswer(invocation -> {
            Currency savedCurrency = invocation.getArgument(0);
            savedCurrency.setCode("NEW");
            return savedCurrency;
        });
        when(mapper.mapCurrencyToDTO(currency)).thenReturn(currencyDTO);

        CurrencyDTO result = currencyService.addCurrency(currencyDTO);


        assertThat(currencyCaptor.getValue()).isEqualTo(currency);
        assertThat(result).isEqualTo(currencyDTO);
        verify(currencyRepository).save(currency);
        verify(mapper).mapCurrencyToDTO(currency);
    }

    @Test
    void addCurrency_WithExistingCurrency_ThrowsException() {
        CurrencyDTO currencyDTO = new CurrencyDTO("USD");
        Currency existingCurrency = new Currency(currencyDTO.getCode());
        when(currencyRepository.findByCode(currencyDTO.getCode())).thenReturn(Optional.of(existingCurrency));

        assertThrows(RuntimeException.class, () -> currencyService.addCurrency(currencyDTO));
        verify(currencyRepository, never()).save(any());
    }
}