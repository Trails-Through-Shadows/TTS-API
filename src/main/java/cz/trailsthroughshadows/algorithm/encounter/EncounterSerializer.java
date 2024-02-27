package cz.trailsthroughshadows.algorithm.encounter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEffect;
import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEntity;
import cz.trailsthroughshadows.api.table.action.features.summon.model.Summon;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
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

        //ids of unlocked parts
        List<Integer> partIds = value.getParts().stream().filter(Part::getUnlocked).map(Part::getId).toList();
        gen.writeFieldName("idParts");
        gen.writeArray(partIds.stream().mapToInt(i -> i).toArray(), 0, partIds.size());

        // enemies
        gen.writeFieldName("enemies");
        gen.writeStartArray();
        value.getEntities().getEnemies().forEach(e -> {
            try {
                serialize(e, gen, serializers);
            } catch (IOException ex) {
                log.error("Error writing enemy", ex);
            }
        });
        gen.writeEndArray();
        //TODO summons form enemies and players

        // players
        gen.writeFieldName("characters");
        gen.writeStartArray();
        value.getEntities().getCharacters().forEach(c -> {
            try {
                serialize(c, gen, serializers);
            } catch (IOException ex) {
                log.error("Error writing character", ex);
            }
        });
        gen.writeEndArray();

        // summons
        gen.writeFieldName("summons");
        gen.writeStartArray();
        value.getEntities().getSummons().forEach(s -> {
            try {
                serialize(s, gen, serializers);
            } catch (IOException ex) {
                log.error("Error writing summon", ex);
            }
        });
        gen.writeEndArray();

        // obstacles
        gen.writeFieldName("obstacles");
        gen.writeStartArray();
        value.getEntities().getObstacles().forEach(o -> {
            try {
                serialize(o, gen, serializers);
            } catch (IOException e) {
                log.error("Error writing obstacle", e);
            }
        });
        gen.writeEndArray();

        gen.writeObjectField("state", value.getState());

        gen.writeEndObject();
    }

    public void serialize(EncounterEntity<?> entity, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeNumberField("id", entity.getId());
        if (entity.getType() != EncounterEntity.EntityType.CHARACTER) {
            gen.writeNumberField("idGroup", entity.getIdGroup());
        }

        switch (entity.getType()) {
            case CHARACTER:
                Character character = (Character) entity.getEntity();
                gen.writeStringField("title", character.getTitle());
                gen.writeStringField("playerName", character.getPlayerName());
                break;
            case ENEMY:
                Enemy enemy = (Enemy) entity.getEntity();
                gen.writeStringField("title", enemy.getTitle());
                break;
            case SUMMON:
                Summon summon = (Summon) entity.getEntity();
                gen.writeStringField("title", summon.getTitle());
                break;
            case OBSTACLE:
                Obstacle obstacle = (Obstacle) entity.getEntity();
                gen.writeStringField("title", obstacle.getTitle());
                break;
        }

        gen.writeNumberField("health", entity.getHealth());
        if (entity.getType() == EncounterEntity.EntityType.CHARACTER || entity.getType() == EncounterEntity.EntityType.ENEMY) {
            gen.writeNumberField("defence", entity.getDefence());
        }
        if (entity.getType() == EncounterEntity.EntityType.CHARACTER) {
            Character character = (Character) entity.getEntity();
            gen.writeNumberField("baseInitiative", character.getInitiative());
        }

        gen.writeFieldName("activeEffects");
        gen.writeStartArray();
        for (EncounterEffect effect : entity.getEffects()) {
            try {
                gen.writeObject(effect);
            } catch (IOException ex) {
                log.error("Error writing active effect", ex);
            }
        }
        gen.writeEndArray();

        // todo more fields
        switch (entity.getType()) {
            case CHARACTER:
                Character character = (Character) entity.getEntity();
                gen.writeStringField("url", character.getUrl());
                break;
            case ENEMY:
                Enemy enemy = (Enemy) entity.getEntity();
                gen.writeStringField("url", enemy.getUrl());
                gen.writeObjectField("startingHex", enemy.getHex());
                break;
            case SUMMON:
                Summon summon = (Summon) entity.getEntity();
                gen.writeStringField("url", summon.getUrl());
                break;
            case OBSTACLE:
                Obstacle obstacle = (Obstacle) entity.getEntity();
                gen.writeStringField("url", obstacle.getUrl());
                gen.writeObjectField("startingHex", obstacle.getHex());
                break;
        }

        gen.writeEndObject();
    }
}
