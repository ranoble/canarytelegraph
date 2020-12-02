package uk.co.tangent.entities;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.backtothefront.HstoreUserType;
import org.hibernate.annotations.AttributeAccessor;
import org.hibernate.annotations.TypeDef;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.HashMap;

import uk.co.tangent.data.steps.confirmations.Result;
import uk.co.tangent.injection.ServiceAwareEntity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@AttributeAccessor("field")
@TypeDef(name = "hstore", typeClass = HstoreUserType.class)
public class TestResult extends ServiceAwareEntity {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne
    @JoinColumn(name = "lane_id")
    private Lane lane;

    @Column(columnDefinition = "text")
    private String results;

    private DateTime written = new DateTime();
    private Boolean healthy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonGetter(value = "test")
    public String serializeTest() {

        return services.getTestService().getPath(test);

    }

    @JsonSetter(value = "test")
    public void deserializeTest(String path) {
        test = services.getTestService().fromPath(path);
    }

    @JsonGetter(value = "lane")
    public String serializeTests() {
        return services.getLaneService().getPath(lane);

    }

    @JsonSetter(value = "lane")
    public void deserializeLane(String path) {

        lane = services.getLaneService().fromPath(path);
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Lane getLane() {
        return lane;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public DateTime getWritten() {
        return written;
    }

    public void setWritten(DateTime written) {
        this.written = written;
    }

    public Boolean getHealthy() {
        return healthy;
    }

    public void setHealthy(Boolean healthy) {
        this.healthy = healthy;
    }

    @JsonIgnore
    public List<Result> getList() throws JsonParseException,
            JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ConcurrentHashMap<String, String> bindings = new ConcurrentHashMap<String, String>();
        final InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(Map.class, bindings);
        mapper.setInjectableValues(injectableValues);
        List<Result> stepResults = mapper.readValue(
                results,
                mapper.getTypeFactory().constructCollectionType(List.class,
                        Result.class));
        return stepResults;
    }

}
