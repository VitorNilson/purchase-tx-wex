package wex.purhcasetx.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseTransactionResponseDto {

    private UUID transactionId;
    private String description;
    private BigDecimal originalValue;
    private BigDecimal convertedValue;
    private ZonedDateTime transactionDate;
    private BigDecimal usedRate;
    private String countryName;

}
