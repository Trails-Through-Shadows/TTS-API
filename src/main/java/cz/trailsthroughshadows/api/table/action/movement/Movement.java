package cz.trailsthroughshadows.api.table.action.movement;


import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.MovementEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "Movement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false)
    private int range;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType type = MovementType.WALK;

    @OneToMany(mappedBy = "idMovement", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<MovementEffect> effects;

    // Skipping n-to-n relationship, there is no additional data in that table
    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(MovementEffect::getEffect).toList();
    }

    enum MovementType {
        WALK,
        JUMP,
    }
}
