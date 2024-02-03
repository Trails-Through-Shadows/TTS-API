package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface LocationRepo extends JpaRepository<LocationDTO, Integer> {

    @Override
    List<LocationDTO> findAll();

    @Override
    Optional<LocationDTO> findById(Integer id);

}
