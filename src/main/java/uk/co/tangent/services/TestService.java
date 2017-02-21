package uk.co.tangent.services;

import org.hibernate.Session;
import uk.co.tangent.entities.Test;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class TestService {
    private final Provider<Session> sessionProvider;

    @Inject
    public TestService(Provider<Session> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    protected Session getSession() {
        return sessionProvider.get();
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
