package uk.co.tangent.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import uk.co.tangent.entities.Test;
import uk.co.tangent.entities.TestResult;

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

    public String getResultsPath(Test test) {
        return String.format("%s/results", test.getPath());
    }

    public List<TestResult> getTestResults(Test test, int page, int limit,
            Optional<Date> since, Optional<Date> until) {
        Criteria criteria = getResultsCriteria(test, since, until);
        criteria.addOrder(Order.desc("written"));
        criteria.setFirstResult(page * limit);
        criteria.setMaxResults(limit);
        return criteria.list();
    }

    public Long getTestResultsCount(Test test, Optional<Date> since,
            Optional<Date> until) {
        return (Long) getResultsCriteria(test, since, until).setProjection(
                Projections.rowCount()).uniqueResult();
    }

    private Criteria getResultsCriteria(Test test, Optional<Date> since,
            Optional<Date> until) {
        Criteria criteria = getSession().createCriteria(TestResult.class);
        criteria.add(Restrictions.eq("test", test));
        if (since.isPresent()) {
            criteria.add(Restrictions.ge("written", since.get()));
        }
        if (until.isPresent()) {
            criteria.add(Restrictions.le("written", until.get()));
        }
        return criteria;
    }
}
