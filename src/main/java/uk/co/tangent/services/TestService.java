package uk.co.tangent.services;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.hibernate.SessionFactory;

import uk.co.tangent.entities.Test;

@Singleton
public class TestService {
    private final Provider<SessionFactory> sessionProvider;

    @Inject
    public TestService(Provider<SessionFactory> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    protected SessionFactory getSessionFactory() {
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

        return getSessionFactory().getCurrentSession().load(Test.class, id);

    }

    public List<Test> getTests() {
        return getSessionFactory().getCurrentSession()
                .createCriteria(Test.class).list();
    }

    public Test save(Test test) {

        if (test.getId() == null) {
            getSessionFactory().getCurrentSession().save(test);
        } else {
            test = (Test) getSessionFactory().getCurrentSession().merge(test);
        }

        return test;
    }

    public Test delete(Test test) {
        getSessionFactory().getCurrentSession().delete(test);
        return test;
    }
}
