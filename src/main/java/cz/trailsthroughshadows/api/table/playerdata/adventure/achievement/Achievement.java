package cz.trailsthroughshadows.api.table.playerdata.adventure.achievement;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Achievement extends AchievementDTO {

    public static Achievement fromDTO(AchievementDTO dto) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(dto, Achievement.class);
    }
}
