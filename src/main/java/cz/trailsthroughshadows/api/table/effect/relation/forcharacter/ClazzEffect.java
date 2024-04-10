package cz.trailsthroughshadows.api.table.effect.relation.forcharacter;

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
@Table(name = "ClassEffect")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClazzEffect {

    @EmbeddedId
    private ClazzEffectId key;

    @Column
    private Integer levelReq;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idEffect", insertable = false, updatable = false)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    private EffectDTO effect;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClazzEffectId implements Serializable {
        @Column(nullable = false)
        private Integer idClass;

        @Column(nullable = false)
        private Integer idEffect;
    }

}