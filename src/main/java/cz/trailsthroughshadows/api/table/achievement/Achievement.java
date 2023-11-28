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
    @Setter(AccessLevel.NONE)//zručení jen setteru
    private Integer id;

    private String title;
    private String description;
    private int xpReward;
    private int progress;
    private boolean claimed;

    // make foreign key
    //
    @ManyToOne
    @JoinColumn(name = "idCampaign", referencedColumnName = "id")
    private Integer campaignId; //mby class CampaignModel



    //get all


}
