package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LocationPart")
public class LocationPartDTO {

    @EmbeddedId
    private LocationPartId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idPart", insertable = false, updatable = false)
    private PartDTO part;

    @Column(nullable = false)
    private int rotation;

    @Embeddable
    @Data
    public static class LocationPartId implements Serializable {

        @Column(name = "idLocation")
        private Integer idLocation;

        @Column(name = "idPart")
        private Integer idPart;

    }
}
