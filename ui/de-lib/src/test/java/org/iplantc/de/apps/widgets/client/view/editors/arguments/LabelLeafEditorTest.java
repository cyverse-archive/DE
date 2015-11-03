package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.ArgumentType;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class LabelLeafEditorTest {

    @Mock private AppTemplateWizardAppearance appearanceMock;
    @Mock private ArgumentEditor argEditorMock;
    @Mock private HasSafeHtml hasHtmlMock;
    
    @Mock private LeafValueEditor<Boolean> requiredEditorMock;
    @Mock private LeafValueEditor<String> labelEditorMock;
    @Mock private LeafValueEditor<ArgumentType> typeEditorMock;
    @Mock private LeafValueEditor<String> idEditorMock;
    @Mock private LeafValueEditor<String> descriptionEditorMock;

    private LabelLeafEditor<String> uut;

    @Before public void setUp() {
        uut = new LabelLeafEditor<String>(hasHtmlMock, argEditorMock, appearanceMock);
        when(argEditorMock.requiredEditor()).thenReturn(requiredEditorMock);
        when(argEditorMock.labelEditor()).thenReturn(labelEditorMock);
        when(argEditorMock.typeEditor()).thenReturn(typeEditorMock);
        when(argEditorMock.idEditor()).thenReturn(idEditorMock);
        when(argEditorMock.descriptionEditor()).thenReturn(descriptionEditorMock);
    }

    @Test public void testSetValue_InfoArgument() {
        when(requiredEditorMock.getValue()).thenReturn(false);
        when(labelEditorMock.getValue()).thenReturn("mock value");
        when(typeEditorMock.getValue()).thenReturn(ArgumentType.Info);
        when(appearanceMock.sanitizeHtml(anyString())).thenReturn(SafeHtmlUtils.EMPTY_SAFE_HTML);
        
        // Call unit under test
        uut.setValue("mock label");

        verify(requiredEditorMock).getValue();
        verify(typeEditorMock).getValue();
        verify(appearanceMock).sanitizeHtml("mock value");

        verifyNoMoreInteractions(idEditorMock, descriptionEditorMock, appearanceMock);
    }
    
    
}
