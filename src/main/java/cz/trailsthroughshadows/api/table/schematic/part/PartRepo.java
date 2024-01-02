package cz.trailsthroughshadows.api.table.schematic.part;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PartRepo extends PagingAndSortingRepository<Part, Integer>, JpaRepository<Part, Integer> {

    @Override
    @EntityGraph(attributePaths = {"hexes"})
    Page<Part> findAll(Pageable pageable);

}
