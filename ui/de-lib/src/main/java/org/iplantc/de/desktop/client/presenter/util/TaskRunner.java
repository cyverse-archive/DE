package org.iplantc.de.desktop.client.presenter.util;

import org.iplantc.de.shared.DEProperties;

import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Periodically performs registered tasks.
 */
public class TaskRunner {

    /**
     * The default interval for repeating tasks, in seconds.
     */
    private static final int DEFAULT_INTERVAL = 15;

    /**
     * The interval for repeating tasks, in milliseconds.
     */
    private int interval;

    /**
     * The single instance of this class.
     */
    private static TaskRunner instance;

    /**
     * The list of tasks to perform.
     */
    private final List<Runnable> tasks = new ArrayList<Runnable>();

    /**
     * The timer to use for repeating tasks.
     */
    private final Timer timer;

    /**
     * Used to log failed tasks.
     */
    private final Logger logger = Logger.getLogger("TaskRunner");

    private TaskRunner() {
        // get interval in seconds
        interval = DEProperties.getInstance().getNotificationPollInterval();

        if (interval == 0) {
            interval = DEFAULT_INTERVAL;
        }

        interval *= 1000;

        timer = new Timer() {

            @Override
            public void run() {
                runTasks();
                timer.schedule(interval);
            }
        };

        timer.schedule(interval);
    }

    /**
     * @return the single instance of this class.
     */
    public static TaskRunner getInstance() {
        if (instance == null) {
            instance = new TaskRunner();
        }
        return instance;
    }

    /**
     * Adds a task to the list of tasks to be performed.
     * 
     * @param task the task to add.
     */
    public void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("the task may not be null");
        }
        tasks.add(task);
    }

    /**
     * Removes a task from the list of tasks to be performed.
     * 
     * @param task the task to remove.
     */
    public void removeTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("the task may not be null");
        }
        tasks.remove(task);
    }

    private void runTasks() {
        for (Runnable task : tasks) {
            try {
                task.run();
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, "timed task failed", e);
            }
        }
    }
}
