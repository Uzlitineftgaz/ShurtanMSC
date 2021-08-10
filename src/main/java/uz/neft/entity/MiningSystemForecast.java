package uz.neft.entity;

import lombok.*;
import uz.neft.entity.template.AbsEntityInteger;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.Month;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"year", "month", "mining_system_id"})})
public class MiningSystemForecast extends AbsEntityInteger {
    private int year;
    private Month month;
    private String expandGas;
    @ManyToOne
    private MiningSystem miningSystem;
}
