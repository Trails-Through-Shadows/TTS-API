package cz.trailsthroughshadows.api.table.background.clazz;

import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.effect.forcharacter.ClassEffect;
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

    @Column(nullable = false, length = 50)
    private String name;

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
    private Collection<ClassAction> actions;

    @ToString.Include(name = "actions")
    public Collection<Action> getActions() {
        if (actions == null) return null;
        return actions.stream().map(ClassAction::getAction).toList();
    }
}
