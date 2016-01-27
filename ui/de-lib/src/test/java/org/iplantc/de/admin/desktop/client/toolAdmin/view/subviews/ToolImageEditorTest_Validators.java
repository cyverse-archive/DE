package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import static org.junit.Assert.*;

import org.iplantc.de.commons.client.validators.UrlValidator;

import com.google.gwt.editor.client.EditorError;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.form.TextField;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author aramsey
 */
@RunWith(GxtMockitoTestRunner.class)
public class ToolImageEditorTest_Validators {

    private TextField urlEditorTest;
    private TextField nameEditorTest;

    @Before
    public void setUp() {
        //Overriding markInvalid -> during testing this method calls
        // the cell which then tries to render and redraw itself causing
        // an exception to get thrown

        nameEditorTest = new TextField() {
            @Override
            protected void markInvalid(List<EditorError> msg) {}
        };
        nameEditorTest.setAllowBlank(false);

        urlEditorTest = new TextField() {
            @Override
            protected void markInvalid(List<EditorError> msg) {}
        };
        urlEditorTest.addValidator(new UrlValidator());
    }

    @Test
    public void testIsValid_urlValidatorPass() {
        urlEditorTest.setValue("http://www.google.com");
        assertTrue(urlEditorTest.isValid());
    }

    @Test
    public void testIsValid_urlValidatorPass_blank() {
        urlEditorTest.setValue("");
        assertTrue(urlEditorTest.isValid());
    }

    @Test
    public void testIsValid_urlValidatorFail() {
        urlEditorTest.setValue("http://");
        assertFalse(urlEditorTest.isValid());
    }

    @Test
    public void testIsValid_namePass() {
        nameEditorTest.setValue("Some name");
        assertTrue(nameEditorTest.isValid());
    }

    @Test
    public void testIsValid_nameFailEmpty() {
        nameEditorTest.setValue("");
        assertFalse(nameEditorTest.isValid());
    }

    @Test
    public void testIsValid_nameFailNull() {
        nameEditorTest.setValue(null);
        assertFalse(nameEditorTest.isValid());
    }
}
