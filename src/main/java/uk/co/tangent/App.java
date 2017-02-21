package uk.co.tangent;

import com.fasterxml.jackson.databind.InjectableValues;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import uk.co.tangent.entities.Lane;
import uk.co.tangent.injection.ServiceRegistry;
import uk.co.tangent.resources.LaneResource;
import uk.co.tangent.resources.TestResource;
import uk.co.tangent.services.LaneAlreadyRunningException;
import uk.co.tangent.services.LaneService;
import uk.co.tangent.services.TaskService;

import javax.inject.Inject;

public class App extends Application<Config> {

    /**
     * Container for services. Just so we dont need to pass them around much.
     */
    private final ServiceRegistry services;
    private final LaneService laneService;
    private final TaskService taskService;

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new CanaryModule());
        injector.getInstance(App.class).run(args);
    }

    @Inject
    public App(ServiceRegistry serviceRegistry, LaneService laneService, TaskService taskService) {
        services = serviceRegistry;
        this.taskService = taskService;
        this.laneService = laneService;
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
        for (Lane lane : laneService.getLanes()) {
            taskService.addLane(lane);
            if (lane.getActive()) {
                taskService.startLane(lane);
            }
        }
    }
}
