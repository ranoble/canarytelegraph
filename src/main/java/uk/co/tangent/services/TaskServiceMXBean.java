package uk.co.tangent.services;

import java.util.Map;

/**
 * Created by sgyurko on 22/02/2017.
 */
public interface TaskServiceMXBean {

    int getNumberOfTasks();

    Map<Long, String> getAllTasks();

    void stopAll();
    void stop(int id);
}
