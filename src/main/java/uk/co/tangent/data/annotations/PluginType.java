package uk.co.tangent.data.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginType {

    String name();

}
