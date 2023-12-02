package wex.purhcasetx.exchangerate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeApiResponseMetaDto {
    private Long count;
    @JsonProperty("total-count")
    private Long totalCount;
    @JsonProperty("total-pages")
    private Long totalPages;
}
