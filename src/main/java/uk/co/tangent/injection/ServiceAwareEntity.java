package uk.co.tangent.injection;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JacksonInject;

public class ServiceAwareEntity {
    @Transient
    @JacksonInject
    protected ServiceRegistry services;

    public ServiceRegistry getServices() {
        return services;
    }

    public void setServices(ServiceRegistry services) {
        this.services = services;
    }
}
