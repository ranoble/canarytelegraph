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
        try (Session session = getSession()) {
            return session.load(Test.class, id);
        }
    }

    public List<Test> getTests() {
        try (Session session = getSession()) {
            return session.createCriteria(Test.class).list();
        }
    }

    public Test save(Test test) {
        try (Session session = getSession()) {

            if (test.getId() == null) {
                session.save(test);
            } else {
                test = (Test) session.merge(test);
            }
        }

        return test;
    }

    public Test delete(Test test) {
        try (Session session = getSession()) {
            session.delete(test);
        }
        return test;
    }
}
