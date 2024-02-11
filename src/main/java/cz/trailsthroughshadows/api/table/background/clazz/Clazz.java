package cz.trailsthroughshadows.api.table.background.clazz;

import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.ClassEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Class")
public class Clazz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(length = 32)
    private String tag;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private int baseHealth;

    @Column(nullable = false)
    private int baseDefence;

    @Column(nullable = false)
    private int baseInitiative;

    @OneToMany
    @JoinColumn(name = "idClass")
    private Collection<ClassEffect> effects;

    @OneToMany
    @JoinColumn(name = "idClass")
    private Collection<ClazzAction> actions;

    @ToString.Include(name = "actions")
    public Collection<ActionDTO> getActions() {
        if (actions == null) return null;
        return actions.stream().map(ClazzAction::getAction).toList();
    }
}
