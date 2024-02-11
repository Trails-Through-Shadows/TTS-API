package cz.trailsthroughshadows.api.table.market.item;

import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.ItemEffect;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Entity
@Table(name = "Item")
@Data
@NoArgsConstructor
public class Item {

    @Id
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(nullable = false, length = 128)
    private String title;

    @Column
    private String description;

    @Column(length = 32)
    private String tag;

    @Column(nullable = false)
    private String requirements;

    @OneToMany
    @JoinColumn(name = "idItem")
    private Collection<ItemEffect> effects;

    @ManyToOne
    @JoinColumn(name = "idAction")
    private ActionDTO action;

    @ToString.Include(name = "effects")
    public Collection<EffectDTO> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(ItemEffect::getEffect).toList();
    }


    public enum ItemType {
        WEAPON, HELMET, CHESTPLATE, LEGGINGS, BOOTS, ACCESSORY, CONSUMABLE,
    }

}
