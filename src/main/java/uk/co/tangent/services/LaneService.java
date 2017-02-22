package uk.co.tangent.services;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;
import java.util.Random;

@Singleton
public class LaneService {
    private final Provider<Session> sessionProvider;

    protected Session getSession() {
        return sessionProvider.get();
    }

    // private TaskService tasks;

    @Inject
    public LaneService(Provider<Session> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public Lane getLane(Long id) {
        try (Session session = getSession()) {
            return session.load(Lane.class, id);
        }
    }

    public List<Lane> getLanes() {
        try (Session session = getSession()) {
            return session.createCriteria(Lane.class).list();
        }
    }

    public List<Test> loadTests(Lane lane) {
        try (Session session = getSession()) {
            Criteria cr = session.createCriteria(Test.class);
            cr.add(Restrictions.eq("lane", lane));
            return cr.list();
        }
    }

    public Lane save(Lane lane) {
        try (Session session = getSession()) {
            if (lane.getId() == null) {
                    session.save(lane);
                /**
                 * TODO: Readd
                 */
                // tasks.addLane(lane);
            } else {
                lane = (Lane) session.merge(lane);
            }
        }

        return lane;
    }

    public Test loadRandomTest(Lane lane) {
        try (Session session = getSession()) {
            Random random = new Random();
            lane = session.load(Lane.class, lane.getId());
            List<Test> tests = lane.getTests();
            int testIndex = random.nextInt(tests.size());
            return tests.get(testIndex);
        }
    }

    public String getPath(Lane lane) {
        // TODO Auto-generated method stub
        return String.format("/lane/%d", lane.getId());
    }

    public Lane fromPath(String path) {
        Long id = Long.parseLong(path.substring("/lane/".length()));
        return getLane(id);
    }
}
