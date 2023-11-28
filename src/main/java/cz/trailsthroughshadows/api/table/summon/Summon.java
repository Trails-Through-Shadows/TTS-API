package cz.trailsthroughshadows.api.table.summon;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Summon")
public class Summon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)//zručení jen setteru
    private Integer id;

    private String name;
    private int duration;
    private int health;




}
