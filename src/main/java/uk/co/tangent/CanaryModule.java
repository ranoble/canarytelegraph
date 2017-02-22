package uk.co.tangent;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import org.hibernate.Session;
import org.reflections.Reflections;
import uk.co.tangent.injection.ServiceAwareInjector;
import uk.co.tangent.jmx.JMXBean;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.management.*;
import java.lang.management.ManagementFactory;

/**
 * Created by sgyurko on 21/02/2017.
 */
public class CanaryModule extends AbstractModule implements TypeListener {

    @Override
    protected void configure() {
        Reflections reflections = new Reflections("uk.co.tangent");
        bind(Reflections.class).toInstance(reflections);
        bindListener(Matchers.any(), this);
    }

    @Provides
    Session getSession(Provider<HibernateBundle<Config>> hibernateBundleProvider) {
        return hibernateBundleProvider.get().getSessionFactory().openSession();
    }

    @Provides
    @Singleton
    HibernateBundle<Config> getHibernate(Provider<ServiceAwareInjector> serviceAwareInjectorProvider) {
        return new ScanningHibernateBundle<Config>(
                "uk.co.tangent.entities") {

            @Override
            public PooledDataSourceFactory getDataSourceFactory(
                    Config configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            protected void configure(
                    org.hibernate.cfg.Configuration configuration) {
                configuration.setInterceptor(serviceAwareInjectorProvider.get());
            }

        };
    }

    /**
     * This will be listening for all object creation events from Guice, and if necessary register the bean as MXBean
     * @param typeLiteral Type literal from Guice
     * @param typeEncounter Type encounter object from Guice
     * @param <I> class
     */
    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        typeEncounter.register(new InjectionListener<I>() {
            @Override
            public void afterInjection(I i) {
                if (i.getClass().isAnnotationPresent(JMXBean.class)) {
                    try {
                        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
                        ObjectName name = new ObjectName(i.getClass().getPackage().getName() + ":type=" + i.getClass().getSimpleName());
                        mBeanServer.registerMBean(i, name);
                    } catch (MalformedObjectNameException|NotCompliantMBeanException|InstanceAlreadyExistsException|MBeanRegistrationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
