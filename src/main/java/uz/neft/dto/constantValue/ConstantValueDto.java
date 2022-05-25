package uz.neft.dto.constantValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConstantValueDto {
    private Integer Id;

    private Double value;

    private Integer constantId;

    private Integer miningSystemId;
    private Integer uppgId;
    private Integer collectionPointId;
    private Integer wellId;

}
