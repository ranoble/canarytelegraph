package uk.co.tangent;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.hibernate.SessionFactory;

import uk.co.tangent.injection.ServiceAwareInjector;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Created by sgyurko on 21/02/2017.
 */
public class CanaryModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    SessionFactory getSession(
            Provider<HibernateBundle<Config>> hibernateBundleProvider) {
        return hibernateBundleProvider.get().getSessionFactory();
    }

    @Provides
    UnitOfWorkAwareProxyFactory getUnitOfWorkFactory(
            Provider<HibernateBundle<Config>> hibernateBundleProvider) {
        return new UnitOfWorkAwareProxyFactory(hibernateBundleProvider.get());
    }

    @Provides
    @Singleton
    HibernateBundle<Config> getHibernate(
            Provider<ServiceAwareInjector> serviceAwareInjectorProvider) {

        return new ScanningHibernateBundle<Config>("uk.co.tangent.entities") {

            @Override
            public PooledDataSourceFactory getDataSourceFactory(
                    Config configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            protected void configure(
                    org.hibernate.cfg.Configuration configuration) {
                configuration
                        .setInterceptor(serviceAwareInjectorProvider.get());
            }

        };
    }
}
