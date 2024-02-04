package cz.trailsthroughshadows.api.table.schematic.part.model;

import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.location.Navigation;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Part")
@EqualsAndHashCode(callSuper = true)
public class PartDTO extends Validable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(name = "tag")
    protected String tag;

    @OneToMany(mappedBy = "key.idPart", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<HexDTO> hexes = new ArrayList<>();

    @Column(name = "usages", columnDefinition = "INT default 0")
    protected int usages = 0;

    public void setHexes(List<HexDTO> hexes) {
        if (hexes != null) {
            this.hexes.retainAll(hexes);
            this.hexes.addAll(hexes);
        } else {
            this.hexes.clear();
        }
    }

    //region Validation
    @Override
    public void validateInner(ValidationConfig validationConfig) {
        // min 5 hexes
        if (getHexes().size() < validationConfig.getHexGrid().getMinHexes()) {
            errors.add("Part must have at least %d hexes!".formatted(validationConfig.getHexGrid().getMinHexes()));
        }

        // max 50 hexes
        if (getHexes().size() > validationConfig.getHexGrid().getMaxHexes()) {
            errors.add("Part must have at most 50 hexes!");
        }

        // every hex has to have correct coordinates
        for (HexDTO hex : getHexes()) {
            errors.addAll(Hex.fromDTO(hex).validate(validationConfig));
        }
        if (!errors.isEmpty())
            return;

        // max 8 hexes wide
        List<Integer> coordinates = new ArrayList<>();

        IntSummaryStatistics qStats = getHexes().stream().mapToInt(HexDTO::getQ).summaryStatistics();
        IntSummaryStatistics rStats = getHexes().stream().mapToInt(HexDTO::getR).summaryStatistics();
        IntSummaryStatistics sStats = getHexes().stream().mapToInt(HexDTO::getS).summaryStatistics();

        int diffQ = qStats.getMax() - qStats.getMin() - 1;
        int diffR = rStats.getMax() - rStats.getMin() - 1;
        int diffS = sStats.getMax() - sStats.getMin() - 1;

        if (diffQ > validationConfig.getHexGrid().getMaxWidth() || diffR > validationConfig.getHexGrid().getMaxWidth() || diffS > validationConfig.getHexGrid().getMaxWidth()) {
            errors.add("Part must not be wider than %d hexes!".formatted(validationConfig.getHexGrid().getMaxWidth()));
        }

        // no hexes can be on the same position
        int duplicates = 0;
        for (HexDTO hex1 : getHexes()) {
            for (HexDTO hex2 : getHexes()) {
                if (hex1 == hex2)
                    continue;

                if (hex1.getQ() == hex2.getQ() && hex1.getR() == hex2.getR() && hex1.getS() == hex2.getS()) {
                    duplicates++;
                    break;
                }
            }
        }
        if (duplicates > 0)
            errors.add("Part must not have duplicate hexes!");

        // must include center hex
        Optional<HexDTO> centerHex = getHexes().stream().filter(hex -> hex.getQ() == 0 && hex.getR() == 0 && hex.getS() == 0).findFirst();
        if (centerHex.isEmpty()) {
            errors.add("Part must include a center hex!");
            return;
        }

        // all hexes must be connected
        Navigation navigation = new Navigation(this);

        for (HexDTO hex : getHexes()) {
            if (hex == centerHex.get())
                continue;

            if (navigation.getPath(centerHex.get(), hex) == null) {
                errors.add("All hexes must be connected!");
                break;
            }
        }

        // part has to have a correct tag and title
        Title title = new Title(tag);
        title.validate(validationConfig);
    }

    @Override
    public String getIdentifier() {
        return getTag();
    }
    //endregion
}