import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;

import uk.co.tangent.data.annotations.PluginType;
import uk.co.tangent.data.steps.DelayStep;
import uk.co.tangent.data.steps.HttpStep;

public class TestParser {

    @Test
    public void scanAnnotations() {
        String packagename = "uk.co.tangent.data.steps";

        final Reflections reflections = new Reflections(packagename);
        Set<Class<?>> types = reflections
                .getTypesAnnotatedWith(PluginType.class);
        assertTrue(types.contains(HttpStep.class));
        assertTrue(types.contains(DelayStep.class));
    }
}
