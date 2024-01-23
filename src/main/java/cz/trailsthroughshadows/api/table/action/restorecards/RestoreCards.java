package cz.trailsthroughshadows.api.table.action.restorecards;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.trailsthroughshadows.api.table.effect.Effect;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RestoreCards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RestoreCards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column
    private Integer numCards;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Effect.EffectTarget target;

    @Column
    private Boolean random;

}
