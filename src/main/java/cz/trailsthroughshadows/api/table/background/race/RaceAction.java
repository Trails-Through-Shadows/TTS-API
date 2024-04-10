package cz.trailsthroughshadows.api.table.background.race;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RaceAction")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RaceAction {

    @EmbeddedId
    private RaceActionId key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private ActionDTO action;

    @Data
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RaceActionId implements Serializable {
        @Column(nullable = false)
        private int idRace;

        @Column(nullable = false)
        private int idAction;
    }

}
