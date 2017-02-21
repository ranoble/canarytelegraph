package uk.co.tangent.data.steps.confirmations;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.fasterxml.jackson.annotation.JacksonInject;

public class Confirmation {
    String name;
    String field;
    String operation;
    String value;

    @JacksonInject
    protected Map<String, String> valueBindings;

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
            if (operation.equals("contains")) {

                if (StringUtils.contains(returned, value)) {
                    return new SuccessResult(this, String.format(
                            "%s is contained in %s", field, value));
                } else {
                    return new FailedResult(this, String.format(
                            "%s is not contained in %s", field, value));
                }
            } else if (operation.equals("equals")) {
                if (StringUtils.equals(returned, value)) {
                    return new SuccessResult(this, String.format(
                            "%s is equal to %s", field, value));
                } else {
                    return new FailedResult(this, String.format(
                            "%s is not equal to %s", field, value));
                }
            } else {
                return new FailedResult(this, String.format(
                        "Operation %s doesn't exist", operation));
            }
        } catch (IllegalAccessException e) {
            return new FailedResult(this, e);
        } catch (InvocationTargetException e) {
            return new FailedResult(this, e);
        } catch (NoSuchMethodException e) {
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
