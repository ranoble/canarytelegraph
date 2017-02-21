import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import uk.co.tangent.data.CanaryTest;
import uk.co.tangent.data.steps.Step;
import uk.co.tangent.data.steps.StepDeserializer;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class TestDeserialization {

    @Test
    public void testDeserializerLoading() {
        StepDeserializer deserial = new StepDeserializer();
        assertTrue(deserial.getSteps().containsKey("http"));
        assertTrue(deserial.getSteps().containsKey("delay"));

    }

    @Test
    public void testDelayDeserializer() throws IOException {
        String delayString = IOUtils.toString(this.getClass()
                .getResourceAsStream("/delay.yml"), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        final InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(Map.class, new HashMap<String, String>());
        objectMapper.setInjectableValues(injectableValues);
        Step step = objectMapper.readValue(delayString, Step.class);
        assertEquals(step.getClass().getCanonicalName(),
                "uk.co.tangent.data.steps.DelayStep");

    }

    @Test
    public void testHttpDeserializer() throws IOException {
        String httpString = IOUtils.toString(this.getClass()
                .getResourceAsStream("/http.yml"), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        final InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(Map.class, new HashMap<String, String>());
        objectMapper.setInjectableValues(injectableValues);
        Step step = objectMapper.readValue(httpString, Step.class);
        assertEquals(step.getClass().getCanonicalName(),
                "uk.co.tangent.data.steps.HttpStep");

    }

    @Test
    public void testHttpMinDeserializer() throws IOException {
        String httpString = IOUtils.toString(this.getClass()
                .getResourceAsStream("/http_min.yml"), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        final InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(Map.class, new HashMap<String, String>());
        objectMapper.setInjectableValues(injectableValues);
        Step step = objectMapper.readValue(httpString, Step.class);
        assertEquals(step.getClass().getCanonicalName(),
                "uk.co.tangent.data.steps.HttpStep");

    }

    @Test
    public void testCanaryTestDeserializer() throws IOException {
        String httpString = IOUtils.toString(this.getClass()
                .getResourceAsStream("/test.yml"), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        final InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(Map.class, new HashMap<String, String>());
        objectMapper.setInjectableValues(injectableValues);
        CanaryTest canary = objectMapper
                .readValue(httpString, CanaryTest.class);
        assertEquals(canary.getClass().getCanonicalName(),
                "uk.co.tangent.data.CanaryTest");

    }

    @Test
    public void testInjection() throws IOException {
        String httpString = IOUtils.toString(this.getClass()
                .getResourceAsStream("/http_min.yml"), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        final InjectableValues.Std injectableValues = new InjectableValues.Std();
        Map<String, String> inj = new HashMap<String, String>();
        inj.put("true", "true");

        injectableValues.addValue(Map.class, inj);
        objectMapper.setInjectableValues(injectableValues);
        Step step = objectMapper.readValue(httpString, Step.class);
        inj.put("after", "after");
        assertEquals(step.getValueBindings().get("true"), "true");
        assertEquals(step.getValueBindings().get("after"), "after");
        assertEquals(step.getClass().getCanonicalName(),
                "uk.co.tangent.data.steps.HttpStep");

    }
}
