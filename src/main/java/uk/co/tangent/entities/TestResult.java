package uk.co.tangent.entities;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.backtothefront.HstoreUserType;

import org.hibernate.annotations.AttributeAccessor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.joda.time.DateTime;

import uk.co.tangent.injection.ServiceAwareEntity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

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

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore")
    private Map<String, String> bindings = new HashMap<String, String>();

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

    public Map<String, String> getBindings() {
        return bindings;
    }

    public void setBindings(Map<String, String> bindings) {
        this.bindings = bindings;
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

}
