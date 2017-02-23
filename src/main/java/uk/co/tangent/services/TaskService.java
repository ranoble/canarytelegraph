package uk.co.tangent.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.tangent.data.CanaryTest;
import uk.co.tangent.data.steps.Step;
import uk.co.tangent.data.steps.confirmations.FailedResult;
import uk.co.tangent.data.steps.confirmations.Result;
import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.entities.TestResult;
import uk.co.tangent.jmx.JMXBean;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
@JMXBean
public class TaskService implements TaskServiceMXBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private Map<Lane, CompletableFuture<?>> tasks = new HashMap<>();
    private ObjectMapper objectMapper;
    private final Provider<Session> sessionProvider;
    private final LaneService laneService;
    private final TestResultService testResultService;

    protected Session getSession() {
        return sessionProvider.get();
    }

    @Inject
    public TaskService(Provider<Session> sessionProvider, LaneService laneService, TestResultService testResultService) {
        objectMapper = new ObjectMapper();
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
        tasks.put(
                lane,
                CompletableFuture.runAsync(() -> {
                    try {
                        List<Result> testResults = new ArrayList<>();
                        while (!Thread.currentThread().isInterrupted()) {
                            Test _test = laneService.loadRandomTest(lane);
                            LOGGER.info("Loaded Test");
                            CanaryTest test = lane.parse(_test);
                            lane.applyBindings(test);
                            boolean healthy = true;

                            for (Step step : test.getSteps()) {
                                List<Result> stepResults = step.call();
                                for (Result result : stepResults) {
                                    result.setStep(step.getName());
                                    result.setTest(test.getName());
                                }
                                testResults.addAll(stepResults);
                            }
                            for (Result result : testResults) {
                                if (result instanceof FailedResult) {
                                    healthy = false;
                                }
                            }
                            LOGGER.info("Steps complete");

                            TestResult testRes = new TestResult();
                            testRes.setTest(_test);
                            testRes.setHealthy(healthy);
                            testRes.setLane(lane);
                            testRes.setResults(objectMapper
                                    .writeValueAsString(testResults));
                            testResults.clear();

                            // Persist results
                            testResultService.saveResults(testRes);

                            lane.sleep();
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error running task", e);
                    }
                }));
    }

    public void stopLane(Lane lane) {
        tasks.get(lane).cancel(true);
    }

    @Override
    public int getNumberOfTasks() {
        return tasks.size();
    }

    @Override
    public Map<Long, String> getAllTasks() {
        return tasks.keySet().stream().collect(Collectors.toMap(Lane::getId, Lane::getName));
    }

    public void stopAll() {
        for (Entry<Lane, CompletableFuture<?>> lanes : tasks.entrySet()) {
            lanes.getValue().cancel(true);
        }
    }

    @Override
    public void stop(int id) {
        tasks.keySet().stream().filter(x -> x.getId().intValue() == id).findFirst().ifPresent(this::stopLane);
    }
}
