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
@Table(name = "LocationPath")
@IdClass(LocationPathDTO.class)
public class LocationPathDTO implements Serializable {

    @Id
    private Integer idCampaign;

    @Id
    private Integer idStart;

    @Id
    private Integer idEnd;

}

