package uk.co.tangent.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import uk.co.tangent.entities.TestResult;

public abstract class TestResultService {
    SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected abstract Session getSession();

    public TestResultService() {
    }

    public void saveResults(TestResult testRes) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.save(testRes);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }

        }
    }
}
