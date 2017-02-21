package uk.co.tangent.services;

import java.util.List;

import org.hibernate.Session;

import uk.co.tangent.entities.Test;

public abstract class TestService {
    protected abstract Session getSession();

    public TestService() {
    }

    public String getPath(Test test) {
        return test.getPath();
    }

    public Test fromPath(String testPath) {
        Long id = Long.parseLong(testPath.substring("/test/".length()));
        return getTest(id);
    }

    public Test getTest(Long id) {
        return getSession().load(Test.class, id);
    }

    public List<Test> getTests() {
        return getSession().createCriteria(Test.class).list();
    }

    public Test save(Test test) {
        if (test.getId() == null) {
            getSession().save(test);
        } else {
            test = (Test) getSession().merge(test);
        }

        return test;
    }

    public Test delete(Test test) {
        getSession().delete(test);
        return test;
    }
}
