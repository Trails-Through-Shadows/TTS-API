package cz.trailsthroughshadows.api.table.background.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.jsonfilter.LazyFieldsFilter;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.relation.forcharacter.RaceEffect;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Race")
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    public Integer id;

    @Column(nullable = false, length = 128)
    public String title;
    @Column(length = 32)
    private String tag;
    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    public int baseInitiative;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "idRace")
    public Collection<RaceEffect> effects;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "idRace")
    @ToString.Exclude
    public Collection<RaceAction> actions;

    @ToString.Include(name = "actions")
    public Collection<ActionDTO> getActions() {
        if (actions == null) return null;
        return actions.stream().map(RaceAction::getAction).collect(Collectors.toList());
    }
}
