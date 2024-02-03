package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.schematic.location.model.LocationDTO;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "CampaignLocation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignLocation {

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private CampaignLocationId id;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "idCampaign", insertable = false, updatable = false)
//    private Campaign campaign;

    @ManyToOne//(fetch = FetchType.LAZY) // todo bulk get or lazy load or dont map by default and only return ID
    @JoinColumn(name = "idLocation", insertable = false, updatable = false)
    private LocationDTO location;

    @Column(nullable = false)
    private boolean start;

    @Column(nullable = false)
    private boolean finish;

    @Column(nullable = false)
    private String winCondition;


    @Embeddable
    @Data
    public static class CampaignLocationId implements Serializable {
        @Column(nullable = false)
        private int idCampaign;

        @Column(nullable = false)
        private int idLocation;
    }
}

