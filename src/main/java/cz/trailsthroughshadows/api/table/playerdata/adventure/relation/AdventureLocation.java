package cz.trailsthroughshadows.api.table.playerdata.adventure.relation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`AdventureLocation`")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AdventureLocation {

    @EmbeddedId
    private AdventureLocationId key;

    @Column(nullable = false)
    private boolean unlocked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationState state;

    public enum LocationState {
        NOT_VISITED, VISITED, FAILED, COMPLETED,
    }

    // This is not good idea bcs that maps all database
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "idLocation", insertable = false, updatable = false)
    // @JsonSerialize(using = LazyFieldsSerializer.class)
    // private LocationDTO location;

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class AdventureLocationId implements Serializable {
        @Column(nullable = false)
        private Integer idAdventure;

        @Column(nullable = false)
        private Integer idLocation;
    }

}
