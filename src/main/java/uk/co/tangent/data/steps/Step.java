package uk.co.tangent.data.steps;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import uk.co.tangent.data.steps.confirmations.Result;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = StepDeserializer.class)
public abstract class Step {
    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private String type;

    @JacksonInject
    private Map<String, String> valueBindings;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected String bind(String payload) {
        StrSubstitutor sub = new StrSubstitutor(valueBindings);
        return sub.replace(payload);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getValueBindings() {
        return valueBindings;
    }

    public void setValueBindings(Map<String, String> valueBindings) {
        this.valueBindings = valueBindings;
    }

    public abstract List<Result> call();
}
