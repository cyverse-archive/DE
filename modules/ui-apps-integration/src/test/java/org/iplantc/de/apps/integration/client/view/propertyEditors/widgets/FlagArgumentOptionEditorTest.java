package org.iplantc.de.apps.integration.client.view.propertyEditors.widgets;

import org.iplantc.de.resources.client.constants.IplantValidationConstants;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;

import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class FlagArgumentOptionEditorTest {

    private FlagArgumentOptionEditor uut;

    @Mock TextField checkedArgOptionMock,
        checkedValueMock,
        unCheckedArgOptionMock,
        unCheckedValueMock;
    @Mock IplantValidationConstants validationConstantsMock;

    @Before public void setUp() throws Exception {

        when(validationConstantsMock.restrictedCmdLineChars()).thenReturn("restrictedChars");
        uut = new FlagArgumentOptionEditor(checkedArgOptionMock,
                                           checkedValueMock,
                                           unCheckedArgOptionMock,
                                           unCheckedValueMock,
                                           validationConstantsMock);
        // Perform initialization verifications
        verify(checkedArgOptionMock, times(2)).addValidator(any(Validator.class));
        verify(unCheckedArgOptionMock, times(2)).addValidator(any(Validator.class));

        verify(checkedValueMock).addValidator(any(Validator.class));
        verify(unCheckedValueMock).addValidator(any(Validator.class));

        verify(checkedArgOptionMock).addInvalidHandler(any(InvalidEvent.InvalidHandler.class));
        verify(checkedValueMock).addInvalidHandler(any(InvalidEvent.InvalidHandler.class));
        verify(unCheckedArgOptionMock).addInvalidHandler(any(InvalidEvent.InvalidHandler.class));
        verify(unCheckedValueMock).addInvalidHandler(any(InvalidEvent.InvalidHandler.class));

        verify(checkedArgOptionMock).addValueChangeHandler(any(ValueChangeHandler.class));
        verify(checkedValueMock).addValueChangeHandler(any(ValueChangeHandler.class));
        verify(unCheckedArgOptionMock).addValueChangeHandler(any(ValueChangeHandler.class));
        verify(unCheckedValueMock).addValueChangeHandler(any(ValueChangeHandler.class));
    }

    /**
     * CORE-6311
     * @throws Exception
     */
    @Test public void editorClearsPreviousValuesWhenNewValueIsNull() throws Exception {
        uut.setValue(null);

        verify(checkedArgOptionMock).clear();
        verify(checkedValueMock).clear();
        verify(unCheckedArgOptionMock).clear();
        verify(unCheckedValueMock).clear();

        verify(checkedArgOptionMock).setValue(eq(""));
        verify(checkedValueMock).setValue(eq(""));
        verify(unCheckedArgOptionMock).setValue(eq(""));
        verify(unCheckedValueMock).setValue(eq(""));

        verifyNoMoreInteractions(checkedArgOptionMock, checkedValueMock, unCheckedArgOptionMock, unCheckedValueMock);
    }
}