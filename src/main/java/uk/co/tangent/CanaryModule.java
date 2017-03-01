package uk.co.tangent;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.inject.Singleton;

import org.hibernate.HibernateException;
import org.hibernate.Session;

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
    Session getSession(HibernateBundle<Config> hibernateBundle) {
        Session session;
        try {
            session = hibernateBundle.getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            session = hibernateBundle.getSessionFactory().openSession();
        }
        return session;
    }

    @Provides
    @Singleton
    HibernateBundle<Config> getHibernate(
            ServiceAwareInjector serviceAwareInjector) {
        return new ScanningHibernateBundle<Config>("uk.co.tangent.entities") {

            @Override
            public PooledDataSourceFactory getDataSourceFactory(
                    Config configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            protected void configure(
                    org.hibernate.cfg.Configuration configuration) {
                configuration.setInterceptor(serviceAwareInjector);
            }
        };
    }

    @Provides
    @Singleton
    SwaggerBundle<Config> getSwagger(ServiceAwareInjector serviceAwareInjector) {
        return new SwaggerBundle<Config>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(
                    Config configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        };
    }
}
