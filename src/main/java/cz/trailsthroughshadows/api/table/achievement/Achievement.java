package cz.trailsthroughshadows.api.table.achievement;

import cz.trailsthroughshadows.api.table.campaign.Campaign;
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
    @Setter(AccessLevel.NONE)//zručení jen setteru
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int xpReward;

}
