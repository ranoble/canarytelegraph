package uk.co.tangent.resources.view;

import io.dropwizard.views.View;

import java.util.List;

import uk.co.tangent.entities.Test;

public class TestListView extends View {

    private List<Test> tests;

    public List<Test> getTests() {
        return tests;
    }

    public TestListView(List<Test> list) {
        super("test.ftl");
        this.tests = list;
    }

}
