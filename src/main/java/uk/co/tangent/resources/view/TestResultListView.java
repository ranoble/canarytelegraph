package uk.co.tangent.resources.view;

import io.dropwizard.views.View;

import java.util.List;

import uk.co.tangent.entities.TestResult;

public class TestResultListView extends View {

    private List<TestResult> testResults;

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public TestResultListView(
            List<uk.co.tangent.entities.TestResult> testResults) {
        super("test_result.ftl");
        this.testResults = testResults;
    }

}
