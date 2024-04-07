package cz.trailsthroughshadows.api.table.effect.relation.foraction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SkillEffect")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SkillEffect {

    @EmbeddedId
    private SkillEffectId key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    private EffectDTO effect;

    @Data
    @Embeddable
    public static class SkillEffectId implements Serializable {
        @Column(nullable = false)
        private Integer idSkill;

        @Column(nullable = false)
        private Integer idEffect;
    }

}