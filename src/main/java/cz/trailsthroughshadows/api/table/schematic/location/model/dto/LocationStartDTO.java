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
@Table(name = "LocationStart")
@IdClass(LocationStartDTO.class)
public class LocationStartDTO implements Serializable {

    @Id
    private Integer idLocation;

    @Id
    private Integer idPart;

    @Id
    private Integer idHex;
}

