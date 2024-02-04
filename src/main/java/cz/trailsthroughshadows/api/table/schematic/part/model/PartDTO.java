package cz.trailsthroughshadows.api.table.schematic.part.model;

import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import cz.trailsthroughshadows.algorithm.location.Navigation;
import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.text.Tag;
import cz.trailsthroughshadows.algorithm.validation.text.Title;
import cz.trailsthroughshadows.api.rest.model.error.RestSubError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
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

    @Column(name = "tag", length = 32)
    protected String tag;

    @Column(name = "title", length = 128)
    protected String title;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "key.idPart", cascade = CascadeType.ALL, orphanRemoval = true)
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
        ValidationConfig.HexGrid hexGrid = validationConfig.getHexGrid();

        List<RestSubError> textErrors = new ArrayList<>();

        // part has to have a correct tag and title
        Title title = new Title(getTitle());
        title.validate(validationConfig).ifPresent(restError -> textErrors.addAll(restError.getErrors()));

        Tag tag = new Tag(getTag());
        tag.validate(validationConfig).ifPresent(restError -> textErrors.addAll(restError.getErrors()));

        // min 5 hexes
        if (getHexes().size() < hexGrid.getMinHexes()) {
            errors.add(new ValidationError(getValidableClass(), "hexes", getHexes().size(), "Part must have at least %d hexes!".formatted(hexGrid.getMinHexes())));
        }

        // max 50 hexes
        if (getHexes().size() > hexGrid.getMaxHexes()) {
            errors.add(new ValidationError(getValidableClass(), "hexes", getHexes().size(), "Part must have at most %d hexes!".formatted(hexGrid.getMaxHexes())));
        }

        // every hex has to have correct coordinates
        for (HexDTO hex : getHexes()) {
            hex.validate(validationConfig).ifPresent(restError -> errors.addAll(restError.getErrors()));
        }
        if (!errors.isEmpty()) {
            errors.addAll(textErrors);
            return;
        }

        errors.addAll(textErrors);

        // max 8 hexes wide
        List<Integer> coordinates = new ArrayList<>();

        IntSummaryStatistics qStats = getHexes().stream().mapToInt(HexDTO::getQ).summaryStatistics();
        IntSummaryStatistics rStats = getHexes().stream().mapToInt(HexDTO::getR).summaryStatistics();
        IntSummaryStatistics sStats = getHexes().stream().mapToInt(HexDTO::getS).summaryStatistics();

        int diffQ = qStats.getMax() - qStats.getMin() - 1;
        int diffR = rStats.getMax() - rStats.getMin() - 1;
        int diffS = sStats.getMax() - sStats.getMin() - 1;

        String widthError = "Part must not be wider than %d hexes!".formatted(hexGrid.getMaxWidth());
        List<String> coords = new ArrayList<>();

        if (diffQ > hexGrid.getMaxWidth()) {
            coords.add("qCoord");
        }
        if (diffR > hexGrid.getMaxWidth()) {
            coords.add("rCoord");
        }
        if (diffS > hexGrid.getMaxWidth()) {
            coords.add("sCoord");
        }

        if (!coords.isEmpty()) {
            errors.add(new ValidationError(getValidableClass(), "hexes", coords, widthError));
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
            errors.add(new ValidationError(getValidableClass(), "hexes", duplicates, "Part must not have any duplicate hexes!"));

        // must include center hex
        Optional<HexDTO> centerHex = getHexes().stream().filter(hex -> hex.getQ() == 0 && hex.getR() == 0 && hex.getS() == 0).findFirst();
        if (centerHex.isEmpty()) {
            errors.add(new ValidationError(getValidableClass(), "hexes", null, "Part must include center hex!"));
            return;
        }

        // all hexes must be connected
        Navigation navigation = new Navigation(this);

        for (HexDTO hex : getHexes()) {
            if (hex == centerHex.get())
                continue;

            if (navigation.getPath(centerHex.get(), hex) == null) {
                errors.add(new ValidationError(getValidableClass(), hex.getValidableValue(), null, "Part must be connected!"));
                break;
            }
        }
    }

    @Override
    public String getValidableValue() {
        return "%s (%s)".formatted(getTag(), getTitle());
    }
    //endregion
}