package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "LocationStart")
public class LocationStartDTO {

    @EmbeddedId
    private LocationStartId key;

    @Data
    @Embeddable
    @NoArgsConstructor
    public static class LocationStartId implements Serializable {
        @Column(nullable = false)
        private Integer idLocation;

        @Column(nullable = false)
        private Integer idHex;
    }
}

