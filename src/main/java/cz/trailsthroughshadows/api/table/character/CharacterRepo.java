package cz.trailsthroughshadows.api.table.character;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface CharacterRepo extends JpaRepository<Character,Integer> {

    @Query("SELECT c FROM Character c")
    Collection<Character> getAll();
}
