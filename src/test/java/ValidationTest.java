import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestPropertySource(locations="classpath:application.properties")
public class ValidationTest {

    private List<Part> parts;

    @Before
    public void setUp() {
        Part p = new Part();
        p.setId(1);
        p.getHexes().add(new Hex(new Hex.HexId(1, 1),0, 0, 0));
        p.setTag("Test part");
        parts.add(p);
    }

    @Test
    public void test() {

    }
}
