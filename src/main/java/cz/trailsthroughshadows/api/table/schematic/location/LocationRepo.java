package cz.trailsthroughshadows.api.table.schematic.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LocationRepo extends PagingAndSortingRepository<Location, Integer>, JpaRepository<Location, Integer> {

}
