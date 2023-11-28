package cz.trailsthroughshadows.api.table.character.clazz;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Class")
public class Clazz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)//zručení jen setteru
    private Integer id;

    @Column
    private String name;

    @Column
    private int baseHealth;

}
