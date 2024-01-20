package cz.trailsthroughshadows.api.table.schematic.path;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Path")
public class Path {

    @EmbeddedId
    private PathId key;
    @Embeddable
    @Data
    public static class PathId {
        @Column(nullable = false)
        private Integer idCampaign;
        @Column(nullable = false)
        private Integer idStart;
        @Column(nullable = false)
        private Integer idEnd;
    }

}
