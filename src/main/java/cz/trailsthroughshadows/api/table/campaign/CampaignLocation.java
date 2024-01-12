package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.schematic.location.Location;
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

    @Column(nullable = false)
    private boolean unlocked;

    @Column(nullable = false)
    private Integer timesVisited;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "idCampaign", insertable = false, updatable = false)
//    private Campaign campaign;

    @ManyToOne//(fetch = FetchType.LAZY) // todo bulk get or lazy load or dont map by default and only return ID
    @JoinColumn(name = "idLocation", insertable = false, updatable = false)
    private Location location;

    @Embeddable
    @Data
    public static class CampaignLocationId implements Serializable {
        @Column(nullable = false)
        private int idCampaign;

        @Column(nullable = false)
        private int idLocation;
    }
}

