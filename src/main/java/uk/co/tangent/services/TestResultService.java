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
    private final Provider<HibernateBundle<Config>> hibernateBundleProvider;

    @Inject
    public TestResultService(Provider<Session> sessionProvider, Provider<HibernateBundle<Config>> hibernateBundleProvider) {
        this.sessionProvider = sessionProvider;
        this.hibernateBundleProvider = hibernateBundleProvider;
    }

    public SessionFactory getSessionFactory() {
        return hibernateBundleProvider.get().getSessionFactory();
    }

    protected Session getSession() {
        return sessionProvider.get();
    }

    public void saveResults(Session session, TestResult testRes) {
        session.save(testRes);
    }
}
