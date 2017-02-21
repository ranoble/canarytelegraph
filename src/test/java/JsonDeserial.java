import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDeserial {
    @Test
    public void testJsonTest() throws IOException {
        String jsonString = IOUtils.toString(this.getClass()
                .getResourceAsStream("/parse_test.json"),
                StandardCharsets.UTF_8);
        ObjectMapper map = new ObjectMapper();
        map.readValue(jsonString, uk.co.tangent.entities.Test.class);
    }
}
