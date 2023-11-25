package cz.trailsthroughshadows.api.table.clazz;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.Query;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Class")
public class ClazzModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)//zručení jen setteru
    private Integer id;

    private String name;
    private int baseHealth;



}
