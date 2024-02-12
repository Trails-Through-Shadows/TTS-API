package cz.trailsthroughshadows.api.table.campaign;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
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

    @JsonSerialize(using = LazyFieldsSerializer.class)
    @ManyToOne(fetch = FetchType.LAZY)
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

