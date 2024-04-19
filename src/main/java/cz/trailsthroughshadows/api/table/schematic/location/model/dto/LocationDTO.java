package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexEnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Location")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LocationDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(name = "title", nullable = false)
    protected String title;

    @Column(name = "tag", length = 32)
    protected String tag;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    protected Location.Type type;

    @Column(name = "description", columnDefinition = "TEXT")
    protected String description;

    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    protected List<LocationPartDTO> parts;

    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    protected List<LocationDoorDTO> doors;

    @OneToMany(mappedBy = "idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    protected List<LocationStartDTO> startHexes;

    @OneToMany(mappedBy = "idStart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    protected List<LocationPathDTO> paths;

    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    protected List<HexEnemyDTO> enemies;

    @OneToMany(mappedBy = "key.idLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonSerialize(using = LazyFieldsSerializer.class)
    protected List<HexObstacleDTO> obstacles;

    @JsonIgnore
    public List<Part> getMappedParts() {
        if (parts == null)
            return new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        return parts.stream()
                .map(LocationPartDTO::getPart)
                .map(partDTO -> modelMapper.map(partDTO, Part.class))
                .toList();
    }

    @JsonIgnore
    public List<Enemy> getMappedEnemies() {
        if (enemies == null)
            return new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        return enemies.stream()
                .map(HexEnemyDTO::getEnemy)
                .map(enemyDTO -> modelMapper.map(enemyDTO, Enemy.class))
                .toList();
    }

    @JsonIgnore
    public List<Obstacle> getMappedObstacles() {
        if (obstacles == null)
            return new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        return obstacles.stream()
                .map(HexObstacleDTO::getObstacle)
                .map(enemyDTO -> modelMapper.map(enemyDTO, Obstacle.class))
                .toList();
    }

    @JsonIgnore
    public void loadAll() {
        Initialization.hibernateInitializeAll(this);
    }

}
