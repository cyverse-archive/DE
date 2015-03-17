package org.iplantc.de.commons.client.requests;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Periodically sends requests to a URL in order to keep the user's session alive.
 *
 * @author Dennis Roberts
 */
public class KeepaliveTimer {

    /**
     * The number of milliseconds in a minute.
     */
    public static final int MILLISECONDS_PER_MINUTE = 60000;

    /**
     * The single instance of this class.
     */
    private static KeepaliveTimer instance;

    /**
     * The actual timer used to send requests.
     */
    private PingTimer timer;

    /**
     * The default constructor.
     */
    private KeepaliveTimer() {
    }

    /**
     * @return the single instance of this class.
     */
    public static KeepaliveTimer getInstance() {
        if (instance == null) {
            instance = new KeepaliveTimer();
        }
        return instance;
    }

    public void start(String url, int interval) {
        clearTimer();
        initTimer(url, interval);
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        clearTimer();
    }

    /**
     * Initializes the timer.
     *
     * @param url the URL to ping.
     * @param interval the number of minutes between pings.
     */
    private void initTimer(String url, int interval) {
        timer = new PingTimer(url);
        timer.scheduleRepeating(interval * MILLISECONDS_PER_MINUTE);
    }

    /**
     * Clears the current timer.
     */
    private void clearTimer() {
        if (timer != null) {
            timer.cancel();
            timer.cleanUp();
            timer = null;
        }
    }

    /**
     * The class used to time the ping requests.
     */
    private class PingTimer extends Timer {

        /**
         * The frame to use when sending keepalive requests.
         */
        private Frame frame;

        /**
         * @param url the URL to send keepalive requests to.
         */
        public PingTimer(String url) {
            frame = new Frame(url);
            frame.setVisible(false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            frame.removeFromParent();
            RootPanel.get().add(frame);
        }

        /**
         * Removes the frame used to send keepalive requests from its parent.
         */
        public void cleanUp() {
            frame.removeFromParent();
        }
    }
}
