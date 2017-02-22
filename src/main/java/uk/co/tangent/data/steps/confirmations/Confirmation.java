package uk.co.tangent.data.steps.confirmations;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.fasterxml.jackson.annotation.JacksonInject;

public class Confirmation {
    private String name;
    private String field;
    private String operation;
    private String value;

    @JacksonInject
    private Map<String, String> valueBindings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    protected String bind(String payload) {
        StrSubstitutor sub = new StrSubstitutor(valueBindings);
        return sub.replace(payload);
    }

    public Result validate(Object response) {
        try {
            String returned = BeanUtils.getProperty(response, field);
            value = bind(value);
            switch (operation) {
            case "contains":
                if (StringUtils.contains(returned, value)) {
                    return new SuccessResult(this, String.format(
                            "%s is contained in %s", field, value));
                } else {
                    return new FailedResult(this, String.format(
                            "%s is not contained in %s", field, value));
                }
            case "equals":
                if (StringUtils.equals(returned, value)) {
                    return new SuccessResult(this, String.format(
                            "%s is equal to %s", field, value));
                } else {
                    return new FailedResult(this, String.format(
                            "%s is not equal to %s", field, value));
                }
            default:
                return new FailedResult(this, String.format(
                        "Operation %s doesn't exist", operation));
            }
        } catch (IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            return new FailedResult(this, e);
        }
    }

    public Map<String, String> getValueBindings() {
        return valueBindings;
    }

    public void setValueBindings(Map<String, String> valueBindings) {
        this.valueBindings = valueBindings;
    }
}
