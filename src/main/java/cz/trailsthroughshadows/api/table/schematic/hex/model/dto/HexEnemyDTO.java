package cz.trailsthroughshadows.api.table.schematic.hex.model.dto;

import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "HexEnemy")
public class HexEnemyDTO {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private HexEnemyId key;

    @ManyToOne
    @JoinColumn(name = "idEnemy", insertable = false, updatable = false)
    private EnemyDTO enemy;

    @Getter
    @Embeddable
    public static class HexEnemyId implements Serializable {

        @Column(name = "idEnemy", nullable = false)
        private int idEnemy;

        @Column(name = "idHex", nullable = false)
        private int idHex;

        @Column(name = "idLocation", nullable = false)
        private int idLocation;

        @Column(name = "idPart", nullable = false)
        private int idPart;

    }
}
