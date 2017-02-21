package uk.co.tangent.injection;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class ServiceAwareInjector extends EmptyInterceptor {
    private static final long serialVersionUID = 1L;
    private ServiceRegistry services;

    public ServiceAwareInjector(ServiceRegistry services) {
        this.services = services;
    }

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
        if (entity instanceof ServiceAwareEntity) {
            ServiceAwareEntity _entity = (ServiceAwareEntity) entity;
            _entity.setServices(services);
        }
        return super.onLoad(entity, id, state, propertyNames, types);
    }

}
