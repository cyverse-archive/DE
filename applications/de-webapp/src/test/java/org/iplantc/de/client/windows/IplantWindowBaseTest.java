package org.iplantc.de.client.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.windows.IplantWindowBase;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.button.ToolButton;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GxtMockitoTestRunner.class)
public class IplantWindowBaseTest {

    private IplantWindowBase uut;

    @Before public void setup() {
        uut = new IplantWindowBase("test") {
            @Override
            public WindowState getWindowState() {
                return null;
            }
        };
    }

    @Test public void minimizeFlagSetToFalseWhenWindowShown(){
        uut.minimized = true;

        uut.onShow();
        assertFalse(uut.minimized);
    }

    @Test public void minimizeFlagSetToFalseWhenMaximized(){
        uut.minimized = true;
        uut.isMaximizable = true;
        uut.btnRestore = mock(ToolButton.class);
        uut.getResizable();
        uut.setMaximized(true);
        assertFalse(uut.minimized);
    }

}