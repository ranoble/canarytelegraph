package uk.co.tangent.data.steps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.tangent.data.annotations.PluginType;
import uk.co.tangent.data.steps.confirmations.Result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = DelayStep.class)
@PluginType(name = "delay")
public class DelayStep extends Step {

    private final Map<String, Integer> metrics;

    public DelayStep() {
        Map<String, Integer> aMap = new HashMap<String, Integer>();
        aMap.put("hour", 3600000);
        aMap.put("min", 60000);
        aMap.put("second", 1000);
        aMap.put("milli", 1);
        metrics = Collections.unmodifiableMap(aMap);
    }

    @JsonProperty(required = true)
    private String metric;
    @JsonProperty(required = true)
    private int length;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public List<Result> call() {
        try {
            Thread.sleep(length * metrics.getOrDefault(metric.toLowerCase(), 1));
        } catch (InterruptedException e) {
        }
        return new ArrayList<Result>();
    }
}
