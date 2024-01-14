package cz.trailsthroughshadows.api.table.equipment.item;

import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.forothers.ItemEffect;
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

    @Column
    private String description;

    @Column(nullable = false)
    private String requirements;

    @OneToMany
    @JoinColumn(name = "idItem")
    private Collection<ItemEffect> effects;

    @ToString.Include(name = "effects") // Including replacement field in toString
    public Collection<Effect> getEffects() {
        if (effects == null) return null;
        return effects.stream().map(ItemEffect::getEffect).toList();
    }


    enum ItemType {
        WEAPON, POTION, HELMET, CHESTPLATE, LEGGINGS, BOOTS, ACCESSORY, SHIELD, SCROLL, WAND, STAFF, BOOK, CONSUMABLE, TOOL, MISC
    }

}
