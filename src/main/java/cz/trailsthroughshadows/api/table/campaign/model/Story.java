package cz.trailsthroughshadows.api.table.campaign.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "Story")
public class Story {
    @Id
    private Integer id;

    @Column(nullable = false)
    private Integer idCampaignLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StoryTrigger trigger;

    @Column(nullable = false)
    private String story;


    public enum StoryTrigger implements Serializable {
        START, END
    }
}
