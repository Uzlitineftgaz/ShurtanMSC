package uz.neft.dto.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uz.neft.dto.Dto;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionPointActionDto implements Dto {

    private Long actionId;

    // Rasxod
    private double expend;

    // Bosim
    private double pressure;

    // Tempratura
    private double temperature;


    private Integer collectionPointId;

    private Date createdAt;

    private boolean door;

//    @LastModifiedBy
//    @Column(nullable = false)
//    private String modifiedBy;
//
//    @LastModifiedDate
//    @Column(nullable = false)
    private Date modified;

}
