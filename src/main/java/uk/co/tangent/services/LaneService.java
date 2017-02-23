package uk.co.tangent.services;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import uk.co.tangent.entities.Lane;
import uk.co.tangent.entities.Test;
import uk.co.tangent.jmx.JMXBean;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Singleton
@JMXBean
public class LaneService implements LaneServiceMXBean {
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
            return getSession().load(Lane.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Lane> getLanes() {
            return getSession().createCriteria(Lane.class).list();
    }

    @SuppressWarnings("unchecked")
    public List<Test> loadTests(Lane lane) {
            Criteria cr = getSession().createCriteria(Test.class);
            cr.add(Restrictions.eq("lane", lane));
            return cr.list();
    }

    public Lane save(Lane lane) {
        if (lane.getId() == null) {
                getSession().save(lane);
            /*
             * TODO: Readd
             */
            // tasks.addLane(lane);
        } else {
            lane = (Lane) getSession().merge(lane);
        }

        return lane;
    }

    public Test loadRandomTest(Lane lane) {
            Random random = new Random();
            lane = getSession().load(Lane.class, lane.getId());
            List<Test> tests = lane.getTests();
            int testIndex = random.nextInt(tests.size());
            return tests.get(testIndex);
    }

    public String getPath(Lane lane) {
        // TODO Auto-generated method stub
        return String.format("/lane/%d", lane.getId());
    }

    public Lane fromPath(String path) {
        Long id = Long.parseLong(path.substring("/lane/".length()));
        return getLane(id);
    }

    @Override
    public List<Set<String>> getAllLanes() {
        List<Lane> lanes = getLanes();
        List<Set<String>> result = new ArrayList<>(lanes.size());
        for (Lane lane : lanes) {
            result.add(lane.serializeTests());
        }
        return result;
    }

    @Override
    public int getLaneCount() {
        return getLanes().size();
    }
}
