package uk.co.tangent.services;

import io.dropwizard.hibernate.UnitOfWork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.tangent.data.CanaryTest;
import uk.co.tangent.data.steps.Step;
import uk.co.tangent.data.steps.confirmations.FailedResult;
import uk.co.tangent.data.steps.confirmations.Result;
import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.entities.TestResult;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Task {

    @UnitOfWork
    public void startTasks(LaneService laneService, TaskService taskService)
            throws LaneAlreadyRunningException {
        for (Lane lane : laneService.getLanes()) {
            taskService.addLane(lane);
            if (lane.getActive()) {
                taskService.startLane(lane);
            }
        }

    }

    @UnitOfWork
    public void runTask(ObjectMapper objectMapper, LaneService laneService,
            Lane lane, TestResultService testResultService) throws IOException {
        List<Result> testResults = new ArrayList<>();
        Test _test = laneService.loadRandomTest(lane);
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

        TestResult testRes = new TestResult();
        testRes.setTest(_test);
        testRes.setHealthy(healthy);
        testRes.setLane(lane);
        testRes.setResults(objectMapper.writeValueAsString(testResults));

        testResults.clear();

        // Persist results
        testResultService.saveResults(testRes);

        lane.sleep();
    }

}
