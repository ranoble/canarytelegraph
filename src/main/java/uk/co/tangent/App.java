package uk.co.tangent;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import uk.co.tangent.entities.Lane;
import uk.co.tangent.injection.ServiceAwareInjector;
import uk.co.tangent.injection.ServiceRegistry;
import uk.co.tangent.resources.LaneResource;
import uk.co.tangent.resources.TestResource;
import uk.co.tangent.services.LaneAlreadyRunningException;
import uk.co.tangent.services.LaneService;
import uk.co.tangent.services.TaskService;
import uk.co.tangent.services.TestResultService;
import uk.co.tangent.services.TestService;

import com.fasterxml.jackson.databind.InjectableValues;

public class App extends Application<Config> {

    /**
     * Container for services. Just so we dont need to pass them around much.
     */
    private ServiceRegistry services;

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    public App() {
        super();

        this.services = setupServices();
    }

    protected ServiceRegistry setupServices() {
        final ServiceRegistry services = new ServiceRegistry();

        final Interceptor interceptor = new ServiceAwareInjector(services);

        HibernateBundle<Config> hibernate = new ScanningHibernateBundle<Config>(
                "uk.co.tangent.entities") {

            @Override
            public PooledDataSourceFactory getDataSourceFactory(
                    Config configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            protected void configure(
                    org.hibernate.cfg.Configuration configuration) {
                configuration.setInterceptor(interceptor);
            }

        };

        services.setHibernate(hibernate);
        services.setTestService(new TestService() {

            @Override
            protected Session getSession() {
                return hibernate.getSessionFactory().getCurrentSession();
            }

        });
        services.setTestResult(new TestResultService() {

            @Override
            protected Session getSession() {
                return hibernate.getSessionFactory().getCurrentSession();
            }

        });
        // guice? Seems overkill for a single service.
        services.setTasks(new TaskService(services.getTestResult()) {
            @Override
            protected Session getSession() {
                return hibernate.getSessionFactory().getCurrentSession();
            }

            @Override
            protected SessionFactory getSessionFactory() {
                return hibernate.getSessionFactory();
            }
        });
        services.setLaneService(new LaneService() {
            @Override
            protected Session getSession() {
                return hibernate.getSessionFactory().getCurrentSession();
            }
        });
        return services;
    }

    @Override
    public String getName() {
        return "canary-telegraph";
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {

        bootstrap.addBundle(services.getHibernate());
        bootstrap.addBundle(new ViewBundle<Config>());
        bootstrap.addBundle(new AssetsBundle("/assets/", "/static/"));

        final InjectableValues.Std injectableValues = new InjectableValues.Std();
        injectableValues.addValue(ServiceRegistry.class, services);

        bootstrap.getObjectMapper().setInjectableValues(injectableValues);

    }

    @Override
    public void run(Config config, Environment env) throws Exception {
        final LaneResource lanes = new LaneResource(services);
        env.jersey().register(lanes);
        registerAndRunLanes();

        final TestResource tests = new TestResource(services);
        env.jersey().register(tests);
    }

    protected void registerAndRunLanes() throws LaneAlreadyRunningException {
        SessionFactory sessionFactory = services.getHibernate()
                .getSessionFactory();

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            LaneService laneService = new LaneService() {
                @Override
                protected Session getSession() {
                    return session;
                }
            };

            TestResultService resultService = new TestResultService() {
                @Override
                protected Session getSession() {
                    return session;
                }
            };

            TaskService tasks = new TaskService(resultService) {
                @Override
                protected Session getSession() {
                    return session;
                }

                @Override
                protected SessionFactory getSessionFactory() {
                    return sessionFactory;
                }
            };

            for (Lane lane : laneService.getLanes()) {
                tasks.addLane(lane);
                if (lane.getActive()) {
                    tasks.startLane(lane);
                }
            }
            transaction.rollback();
        }
    }
}
