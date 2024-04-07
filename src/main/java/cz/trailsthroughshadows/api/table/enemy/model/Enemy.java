package cz.trailsthroughshadows.api.table.enemy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.images.ImageLoader;
import cz.trailsthroughshadows.api.table.action.model.Action;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
// @JsonInclude(JsonInclude.Include.NON_NULL)
public class Enemy extends EnemyDTO {

    private HexDTO startingHex;

    private String url;

    @JsonIgnore
    List<Action> deck = new ArrayList<>();
    @JsonIgnore
    int deckIndex = 0;

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }

    public static Enemy fromDTO(EnemyDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Enemy.class);
    }

    public static Enemy fromDTO(EnemyDTO dto, HexDTO hex) {
        Enemy enemy = fromDTO(dto);
        enemy.setStartingHex(hex);
        enemy.setDeck(new ArrayList<>(dto.getMappedActions().stream().map(Action::fromDTO).toList()));

        if (enemy.getDeck().isEmpty())
            enemy.setDeckIndex(-1);

        return enemy;
    }

    public void shuffleDeck() {
        // todo: better shuffling method

        for (int i = 0; i < deck.size(); i++) {
            int randomIndex = (int) (Math.random() * deck.size());
            Action temp = deck.get(i);
            deck.set(i, deck.get(randomIndex));
            deck.set(randomIndex, temp);
        }
    }

    public Action drawCard() {
        if (deckIndex == -1)
            return Action.fromDTO(ActionDTO.DO_NOTHING);

        if (deckIndex >= deck.size()) {
            shuffleDeck();
            deckIndex = 0;
        }
        return deck.get(deckIndex++);
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
