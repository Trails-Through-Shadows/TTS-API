package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.api.table.schematic.part.model.LocationPart;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;

import java.util.List;

public interface ILocation {

    List<Part> getParts();

    int getId();

    void setId(int id);

    String getTitle();

    void setTitle(String title);

    String getTag();

    void setTag(String tag);

    Type getType();

    void setType(Type type);

    String getDescription();

    void setDescription(String description);

    void setLocationParts(List<LocationPart> locationParts);

    boolean equals(Object o);

    int hashCode();

    String toString();

    enum Type {
        CITY, DUNGEON, MARKET, QUEST
    }
}
