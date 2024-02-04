package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
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

    @Id
    private int idHex;

}