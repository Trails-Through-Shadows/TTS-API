package cz.trailsthroughshadows.api.table.effect;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


/**
 * TODO table with foreign keys and they are primary too
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Effect")
public class SummonEffect {


    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private SummonEffectKey key;

    public class SummonEffectKey implements Serializable {
        @Column(nullable = false)
        private int idSummon;
        @Column(nullable = false)
        private int idEffect;
    }
}
