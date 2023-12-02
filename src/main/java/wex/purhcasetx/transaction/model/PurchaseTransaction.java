package wex.purhcasetx.transaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_purchase_transaction")
public class PurchaseTransaction {

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column("transaction_id")
    private UUID transactionId;

    @Size(max = 50, message = "Exceeded Max lenght of 50 characters.")
    private String description;

    @NotNull
    @Column("transaction_amount")
    private BigDecimal purchaseAmount;

    @NotNull
    @Column("transaction_date")
    private ZonedDateTime transactionDate;
}
