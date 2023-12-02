package wex.purchasetx.exchangerate.dto;

import wex.purchasetx.exchangerate.model.ExchangeRate;

import java.util.List;

public record ExchangeRateApiResponseDto(List<ExchangeRate> data, ExchangeApiResponseMetaDto meta)  {
}
