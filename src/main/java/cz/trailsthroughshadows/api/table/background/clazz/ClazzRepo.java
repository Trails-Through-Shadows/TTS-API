package cz.trailsthroughshadows.api.table.background.clazz;

import cz.trailsthroughshadows.api.table.background.clazz.model.ClazzDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ClazzRepo extends JpaRepository<ClazzDTO, Integer> {

    @Query("SELECT c FROM ClazzDTO c")
    Collection<ClazzDTO> getAll();
}
