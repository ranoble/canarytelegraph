package uk.co.tangent.entities;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import uk.co.tangent.injection.ServiceAwareEntity;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity
public class Test extends ServiceAwareEntity {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    @ApiModelProperty(value = "The test definition, in YAML format. This is a JSON Object definiting test Bindings, and the Tests Steps. Refer to https://github.com/tangentlabs/canarytelegraph/test_definitions.md.", example = "bindings:\n"
            + " - lastname: '[ab]{4,6}c'\n"
            + "steps:\n"
            + "  - type: delay\n"
            + "    name: Wait for 60 minutes\n"
            + "    length: 60\n"
            + "    metric: second\n"
            + "  - type: http\n"
            + "    name: Check user update\n"
            + "    url: https://www.google.co.uk/?ie=UTF-8#q=${lastname}\n"
            + "    method: GET\n"
            + "    confirm:\n"
            + "      - field: status\n"
            + "        name: Confirm result status code\n"
            + "        operation: equals\n"
            + "        value: 200\n"
            + "      - field: body\n"
            + "        name: Confirm lastName updated\n"
            + "        value: 'Did you mean:'")
    private String definition;

    @OneToMany(mappedBy = "test")
    private List<TestResult> results;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @JsonGetter(value = "results")
    @ApiModelProperty(readOnly = true)
    public String serializeResults() {
        return getServices().getTestService().getResultsPath(this);
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
        if (!(o instanceof Test)) {
            return false;
        }

        Test other = (Test) o;
        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }

    @ApiModelProperty(readOnly = true)
    public String getPath() {
        return String.format("/test/%d", this.getId());
    }

}
