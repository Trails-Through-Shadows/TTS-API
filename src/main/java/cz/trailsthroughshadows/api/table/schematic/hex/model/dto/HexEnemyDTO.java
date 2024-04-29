package cz.trailsthroughshadows.api.table.schematic.hex.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "HexEnemy")
public class HexEnemyDTO extends Validable {

    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private HexEnemyId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idEnemy", insertable = false, updatable = false)
    private EnemyDTO enemy;

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        if (key == null) {
            errors.add(new ValidationError("HexEnemy", "key", null, "Key must not be null."));
        }
        if (!errors.isEmpty()) { return; }
        if (key.idEnemy == null) {
            errors.add(new ValidationError("HexEnemy", "key.idEnemy", null, "Enemy ID must not be null."));
        }
        if (key.idHex == null) {
            errors.add(new ValidationError("HexEnemy", "key.idHex", null, "Hex ID must not be null."));
        }
        if (key.idLocation == null) {
            errors.add(new ValidationError("HexEnemy", "key.idLocation", null, "Location ID must not be null."));
        }
        if (key.idPart == null) {
            errors.add(new ValidationError("HexEnemy", "key.idPart", null, "Part ID must not be null."));
        }
        if (enemy == null) {
            errors.add(new ValidationError("HexEnemy", "enemy", null, "Enemy must not be null."));
        }
        validateChild(enemy, validationConfig);
        if (!errors.isEmpty()) { return; }
        if (enemy.getId() == null) {
            errors.add(new ValidationError("HexEnemy", "enemy.id", null, "Enemy ID must not be null."));
        }
    }

    @Override
    public String getValidableValue() {
        return null;
    }

    @Embeddable
    @Data
    public static class HexEnemyId implements Serializable {

        @Column(name = "idEnemy", nullable = false)
        private Integer idEnemy;

        @Column(name = "idHex", nullable = false)
        private Integer idHex;

        @Column(name = "idLocation", nullable = false)
        private Integer idLocation;

        @Column(name = "idPart", nullable = false)
        private Integer idPart;

    }
}
