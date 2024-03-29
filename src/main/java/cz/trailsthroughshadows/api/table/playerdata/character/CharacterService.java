package cz.trailsthroughshadows.api.table.playerdata.character;

import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.response.IdResponse;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
import cz.trailsthroughshadows.api.table.background.clazz.ClazzRepo;
import cz.trailsthroughshadows.api.table.background.clazz.model.ClazzDTO;
import cz.trailsthroughshadows.api.table.background.race.RaceRepo;
import cz.trailsthroughshadows.api.table.background.race.model.RaceDTO;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureRepo;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.playerdata.character.model.CharacterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class CharacterService {

    @Autowired
    ValidationService validation;

    @Autowired
    ValidationConfig validationConfig;

    @Autowired
    CharacterRepo characterRepo;

    @Autowired
    AdventureRepo adventureRepo;

    @Autowired
    ClazzRepo clazzRepo;

    @Autowired
    RaceRepo raceRepo;

    public CharacterDTO findById(int id) {
        return characterRepo.findById(id)
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Character not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });
    }

    public List<CharacterDTO> findAll() {
        return characterRepo.findAll();
    }

    public RestResponse add(CharacterDTO character, Integer adventureId, Session session) {
        if (character == null) {
            RestError error = RestError.of(HttpStatus.BAD_REQUEST, "Character cannot be null!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        AdventureDTO adventure = adventureRepo.findById(adventureId)
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Adventure not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });
        character.setIdAdventure(adventureId);
        character.setInventory(new LinkedList<>());

        int limit = validationConfig.getAdventure().getMaxPlayers();
        int current = characterRepo.getCountByAdventureId(adventure.getId());

        if (character.getId() != null) {
            RestError error = RestError.of(HttpStatus.BAD_REQUEST, "Character ID must be null!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        if (current >= limit) {
            RestError error = RestError.of(HttpStatus.BAD_REQUEST, "Adventure is full!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        if (!session.hasAccess(adventure.getIdLicense())) {
            RestError error = RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to add a character to this adventure!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        ClazzDTO clazz = clazzRepo.findById(character.getIdClass())
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Class not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });
        character.setClazz(clazz);

        RaceDTO race = raceRepo.findById(character.getIdRace())
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Race not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });
        character.setRace(race);

        validation.validate(character);

        log.debug("Saving character '{}' for adventure '{}'", character, adventure.getId());
        characterRepo.save(character);

        return new IdResponse(HttpStatus.OK, character.getId());
    }

    public RestResponse delete(Integer id, Session session) {
        CharacterDTO character = characterRepo.findById(id)
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Character not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });

        AdventureDTO adventure = adventureRepo.findById(character.getIdAdventure())
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Adventure not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });

        if (!session.hasAccess(adventure.getIdLicense())) {
            RestError error = RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to delete this character!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        log.debug("Deleting character '{}' for adventure '{}'", character, adventure.getId());
        characterRepo.delete(character);

        return new MessageResponse(HttpStatus.OK, "Character deleted!");
    }

    public RestResponse update(Integer id, CharacterDTO newCharacter, Session session) {
        CharacterDTO character = characterRepo.findById(id)
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Character not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });

        AdventureDTO adventure = adventureRepo.findById(character.getIdAdventure())
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Adventure not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });

        if (!session.hasAccess(adventure.getIdLicense())) {
            RestError error = RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to update this character!");
            log.warn(error.toString());
            throw new RestException(error);
        }

        ClazzDTO clazz = clazzRepo.findById(newCharacter.getIdClass())
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Class not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });

        RaceDTO race = raceRepo.findById(newCharacter.getIdRace())
                .orElseThrow(() -> {
                    RestError error = RestError.of(HttpStatus.NOT_FOUND, "Race not found!");
                    log.warn(error.toString());
                    return new RestException(error);
                });

        character.setClazz(clazz);
        character.setIdClass(newCharacter.getIdClass());
        character.setRace(race);
        character.setIdRace(newCharacter.getIdRace());
        character.setTitle(newCharacter.getTitle());
        character.setPlayerName(newCharacter.getPlayerName());

        validation.validate(character);

        log.debug("Updating character '{}' for adventure '{}'", character, adventure.getId());
        characterRepo.save(newCharacter);
        return new IdResponse(HttpStatus.OK, newCharacter.getId());
    }
}
