package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.table.enemy.model.dto.HexEnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.obstacle.HexObstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Location")
public class LocationDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "tag")
    public String tag;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public Location.Type type;

    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<LocationPartDTO> parts;

    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<LocationDoorDTO> doors;

    @JsonIgnore
    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<HexEnemyDTO> enemies;

    @JsonIgnore
    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<HexObstacle> obstacles;

    public List<Part> getParts() {
        if (parts == null) {
            return new ArrayList<>();
        }

        return parts.stream()
                .map(locationPart -> Part.fromDTO(
                        locationPart.getPart(),
                        this,
                        locationPart.getRotation(),
                        enemies.stream()
                                .filter(hexEnemy -> hexEnemy.getKey().getIdPart() == locationPart.getPart().getId())
                                .toList(),
                        obstacles.stream()
                                .filter(hexObstacle -> hexObstacle.getKey().getIdPart() == locationPart.getPart().getId())
                                .toList()
                )).toList();
    }
}
