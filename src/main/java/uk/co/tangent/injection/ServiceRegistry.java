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
    private final Provider<TestService> testService;
    private final Provider<TaskService> tasks;
    private final Provider<LaneService> laneService;
    private final Provider<TestResultService> testResult;
    private final Provider<HibernateBundle<Config>> hibernate;

    @Inject
    public ServiceRegistry(Provider<TestService> testService, Provider<TaskService> tasks, Provider<LaneService> laneService,
                           Provider<TestResultService> testResult, Provider<HibernateBundle<Config>> hibernateBundleProvider) {
        this.testResult = testResult;
        this.testService = testService;
        this.laneService = laneService;
        this.tasks = tasks;
        this.hibernate = hibernateBundleProvider;
    }

    public TestService getTestService() {
        return testService.get();
    }

    public TaskService getTasks() {
        return tasks.get();
    }

    public LaneService getLaneService() {
        return laneService.get();
    }


    public TestResultService getTestResult() {
        return testResult.get();
    }

    public HibernateBundle<Config> getHibernate() {
        return hibernate.get();
    }
}
