package uz.neft.entity.action;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import uz.neft.entity.User;
import uz.neft.entity.Well;
import uz.neft.entity.enums.WellStatus;
import uz.neft.entity.template.AbsEntityLong;
import uz.neft.entity.variables.*;
import uz.neft.repository.CollectionPointRepository;
import uz.neft.repository.MiningSystemGasCompositionRepository;
import uz.neft.repository.UserRepository;
import uz.neft.repository.WellRepository;
import uz.neft.repository.action.WellActionRepository;
import uz.neft.repository.constants.ConstantRepository;
import uz.neft.repository.constants.MiningSystemConstantRepository;
import uz.neft.service.Calculator;
import uz.neft.utils.Converter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WellAction extends AbsEntityLong {
    // Bosim
    private double pressure;

    // Tempratura
    private double temperature;

    //Sredniy rasxod D_skv
    private double average_expend;

    // Rasxod Q
    private double expend;

    // Rpl
    private double rpl;


    //Р_ПКР
    private double P_pkr;


    //T_ПКР
    private double T_pkr;


    //P_pr
    private double P_pr;

    //T_pr
    private double T_pr;

    //Относительная плотность газа ( ρ_отн )
    private double Ro_otn;


    //Z - Коэффициент сверхсжимаемости газа
    private double Z;


    //delta - Поправочный коэффициент, зависящий от приведенных давления и температуры (δ)
    private double delta;


    //Шайбный коэффициент расхода, С
    private double C;


    //ρ_газа – плотность газа при стандартных условиях
    private double ro_gas;


    //ρ_возд – плотность воздуха при стандартных условиях
    private double ro_air;




    @Enumerated(EnumType.STRING)
    private WellStatus status;

    @ManyToOne
    private User user;

    @ManyToOne
    private Well well;

    private int perforation_min;
    private int perforation_max;




//    @LastModifiedBy
//    @Column(nullable = false)
//    private String modifiedBy;
//
//    @LastModifiedDate
//    @Column(nullable = false)
//    private LocalDateTime modified;

}
