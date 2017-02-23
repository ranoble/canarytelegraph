package uk.co.tangent.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.hibernate.SessionFactory;

import uk.co.tangent.entities.TestResult;

@Singleton
public class TestResultService {
    private final Provider<SessionFactory> sessionProvider;

    @Inject
    public TestResultService(Provider<SessionFactory> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    protected SessionFactory getSessionFactory() {
        return sessionProvider.get();
    }

    public void saveResults(TestResult testRes) {
        getSessionFactory().getCurrentSession().save(testRes);

    }
}
