package cz.trailsthroughshadows.api.table.schematic.location;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "LocationStart")
public class LocationStart {

    @EmbeddedId
    private LocationStartId key;

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class LocationStartId implements Serializable {
        @Column(nullable = false)
        private Integer location;
        @Column(nullable = false)
        private Integer hex;
    }

}
