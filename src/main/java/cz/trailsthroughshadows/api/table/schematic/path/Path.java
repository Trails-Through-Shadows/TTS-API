package cz.trailsthroughshadows.api.table.schematic.path;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Path")
public class Path {

    private int idStart;
    private int idEnd;

}
