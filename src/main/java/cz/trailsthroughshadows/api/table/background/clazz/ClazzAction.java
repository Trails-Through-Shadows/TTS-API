package cz.trailsthroughshadows.api.table.background.clazz;


import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "ClassAction")
@Data
@NoArgsConstructor
public class ClazzAction {

    @EmbeddedId
    private ClazzActionId key;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idAction", insertable = false, updatable = false)
    private ActionDTO action;

    @Data
    public static class ClazzActionId implements Serializable {
        @Column(nullable = false)
        private Integer idClass;

        @Column(nullable = false)
        private Integer idAction;
    }

}
