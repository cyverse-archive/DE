package org.iplantc.de.client.models;

import java.util.List;

public interface UserSession {
    List<WindowState> getWindowStates();

    void setWindowStates(List<WindowState> windowStates);

}