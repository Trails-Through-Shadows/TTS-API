package cz.trailsthroughshadows.api.table.enemy.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EnemyAction")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EnemyActionDTO {

    @EmbeddedId
    private EnemyActionId key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private ActionDTO action;

    @Embeddable
    @Data
    public static class EnemyActionId implements Serializable {
        @Column(nullable = false)
        private Integer idEnemy;

        @Column(nullable = false)
        private Integer idAction;
    }
}
