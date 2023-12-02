package wex.purhcasetx.exchangerate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import wex.purhcasetx.common.serializer.ToLowerCaseDeserializer;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "tb_exchange_rate")
public class ExchangeRate implements Persistable<String> {

    @Id
    @JsonDeserialize(converter = ToLowerCaseDeserializer.class)
    private String country;

    @JsonDeserialize(converter = ToLowerCaseDeserializer.class)
    private String currency;

    @JsonProperty("exchange_rate")
    private BigDecimal rate;

    @JsonProperty("effective_date")
    private LocalDate effectiveDate;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public String getId() {
        return this.country;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
