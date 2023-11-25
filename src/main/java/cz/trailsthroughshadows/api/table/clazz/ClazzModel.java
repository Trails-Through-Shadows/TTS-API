package cz.trailsthroughshadows.api.table.clazz;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Class")
public class ClazzModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter(AccessLevel.NONE)//zručení jen getteru
    private Integer id;

    private String name;
    private int baseHealth;



}
