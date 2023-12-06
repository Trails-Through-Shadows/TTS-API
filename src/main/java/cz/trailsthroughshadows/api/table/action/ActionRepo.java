package cz.trailsthroughshadows.api.table.action;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ActionRepo extends JpaRepository<Action, Integer> {

    @Query("SELECT a FROM Action a")
    Collection<Action> getAll();
}