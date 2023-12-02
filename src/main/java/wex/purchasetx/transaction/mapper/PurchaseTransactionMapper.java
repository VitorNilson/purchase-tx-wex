package wex.purchasetx.transaction.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import wex.purchasetx.transaction.dto.PurchaseTransactionResponseDto;
import wex.purchasetx.transaction.model.PurchaseTransaction;

@Component
@RequiredArgsConstructor
public class PurchaseTransactionMapper {

    private final ModelMapper modelMapper;

    public PurchaseTransactionResponseDto domainToResponseDto(PurchaseTransaction original) {
        return modelMapper.typeMap(PurchaseTransaction.class, PurchaseTransactionResponseDto.class)
                .addMappings(mapper -> mapper.map(PurchaseTransaction::getPurchaseAmount, PurchaseTransactionResponseDto::setOriginalValue))
                .map(original);
    }

}
