package cz.trailsthroughshadows.algorithm.encounter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class EncounterSerializer extends JsonSerializer<Encounter> {

    @Override
    public void serialize(Encounter value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeNumberField("idLocation", value.getLocation().getId());

        List<Integer> partIds = value.getLocation().getMappedParts().stream().map(PartDTO::getId).toList();
        gen.writeFieldName("idParts");
        gen.writeArray(partIds.stream().mapToInt(i -> i).toArray(), 0, partIds.size());

        List<Enemy> enemies = value.getLocation().getMappedEnemies();
        gen.writeFieldName("enemies");
        gen.writeStartArray();
        enemies.forEach(e -> {
            try {
                gen.writeObject(e);
            } catch (IOException e1) {
                log.error("Error writing enemy", e1);
            }
        });
        gen.writeEndArray();
        //TODO summons form enemies and players


        List<Obstacle> obstacles = value.getLocation().getMappedObstacles();
        gen.writeFieldName("obstacles");
        gen.writeStartArray();
        obstacles.forEach(e -> {
            try {
                gen.writeObject(e);
            } catch (IOException e1) {
                log.error("Error writing Obstacle", e1);
            }
        });
        gen.writeEndArray();

        gen.writeObjectField("state", value.getState());

        //gen.writeObject(value.getLocation().getMappedObstacles());

        gen.writeEndObject();
    }
}
