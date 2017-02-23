package uk.co.tangent.injection;

import io.dropwizard.hibernate.HibernateBundle;
import uk.co.tangent.Config;
import uk.co.tangent.services.LaneService;
import uk.co.tangent.services.TaskService;
import uk.co.tangent.services.TestResultService;
import uk.co.tangent.services.TestService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ServiceRegistry {
    private final TestService testService;
    private final TaskService tasks;
    private final LaneService laneService;
    private final TestResultService testResult;
    private final HibernateBundle<Config> hibernate;

    @Inject
    public ServiceRegistry(TestService testService, TaskService tasks, LaneService laneService,
                           TestResultService testResult, HibernateBundle<Config> hibernateBundle) {
        this.testResult = testResult;
        this.testService = testService;
        this.laneService = laneService;
        this.tasks = tasks;
        this.hibernate = hibernateBundle;
    }

    public TestService getTestService() {
        return testService;
    }

    public TaskService getTasks() {
        return tasks;
    }

    public LaneService getLaneService() {
        return laneService;
    }


    public TestResultService getTestResult() {
        return testResult;
    }

    public HibernateBundle<Config> getHibernate() {
        return hibernate;
    }
}
