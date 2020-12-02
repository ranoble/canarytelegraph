package uk.co.tangent.jmx;

import java.lang.annotation.*;

/**
 * Created by sgyurko on 22/02/2017.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JMXBean {
}
