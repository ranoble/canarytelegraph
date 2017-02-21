package uk.co.tangent.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import uk.co.tangent.data.CanaryTest;
import uk.co.tangent.data.steps.Step;
import uk.co.tangent.data.steps.confirmations.FailedResult;
import uk.co.tangent.data.steps.confirmations.Result;
import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.entities.TestResult;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class TaskService {

    Map<Lane, CompletableFuture<?>> tasks = new HashMap<>();
    private ObjectMapper objectMapper;

    protected abstract Session getSession();

    @Deprecated
    /**
     * This is here to allow the hibernate session state to be managed in the thread. 
     * We can remove this if we move to a simple immutable message queue.
     * @return SessionFactory
     */
    protected abstract SessionFactory getSessionFactory();

    public TaskService(TestResultService testResultService) {
        objectMapper = new ObjectMapper();
    }

    public void addLane(Lane lane) {
        tasks.put(lane, CompletableFuture.completedFuture(new ArrayList<>()));
    }

    // TODO: This needs to be rewtitten to use simple immutable messages.
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
                        for (;;) {
                            try (Session session = getSessionFactory()
                                    .openSession()) {
                                Transaction transaction = session
                                        .beginTransaction();
                                TestResultService testResultService = new TestResultService() {
                                    @Override
                                    protected Session getSession() {
                                        return session;
                                    }
                                };
                                LaneService laneService = new LaneService() {
                                    @Override
                                    protected Session getSession() {
                                        return session;
                                    }
                                };

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

                                testResultService.saveResults(testRes);

                                transaction.commit();
                                testResults.clear();
                                lane.sleep();
                            }

                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
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
