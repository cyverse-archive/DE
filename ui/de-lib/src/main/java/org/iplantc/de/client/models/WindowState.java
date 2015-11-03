package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.autobean.shared.Splittable;

public interface WindowState {

    WindowType getConfigType();

    void setConfigType(WindowType type);

    boolean isMaximized();

    boolean isMinimized();

    @PropertyName("win_left")
    int getWinLeft();

    @PropertyName("win_top")
    int getWinTop();

    int getWidth();

    int getHeight();

    Splittable getWindowConfig();
    
    void setMaximized(boolean maximized);
    
    void setMinimized(boolean minimized);
    
    @PropertyName("win_left")
    void setWinLeft(int winLeft);
    
    @PropertyName("win_top")
    void setWinTop(int winTop);
    
    void setWidth(int width);
    
    void setHeight(int height);
    
    void setWindowConfig(Splittable config);
    
}
