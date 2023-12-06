package cz.trailsthroughshadows.api.table.effect;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Effect")
public class Effect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column
    @Enumerated(EnumType.STRING)
    private EffectRange range;

    @Column
    private int duration;

    @Column
    private Integer strength;

    public enum EffectType {
        PUSH,
        PULL,
        FORCED_MOVEMENT_IMMUNITY,
        POISON, POISON_IMMUNITY,
        FIRE, FIRE_IMMUNITY, BLEED, BLEED_IMMUNITY,
        DISARM, DISARM_IMMUNITY, STUN, STUN_IMMUNITY,
        CONFUSION, CONFUSION_IMMUNITY, CHARM, CHARM_IMMUNITY,
        FEAR, FEAR_IMMUNITY, INVISIBILITY, SHIELD, BONUS_HEALTH,
        BONUS_DAMAGE, BONUS_MOVEMENT
    }

    public enum EffectRange {
        SELF,
        ENEMY,
        ALLY,
        ALL
    }
}
