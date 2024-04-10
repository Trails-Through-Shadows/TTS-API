package cz.trailsthroughshadows.api.table.background.clazz;


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
@Table(name = "ClassAction")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClazzAction {

    @EmbeddedId
    private ClazzActionId key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private ActionDTO action;

    @Data
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClazzActionId implements Serializable {
        @Column(nullable = false)
        private Integer idClass;

        @Column(nullable = false)
        private Integer idAction;
    }

}
