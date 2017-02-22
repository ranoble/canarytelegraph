package uk.co.tangent.data.steps;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import uk.co.tangent.data.annotations.PluginType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StepDeserializer extends JsonDeserializer<Step> {

    private static final String NAME = "type";
    private Map<String, Class> steps;

    public StepDeserializer() {
        super();
        String packagename = "uk.co.tangent.data.steps";
        steps = new HashMap<String, Class>();

        final Reflections reflections = new Reflections(packagename);
        Set<Class<?>> types = reflections
                .getTypesAnnotatedWith(PluginType.class);
        for (Class type : types) {
            PluginType annotation = (PluginType) type
                    .getAnnotation(PluginType.class);
            if (annotation != null) {
                steps.put(annotation.name(), type);
            }

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Step deserialize(JsonParser jp, DeserializationContext context)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);
        if (root.has(NAME)) {
            JsonNode jsonNode = root.get(NAME);
            Class<?> target = steps.get(jsonNode.asText());
            if (target == null) {
                throw context.mappingException(String.format(
                        "Type %s not registered", jsonNode.asText()));
            }
            Object step = mapper.readValue(root.toString(), target);
            if (step instanceof Step) {
                return (Step) step;
            }
            throw context
                    .mappingException("An annotate instance of PluginType must inherit from uk.co.tangent.data.steps.Step");

        }
        throw context
                .mappingException("Could not deserialize, no type parameter provided");
    }

    public Map<String, Class> getSteps() {
        return steps;
    }

    public void setSteps(Map<String, Class> steps) {
        this.steps = steps;
    }

}
