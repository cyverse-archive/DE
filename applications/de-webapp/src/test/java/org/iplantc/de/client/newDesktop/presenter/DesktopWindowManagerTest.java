package org.iplantc.de.client.newDesktop.presenter;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.WindowManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class DesktopWindowManagerTest {

    @Mock WindowManager windowManagerMock;

    private DesktopWindowManager uut;

    @Before public void setup() {
        uut = new DesktopWindowManager(windowManagerMock);
    }

    @Test public void showNewWindowCreatesNewWindowTest() {

    }

    @Test public void showExistingWindowShowsExistingWindowTest() {

    }
}
