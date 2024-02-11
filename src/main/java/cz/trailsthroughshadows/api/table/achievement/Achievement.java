package cz.trailsthroughshadows.api.table.achievement;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Achievement")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) //zrušení jen setteru
    private Integer id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int xpReward;

}
