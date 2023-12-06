package cz.trailsthroughshadows.api.table.action;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Action")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private Discard discard;

    enum Discard {
        PERNAMENT,
        SHORT_REST,
        LONG_REST,
        NEVER
    }
}
