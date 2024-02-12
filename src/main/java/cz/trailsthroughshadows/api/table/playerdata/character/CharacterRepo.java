package cz.trailsthroughshadows.api.table.playerdata.character;

import cz.trailsthroughshadows.api.table.playerdata.character.model.CharacterDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface CharacterRepo extends JpaRepository<CharacterDTO, Integer> {

    @Query("SELECT c FROM CharacterDTO c")
    Collection<CharacterDTO> getAll();

    @Query("SELECT c FROM CharacterDTO c WHERE c.idAdventure = ?1")
    Collection<CharacterDTO> getByAdventure(int idAdventure);
}
