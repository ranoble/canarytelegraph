package uk.co.tangent.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.HibernateBundle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import uk.co.tangent.Config;
import uk.co.tangent.data.CanaryTest;
import uk.co.tangent.data.steps.Step;
import uk.co.tangent.data.steps.confirmations.FailedResult;
import uk.co.tangent.data.steps.confirmations.Result;
import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.entities.TestResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

@Singleton
public class TaskService {

    Map<Lane, CompletableFuture<?>> tasks = new HashMap<>();
    private ObjectMapper objectMapper;
    private final HibernateBundle<Config> hibernateBundle;
    private final LaneService laneService;
    private final TestResultService testResultService;

    protected Session getSession() {
        return hibernateBundle.getSessionFactory().getCurrentSession();
    }

    protected SessionFactory getSessionFactory() {
        return hibernateBundle.getSessionFactory();
    }

    @Inject
    public TaskService(HibernateBundle<Config> hibernateBundle, LaneService laneService, TestResultService testResultService) {
        objectMapper = new ObjectMapper();
        this.hibernateBundle = hibernateBundle;
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
                            try (Session session = getSessionFactory()
                                    .openSession()) {
                                Transaction transaction = session
                                        .beginTransaction();

                                Test _test = laneService.loadRandomTest(lane);
                                System.out.println("Loaded Test");
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
                                System.out.println("Steps complete");

                                TestResult testRes = new TestResult();
                                testRes.setTest(_test);
                                testRes.setHealthy(healthy);
                                testRes.setLane(lane);
                                testRes.setResults(objectMapper
                                        .writeValueAsString(testResults));

                                testResultService.saveResults(session, testRes);

                                transaction.commit();
                                testResults.clear();
                                lane.sleep();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

}
