package uk.co.tangent.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.backtothefront.HstoreUserType;
import nl.flotsam.xeger.Xeger;
import org.hibernate.annotations.AttributeAccessor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import uk.co.tangent.data.CanaryTest;
import uk.co.tangent.injection.ServiceAwareEntity;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Entity
@AttributeAccessor("field")
@TypeDef(name = "hstore", typeClass = HstoreUserType.class)
public class Lane extends ServiceAwareEntity {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore")
    @ApiModelProperty(value = "The key value bindings, bound to this specific lane, as a json object. These are variables you can define and use in the tests. They can be defined as string literals or as simple regular expressions. in the case of Regular Expressions, random strings will be generated.", example = "{\"user_id\": \"(1|2|3)\"}")
    private Map<String, String> laneBindings = new HashMap<String, String>();

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(name = "lane_tests")
    private List<Test> tests;

    @OneToMany(mappedBy = "lane")
    private List<TestResult> results;

    @ApiModelProperty(required = true)
    private String name;

    private Boolean active = Boolean.FALSE;

    @Transient
    private Random random;

    @Transient
    private ObjectMapper objectMapper;

    @Transient
    private ConcurrentHashMap<String, String> bindings;

    public Lane() {
        objectMapper = new ObjectMapper(new YAMLFactory());
        bindings = new ConcurrentHashMap<String, String>();
        final InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(Map.class, bindings);
        objectMapper.setInjectableValues(injectableValues);
        random = new Random();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonGetter(value = "tests")
    @ApiModelProperty(value = "List of tests, int the form of relative URI's: As json array.", example = "[/test/1,/test/2]")
    public Set<String> serializeTests() {
        List<Test> tests = getTests();
        return tests.stream()
                .map(test -> getServices().getTestService().getPath(test))
                .collect(Collectors.toSet());

    }

    @JsonSetter(value = "tests")
    @ApiModelProperty(value = "List of tests, int the form of relative URI's: As json array.", example = "[/test/1,/test/2]")
    public void deserializeTests(Set<String> testPaths) {
        tests = testPaths.stream()
                .map(string -> getServices().getTestService().fromPath(string))
                .collect(Collectors.toList());

    }

    @JsonGetter(value = "results")
    @ApiModelProperty(value = "The relative path of the lane tests", readOnly = true)
    public String serializeResults() {
        return getServices().getLaneService().getResultsPath(this);
    }

    public List<Test> getTests() {
        if (tests == null) {
            tests = new ArrayList<>();
        }
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public Map<String, String> getLaneBindings() {
        return laneBindings;
    }

    public void setLaneBindings(Map<String, String> laneBindings) {
        this.laneBindings = laneBindings;
    }

    public void applyBindings(CanaryTest canaryTest) {
        bindings.clear();
        applyTestBindings(canaryTest);
        applyLaneBindings(canaryTest);
    }

    protected void applyLaneBindings(CanaryTest canaryTest) {
        // Lane Bindings take precedence
        for (Entry<String, String> entry : this.getLaneBindings().entrySet()) {
            Xeger generator = new Xeger(entry.getValue());
            String result = generator.generate();
            canaryTest.getValueBindings().put(entry.getKey(), result);
        }
    }

    protected void applyTestBindings(CanaryTest canaryTest) {
        for (Map<String, String> binding : canaryTest.getBindings()) {
            for (Entry<String, String> entry : binding.entrySet()) {
                Xeger generator = new Xeger(entry.getValue());
                String result = generator.generate();
                canaryTest.getValueBindings().put(entry.getKey(), result);
            }
        }
    }

    public Boolean getActive() {
        return Optional.ofNullable(active).orElse(Boolean.FALSE);
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean sleep() {
        try {
            Thread.sleep(random.nextInt(600) * 1000);
            return true;
        } catch (InterruptedException e) {
            return false;
        }

    }

    public CanaryTest parse(Test test) throws JsonParseException,
            JsonMappingException, IOException {
        return objectMapper.readValue(test.getDefinition(), CanaryTest.class);
    }

    public List<TestResult> getResults() {
        return results;
    }

    public void setResults(List<TestResult> results) {
        this.results = results;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Lane)) {
            return false;
        }

        Lane other = (Lane) o;
        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
