package my.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.code.dto.CurrencyDTO;
import my.code.dto.ExchangeRateDTO;
import my.code.service.CurrencyExchangeRateService;
import my.code.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private CurrencyExchangeRateService currencyExchangeRateService;

    @Test
    void getCurrencies_ReturnsListOfCurrencies() throws Exception {
        List<CurrencyDTO> currencies = Arrays.asList(
                new CurrencyDTO("USD"),
                new CurrencyDTO("EUR")
        );
        when(currencyService.getAllCurrencies()).thenReturn(currencies);

        mockMvc.perform(get("/api/currencies/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("USD")))
                .andExpect(jsonPath("$[1].code", is("EUR")));
    }

    @Test
    void getExchangeRates_WithValidCurrencyCode_ReturnsExchangeRates() throws Exception {
        String currencyCode = "USD";
        List<ExchangeRateDTO> exchangeRates = Arrays.asList(
                new ExchangeRateDTO("USD", BigDecimal.valueOf(1.0)),
                new ExchangeRateDTO("EUR", BigDecimal.valueOf(0.8))
        );
        when(currencyService.getExchangeRates(currencyCode)).thenReturn(exchangeRates);

        mockMvc.perform(get("/api/currencies/{currencyCode}", currencyCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void convertCurrency_WithValidParameters_ReturnsConvertedAmount() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        BigDecimal convertedAmount = BigDecimal.valueOf(120);
        when(currencyExchangeRateService.convert(amount, fromCurrency, toCurrency)).thenReturn(convertedAmount);

        mockMvc.perform(get("/api/currencies/convert/{amount}/{fromCurrency}/{toCurrency}", amount, fromCurrency, toCurrency)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("120"));
    }

    @Test
    void addCurrency_WithValidRequestBody_ReturnsAddedCurrency() throws Exception {
        CurrencyDTO currencyDTO = new CurrencyDTO("NEW");
        when(currencyService.addCurrency(any())).thenReturn(currencyDTO);

        mockMvc.perform(post("/api/currencies/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currencyDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("NEW")));
    }
}