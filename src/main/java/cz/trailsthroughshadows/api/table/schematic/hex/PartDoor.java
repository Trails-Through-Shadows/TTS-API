package cz.trailsthroughshadows.api.table.schematic.hex;

import cz.trailsthroughshadows.api.table.schematic.part.Part;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PartDoor")
public class PartDoor {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private PartDoorId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toPart")
    private Part toPart;

    @Data
    @Embeddable
    public static class PartDoorId implements Serializable {
        @Column(nullable = false)
        private int location;

        @Column(nullable = false)
        private int fromPart;

        @Column(nullable = false)
        private int hex;
    }

}
