package cz.trailsthroughshadows.algorithm.encounter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEntity;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class EncounterSerializer extends JsonSerializer<Encounter> {

    @Override
    public void serialize(Encounter value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeNumberField("idLocation", value.getLocation().getId());

        //ids of unlocked parts
        List<Integer> partIds = value.getLocation().getMappedParts().stream()
                .filter(Part::isUnlocked)
                .map(Part::getId)
                .toList();
        gen.writeFieldName("idParts");
        gen.writeArray(partIds.stream().mapToInt(i -> i).toArray(), 0, partIds.size());

        // enemies
        gen.writeFieldName("enemies");
        gen.writeStartArray();
        value.getEnemies().forEach(e -> {
            try {
//                gen.writeObject(e);



            } catch (IOException e1) {
                log.error("Error writing enemy", e1);
            }
        });
        gen.writeEndArray();
        //TODO summons form enemies and players

        // players
        gen.writeFieldName("characters");
        gen.writeStartArray();
        value.getCharacters().forEach(e -> {
            try {
                gen.writeObject(e);
            } catch (IOException e1) {
                log.error("Error writing character", e1);
            }
        });
        gen.writeEndArray();

        // summons
        gen.writeFieldName("summons");
        gen.writeStartArray();
        value.getSummons().forEach(e -> {
            try {
                gen.writeObject(e);
            } catch (IOException e1) {
                log.error("Error writing summon", e1);
            }
        });
        gen.writeEndArray();

        // obstacles
        gen.writeFieldName("obstacles");
        gen.writeStartArray();
        value.getObstacles().forEach(o -> {
            try {
                gen.writeObject(o);
            } catch (IOException e) {
                log.error("Error writing obstacle", e);
            }
        });
        gen.writeEndArray();

        gen.writeObjectField("state", value.getState());

        gen.writeEndObject();
    }

    public void serialize(EncounterEntity entity, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeNumberField("id", entity.getId());
        gen.writeNumberField("idGroup", -1);
        gen.writeNumberField("initiative", entity.getInitiative());
        gen.writeNumberField("health", entity.getHealth());

        gen.writeFieldName("activeEffects");
        gen.writeStartArray();
        for (Map.Entry<Effect, Integer> entry : entity.getActiveEffects().entrySet()) {
            try {
                gen.writeStartObject();
                gen.writeObjectField("effect", entry.getKey());
                gen.writeNumberField("duration", entry.getValue());
                gen.writeEndObject();
            } catch (IOException e1) {
                log.error("Error writing active effect", e1);
            }
        }
//        entity.getActiveEffects().for((effect, duration) -> {
//            try {
//                gen.writeStartObject();
//                gen.writeObjectField("effect", effect);
//                gen.writeNumberField("duration", duration.);
//                gen.writeEndObject();
//            } catch (IOException e1) {
//                log.error("Error writing active effect", e1);
//            }
//        });
        gen.writeEndArray();
//
//        // todo more fields
//        gen.writeObjectField("startingHex", entity.getEntity().getHex());
//        gen.writeStringField("url", entity.getEntity().getUrl());
//
        gen.writeEndObject();
    }
}
