package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "LocationDoor")
@IdClass(LocationDoorDTO.class)
public class LocationDoorDTO implements Serializable {

    @Id
    private int idLocation;

    @Id
    private int idPartFrom;

    @Id
    private int idPartTo;


    @Column(nullable = false)
    private Integer qCoord;

    @Column(nullable = false)
    private Integer rCoord;

    @Column(nullable = false)
    private Integer sCoord;

}