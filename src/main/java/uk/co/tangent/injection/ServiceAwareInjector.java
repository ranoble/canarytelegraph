package uk.co.tangent.injection;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.Serializable;

public class ServiceAwareInjector extends EmptyInterceptor {
    private static final long serialVersionUID = -6731622938182113109L;
    private Provider<ServiceRegistry> servicesProvider;

    @Inject
    public ServiceAwareInjector(Provider<ServiceRegistry> servicesProvider) {
        this.servicesProvider = servicesProvider;
    }

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
        if (entity instanceof ServiceAwareEntity) {
            ServiceAwareEntity _entity = (ServiceAwareEntity) entity;
            _entity.setServices(servicesProvider.get());
        }
        return super.onLoad(entity, id, state, propertyNames, types);
    }

}
