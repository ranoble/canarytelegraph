package uk.co.tangent.injection;

import io.dropwizard.hibernate.HibernateBundle;
import uk.co.tangent.Config;
import uk.co.tangent.services.LaneService;
import uk.co.tangent.services.TaskService;
import uk.co.tangent.services.TestResultService;
import uk.co.tangent.services.TestService;

public class ServiceRegistry {
    private TestService testService;
    private TaskService tasks;
    private LaneService laneService;
    private TestResultService testResult;
    private HibernateBundle<Config> hibernate;

    public TestService getTestService() {
        return testService;
    }

    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    public TaskService getTasks() {
        return tasks;
    }

    public void setTasks(TaskService tasks) {
        this.tasks = tasks;
    }

    public LaneService getLaneService() {
        return laneService;
    }

    public void setLaneService(LaneService laneService) {
        this.laneService = laneService;
    }

    public TestResultService getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResultService testResult) {
        this.testResult = testResult;
    }

    public HibernateBundle<Config> getHibernate() {
        return hibernate;
    }

    public void setHibernate(HibernateBundle<Config> hibernate) {
        this.hibernate = hibernate;
    }
}
