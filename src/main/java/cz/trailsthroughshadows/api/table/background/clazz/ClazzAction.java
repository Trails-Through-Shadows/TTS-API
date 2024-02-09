package cz.trailsthroughshadows.api.table.background.clazz;


import cz.trailsthroughshadows.api.table.action.Action;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ClassAction")
@Data
@NoArgsConstructor
public class ClazzAction {

    @Id
    @Column(nullable = false)
    private int idClass;

    @Id
    @Column(nullable = false)
    private int idAction;

    @ManyToOne
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private Action action;


}
