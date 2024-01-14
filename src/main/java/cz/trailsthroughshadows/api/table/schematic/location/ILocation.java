package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.api.table.schematic.part.LocationPart;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import lombok.ToString;

import java.util.List;

public interface ILocation {
    
    List<Part> getParts();

    int getId();

    String getTitle();

    String getTag();

    Type getType();

    String getDescription();

    void setId(int id);

    void setTitle(String title);

    void setTag(String tag);

    void setType(Type type);

    void setDescription(String description);

    void setLocationParts(List<LocationPart> locationParts);

    boolean equals(Object o);

    int hashCode();

    String toString();

    enum Type {
        CITY, DUNGEON, MARKET, QUEST
    }
}
