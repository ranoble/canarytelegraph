package uk.co.tangent.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import uk.co.tangent.data.steps.Step;

import com.fasterxml.jackson.annotation.JacksonInject;

public class CanaryTest {
    List<Map<String, String>> bindings;
    List<Step> steps;
    String descriminator;

    @JacksonInject
    Map<String, String> valueBindings;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getBindings() {
        return bindings;
    }

    public void setBindings(List<Map<String, String>> bindings) {
        this.bindings = bindings;
    }

    public List<Step> getSteps() {
        return Optional.ofNullable(steps).orElse(new ArrayList<>());
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public String getDescriminator() {
        return descriminator;
    }

    public void setDescriminator(String descriminator) {
        this.descriminator = descriminator;
    }

    public Map<String, String> getValueBindings() {
        return valueBindings;
    }

    public void setValueBindings(Map<String, String> valueBindings) {
        this.valueBindings = valueBindings;
    }

}
