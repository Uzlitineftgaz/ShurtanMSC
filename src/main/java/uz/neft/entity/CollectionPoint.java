package uz.neft.entity;

import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.neft.entity.template.AbsEntityInteger;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "collection_point")
@Builder
//@Audited
//@EntityListeners(AuditingEntityListener.class)
public class CollectionPoint extends AbsEntityInteger {


    @NotNull
    private String name;

    @ManyToOne
    private Uppg uppg;

    private String temperatureUnit;
    private String pressureUnit;

    private boolean activeE;

    public CollectionPoint(String name, Uppg uppg, String temperatureUnit, String pressureUnit, OpcServer opcServer) {
        this.name = name;
        this.uppg = uppg;
        this.temperatureUnit = temperatureUnit;
        this.pressureUnit = pressureUnit;
        this.opcServer = opcServer;
    }

    @ManyToOne
    private OpcServer opcServer;

    public String jsonRequestBodyTemperature(){
        if (opcServer!=null){
            return "{\n" +
                    "    \"server\":\""+opcServer.getAddress()+"\",\n" +
                    "    \"unit\":\""+temperatureUnit+"\"\n" +
                    "}";
        }
        return "";
    }

    public String jsonRequestBodyPressure(){
        if (opcServer!=null){
            return "{\r\n    \"server\":\""+opcServer.getAddress()+"\",\r\n    \"unit\":\""+pressureUnit+"\"\r\n}";
        }
        return "";
    }


}
