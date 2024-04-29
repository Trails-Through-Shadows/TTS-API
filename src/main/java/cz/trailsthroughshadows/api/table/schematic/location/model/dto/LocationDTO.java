package cz.trailsthroughshadows.api.table.schematic.location.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.text.Description;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.json.LazyFieldsSerializer;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexEnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import jakarta.annotation.Nullable;
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
public class LocationDTO extends Validable {

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
    public List<HexDTO> getMappedStartHexes() {
        if (startHexes == null || parts.size() == 0) {
            return new ArrayList<>();
        }

        List<HexDTO> hexes = new ArrayList<>();
        for (LocationStartDTO startHex : startHexes) {
            for (HexDTO hex : parts.get(0).getPart().getHexes()) {
                hexes.add(hex);
            }
        }

        return hexes;
    }

    @JsonIgnore
    public void loadAll() {
        Initialization.hibernateInitializeAll(this);
    }

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        validateChild(new Title(title), validationConfig);
        validateChild(new Description(description), validationConfig);
        validateChild(new Tag(tag), validationConfig);

        if (parts == null) {
            errors.add(new ValidationError("Location", "parts", null, "Parts must not be null."));
        }
        parts.forEach(part -> validateChild(part, validationConfig));
        if (doors == null) {
            errors.add(new ValidationError("Location", "doors", null, "Doors must not be null."));
        }
        doors.forEach(door -> validateChild(door, validationConfig));
        if (startHexes == null) {
            errors.add(new ValidationError("Location", "startHexes", null, "Start hexes must not be null."));
        }
        startHexes.forEach(startHex -> validateChild(startHex, validationConfig));
        if (enemies == null) {
            errors.add(new ValidationError("Location", "enemies", null, "Enemies must not be null."));
        }
        enemies.forEach(enemy -> validateChild(enemy, validationConfig));
        if (obstacles == null) {
            errors.add(new ValidationError("Location", "obstacles", null, "Obstacles must not be null."));
        }
        obstacles.forEach(obstacle -> validateChild(obstacle, validationConfig));
        if (type == null) {
            errors.add(new ValidationError("Location", "type", null, "Type must not be null."));
        }

        if (parts.isEmpty()) {
            errors.add(new ValidationError("Location", "parts", null, "Parts must not be empty."));
        }
        if (startHexes.size() < 6) {
            errors.add(new ValidationError("Location", "startHexes", startHexes.size(), "Location has to have at least 6 start hexes."));
        }

        // TODO: validate that all parts are connected
    }

    @Override
    public String getValidableValue() {
        return title;
    }
}
