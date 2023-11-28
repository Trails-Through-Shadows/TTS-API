package cz.trailsthroughshadows.api.table.campaign;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "idLicense", nullable = false)
    private int idLicense;

    @Column(name = "currentLocation", nullable = false)
    private int currentLocation;

    @Column(name = "reputation", nullable = false)
    private int reputation;

    @Column(name = "partyXp", nullable = false)
    private int partyXp;


}
