package uk.co.tangent.services;

import io.dropwizard.hibernate.HibernateBundle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import uk.co.tangent.Config;
import uk.co.tangent.entities.TestResult;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class TestResultService {
    private final Provider<Session> sessionProvider;

    @Inject
    public TestResultService(Provider<Session> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    protected Session getSession() {
        return sessionProvider.get();
    }

    public void saveResults(TestResult testRes) {
        try (Session session = getSession()) {
            session.save(testRes);
        }
    }
}
