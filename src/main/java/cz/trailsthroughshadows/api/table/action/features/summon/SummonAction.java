package cz.trailsthroughshadows.api.table.action.features.summon;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.rest.jsonfilter.LazyFieldsFilter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SummonAction")
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SummonAction {

    @EmbeddedId
    private SummonActionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idSummon")
    @JoinColumn(name = "idSummon")
    private Summon summon;

    @Column
    private Integer range;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummonActionId implements Serializable {
        @Column(name = "idSummon")
        private Integer idSummon;
        @Column(name = "idAction")
        private Integer idAction;
    }
}

