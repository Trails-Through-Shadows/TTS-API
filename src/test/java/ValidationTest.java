import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@Slf4j
public class ValidationTest {

    private final List<Part> parts = new ArrayList<>();

    @Before
    public void setUp() {
        Part p = new Part();
        p.setId(1);
        p.getHexes().add(new Hex(new Hex.HexId(1, 1), 0, 0, 0));
        p.setTag("Test part");
        parts.add(p);
    }

    @Test
    public void test() {
        for (Part part : parts) {
            log.info("Validating part {}...", part.getTag());
        }
    }
}
