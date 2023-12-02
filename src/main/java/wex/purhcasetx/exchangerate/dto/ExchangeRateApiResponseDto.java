package wex.purhcasetx.exchangerate.dto;

import wex.purhcasetx.exchangerate.model.ExchangeRate;

import java.util.List;

public record ExchangeRateApiResponseDto(List<ExchangeRate> data, ExchangeApiResponseMetaDto meta)  {
}
