package cz.trailsthroughshadows.api.table.campaign.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "CampaignLocation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CampaignLocation {

    @EmbeddedId
    private CampaignLocationId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idLocation", insertable = false, updatable = false)
    private LocationDTO location;

    @Column(nullable = false)
    private Boolean start;

    @Column(nullable = false)
    private Boolean finish;

    @Column(nullable = false, name = "winCondition")
    private String winConditionString;

    @Transient
    private WinCondition winCondition;

    @PostLoad
    private void postLoad() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = winConditionString.replaceAll("\\\\", "");
            winCondition = mapper.readValue(tmp, WinCondition.class);
        } catch (Exception e) {
            winCondition = null;
        }
    }

    @JsonIgnore
    public void setWinConditionString(String winConditionString) {
        this.winConditionString = winConditionString;
        postLoad();
    }

    @JsonIgnore
    public String getWinConditionString() {
        return winConditionString;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinCondition implements Serializable {
        private String type;

        private String value;
    }

    @Embeddable
    @Data
    public static class CampaignLocationId implements Serializable {
        @Column(nullable = false)
        private Integer idCampaign;

        @Column(nullable = false)
        private Integer idLocation;
    }
}

