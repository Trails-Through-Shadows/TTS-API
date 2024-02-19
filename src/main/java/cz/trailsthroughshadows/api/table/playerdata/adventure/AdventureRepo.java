package cz.trailsthroughshadows.api.table.playerdata.adventure;

import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdventureRepo extends JpaRepository<AdventureDTO, Integer> {

    @Query("SELECT COUNT(*) FROM AdventureDTO WHERE idLicense = :licenseId")
    int getCountByLicenseId(@Param("licenseId") int licenseId);

}
