package uk.co.tangent;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import javax.inject.Inject;

import uk.co.tangent.entities.Lane;
import uk.co.tangent.injection.ServiceRegistry;
import uk.co.tangent.resources.LaneResource;
import uk.co.tangent.resources.StatusResource;
import uk.co.tangent.resources.TestResource;
import uk.co.tangent.services.LaneAlreadyRunningException;
import uk.co.tangent.services.LaneService;
import uk.co.tangent.services.TaskService;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class App extends Application<Config> {

    /**
     * Container for services. Just so we dont need to pass them around much.
     */
    private final ServiceRegistry services;
    private final LaneResource laneResource;
    private final TestResource testResource;
    private final StatusResource statusResource;

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new CanaryModule());
        injector.getInstance(App.class).run(args);
    }

    @Inject
    public App(ServiceRegistry serviceRegistry, LaneService laneService,
            TaskService taskService, LaneResource laneResource,
            TestResource testResource, StatusResource statusResource) {
        services = serviceRegistry;
        this.laneResource = laneResource;
        this.testResource = testResource;
        this.statusResource = statusResource;
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
        bootstrap.getObjectMapper().disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        bootstrap.addBundle(services.getSwagger());
    }

    @Override
    public void run(Config config, Environment env) throws Exception {
        env.jersey().register(laneResource);
        env.jersey().register(testResource);
        env.jersey().register(statusResource);
        registerAndRunLanes();
    }

    private void registerAndRunLanes() throws LaneAlreadyRunningException {
        for (Lane lane : services.getLaneService().getLanes()) {
            services.getTaskService().addLane(lane);
            if (lane.getActive()) {
                services.getTaskService().startLane(lane);
            }
        }
    }
}
