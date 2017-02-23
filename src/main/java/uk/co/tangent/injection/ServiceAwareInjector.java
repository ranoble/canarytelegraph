package uk.co.tangent.injection;

import com.google.inject.Injector;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.Serializable;

public class ServiceAwareInjector extends EmptyInterceptor {

    private static final long serialVersionUID = -8654687534520838664L;
    private final Injector injector;

    @Inject
    public ServiceAwareInjector(Injector injector) {
        this.injector = injector;
    }

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
        if (entity instanceof ServiceAwareEntity) {
            injector.injectMembers(entity);
        }
        return super.onLoad(entity, id, state, propertyNames, types);
    }

}
