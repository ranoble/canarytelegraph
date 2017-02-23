package uk.co.tangent.services;

import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.tangent.entities.Lane;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class TaskService {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TaskService.class);

    private Map<Lane, CompletableFuture<?>> tasks = new HashMap<>();
    private ObjectMapper objectMapper;
    private final Provider<SessionFactory> sessionProvider;
    private final LaneService laneService;
    private final TestResultService testResultService;

    private Provider<UnitOfWorkAwareProxyFactory> unitOfWorkFactoryProvider;

    protected SessionFactory getSessionFactory() {
        return sessionProvider.get();
    }

    protected UnitOfWorkAwareProxyFactory getUnitOfWorkFactory() {
        return unitOfWorkFactoryProvider.get();
    }

    @Inject
    public TaskService(Provider<SessionFactory> sessionProvider,
            Provider<UnitOfWorkAwareProxyFactory> unitOfWorkFactoryProvider,
            LaneService laneService, TestResultService testResultService) {
        objectMapper = new ObjectMapper();
        this.unitOfWorkFactoryProvider = unitOfWorkFactoryProvider;
        this.sessionProvider = sessionProvider;
        this.testResultService = testResultService;
        this.laneService = laneService;
    }

    public void addLane(Lane lane) {
        tasks.put(lane, CompletableFuture.completedFuture(new ArrayList<>()));
    }

    // TODO: This needs to be rewritten to use simple immutable messages.
    // Horrible, but works for a pre-alpha.
    public void startLane(Lane lane) throws LaneAlreadyRunningException {
        CompletableFuture<?> future = tasks.get(lane);
        if (!future.isDone()) {
            throw new LaneAlreadyRunningException(String.format(
                    "Lane: %s is currently running", lane.getName()));
        }
        tasks.put(lane, CompletableFuture.runAsync(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Task task = getUnitOfWorkFactory().create(Task.class);
                    task.runTask(objectMapper, laneService, lane,
                            testResultService);
                }
            } catch (Exception e) {
                LOGGER.error("Error running task", e);
            }
        }));

    }

    public void stopLane(Lane lane) {
        tasks.get(lane).cancel(true);
    }

    public void stopAll() {
        for (Entry<Lane, CompletableFuture<?>> lanes : tasks.entrySet()) {
            lanes.getValue().cancel(true);
        }
    }

    public void startActiveLanes() throws LaneAlreadyRunningException {
        Task task = getUnitOfWorkFactory().create(Task.class);
        task.startTasks(laneService, this);

    }
}
