package my.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDTO {
    private String targetCurrencyCode;
    private BigDecimal rate;
}
