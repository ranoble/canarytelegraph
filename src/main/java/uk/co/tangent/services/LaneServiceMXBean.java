package uk.co.tangent.services;

import uk.co.tangent.entities.Lane;

import java.util.List;
import java.util.Set;

/**
 * Created by sgyurko on 22/02/2017.
 */
public interface LaneServiceMXBean {
    List<Set<String>> getAllLanes();
    int getLaneCount();
}
