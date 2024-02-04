package cz.trailsthroughshadows.algorithm.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class Entity {

//    @Transient
//    public Hex hex;
//
//    @Transient
//    public List<Effect> activeEffects = new ArrayList<>();

    public abstract String getTitle();

}
