package cz.trailsthroughshadows.api.table.character.clazz;


import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.market.item.Item;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ClassAction")
@Data
@NoArgsConstructor
public class ClassAction {
    @Id
    private Integer id;
    @Column(nullable = false)
    private int idClass;
    @Column
    private Integer levelReq;

    @ManyToOne
    @JoinColumn(name = "idAction")
    private Action action;

    @ManyToOne
    @JoinColumn(name = "itemReq")
    private Item item;

}
