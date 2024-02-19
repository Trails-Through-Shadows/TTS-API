package cz.trailsthroughshadows.api.table.playerdata.adventure.license;

import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface LicenseRepo extends JpaRepository<License, Integer> {

    @Query("SELECT c FROM License c")
    Collection<License> getAll();

    @Query("SELECT l FROM License l WHERE l.key = ?1")
    Optional<License> findByKey(String key);
}
