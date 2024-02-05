package cz.trailsthroughshadows.api.table.enemy.model.dto;

import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.jsonfilter.LazyFieldsFilter;
import cz.trailsthroughshadows.api.table.action.Action;
import cz.trailsthroughshadows.api.table.effect.Effect;
import cz.trailsthroughshadows.api.table.effect.forothers.EnemyEffect;
import jakarta.annotation.Nullable;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Enemy")
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EnemyDTO extends Validable implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false)
    private int baseHealth;

    @Column(nullable = false)
    private int baseDefence;

    @Column(length = 32)
    private String tag;

    @Column
    private Integer usages;

    @OneToMany(mappedBy = "idEnemy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnemyEffect> effects;

    @OneToMany(mappedBy = "idEnemy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnemyActionDTO> actions;

    public List<Effect> getEffects() {
        if (effects == null) return new ArrayList<>();
        return effects.stream().map(EnemyEffect::getEffect).toList();
    }

    public List<Action> getActions() {
        if (actions == null) return new ArrayList<>();
        return actions.stream().map(EnemyActionDTO::getAction).toList();
    }


    public void loadAll() throws Exception {
        Initialization init = new Initialization();
        init.initializeAndUnproxy(this);
    }


    @Override
    public EnemyDTO clone() {
        EnemyDTO enemy = new EnemyDTO();

        enemy.setId(this.getId());
        enemy.setTitle(this.getTitle());
        enemy.setBaseDefence(this.getBaseDefence());
        enemy.setBaseHealth(this.getBaseHealth());

        return enemy;
    }

    //region Validation
    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        Title title = new Title(getTitle());
        title.validate(validationConfig);
    }

    @Override
    public String getValidableValue() {
        return getTitle();
    }
    //endregion
}
