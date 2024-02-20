package cz.trailsthroughshadows.api.table.market.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.ItemEffect;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ItemDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "key.idItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private Collection<ItemEffect> effects;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idAction")
    private ActionDTO action;

    @JsonIgnore
    public Collection<EffectDTO> getMappedEffects() {
        if (effects == null) return null;
        return effects.stream().map(ItemEffect::getEffect).toList();
    }

    //region Validation

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        // Title, tag and description have to be valid.
        validateChild(new Title(title), validationConfig);
        validateChild(new Tag(tag), validationConfig);
        validateChild(new Description(description), validationConfig);

        // Action has to be validated, if it is not null.
        if (action != null) {
            validateChild(action, validationConfig);
        }

        // All effects must be validated.
        for (var e : effects) {
            validateChild(e.getEffect(), validationConfig);
        }
    }

    @Override
    public String getValidableValue() {
        return getTitle() + " (" + getType().name() + ")";
    }

    //endregion


    public enum ItemType {
        WEAPON, HELMET, CHESTPLATE, LEGGINGS, BOOTS, ACCESSORY, CONSUMABLE,
    }
}
