package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import jakarta.persistence.*;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idHex", insertable = false, updatable = false)
    @JoinColumn(name = "idPart", insertable = false, updatable = false)
    private HexDTO hex;

}

