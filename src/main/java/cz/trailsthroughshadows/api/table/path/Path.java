package cz.trailsthroughshadows.api.table.path;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * TODO table without id
 */
@Table(name = "Path")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Path {

    private int idStart;
    private int idEnd;

}
