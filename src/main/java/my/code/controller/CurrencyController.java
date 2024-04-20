package my.code.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import my.code.dto.CurrencyDTO;
import my.code.dto.ExchangeRateDTO;
import my.code.service.CurrencyExchangeRateService;
import my.code.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final CurrencyExchangeRateService currencyExchangeRateService;

    @Operation(summary = "Get all currencies", description = "Returns a list of all available currencies.")
    @GetMapping("/list")
    public ResponseEntity<List<CurrencyDTO>> getCurrencies() {
        List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
        return ResponseEntity.ok(currencies);
    }


    @Operation(summary = "Get exchange rates by currency code", description = "Returns exchange rates for a specific currency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rates."),
            @ApiResponse(responseCode = "404", description = "Currency not found.")
    })
    @GetMapping("/{currencyCode}")
    public ResponseEntity<List<ExchangeRateDTO>> getExchangeRates(@PathVariable String currencyCode) {
        List<ExchangeRateDTO> exchangeRates = currencyService.getExchangeRates(currencyCode);
        return ResponseEntity.ok(exchangeRates);
    }

    @Operation(summary = "Convert currency", description = "Converts the specified amount from one currency to another.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully converted currency."),
            @ApiResponse(responseCode = "404", description = "Invalid request parameters.")
    })
    @GetMapping("/convert/{amount}/{fromCurrency}/{toCurrency}")
    public ResponseEntity<BigDecimal> convertCurrency(@PathVariable BigDecimal amount,
                                                      @PathVariable String fromCurrency,
                                                      @PathVariable String toCurrency) {
        BigDecimal convertedAmount = currencyExchangeRateService.convert(amount, fromCurrency, toCurrency);
        return ResponseEntity.ok(convertedAmount);
    }


    @Operation(summary = "Add new currency", description = "Adds a new currency to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added new currency."),
            @ApiResponse(responseCode = "404", description = "Invalid request body.")
    })
    @PostMapping("/add")
    public ResponseEntity<CurrencyDTO> addCurrency(@RequestBody CurrencyDTO currencyDTO) {
        CurrencyDTO addedCurrency = currencyService.addCurrency(currencyDTO);
        return ResponseEntity.ok(addedCurrency);
    }
}
