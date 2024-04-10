package cz.trailsthroughshadows.api.table.action;


import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ActionRepo extends JpaRepository<ActionDTO, Integer> {

    @Query("SELECT c FROM ActionDTO c")
    Collection<ActionDTO> getAll();

}