package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.schematic.location.Location;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "CampaignLocation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignLocation {

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private CampaignLocationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCampaign", insertable = false, updatable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idLocation", insertable = false, updatable = false)
    private Location location;

    @Embeddable
    public class CampaignLocationId implements Serializable {
        @Column(nullable = false)
        private int idCampaign;

        @Column(nullable = false)
        private int idLocation;
    }
}

