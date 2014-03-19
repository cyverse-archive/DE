package org.iplantc.de.apps.integration.client.presenter;

import org.iplantc.de.apps.integration.client.events.DeleteArgumentGroupEvent;
import org.iplantc.de.apps.integration.client.view.AppsEditorView;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.UUIDServiceAsync;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.uiapps.integration.AppIntegrationErrorMessages;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class AppsEditorPresenterImplTest {

    @Mock private AppsEditorView mockView;
    @Mock private EventBus mockEventBus;
    @Mock private AppTemplateServices mockAppTemplateService;
    @Mock private AppIntegrationErrorMessages mockErrorMessages;
    @Mock private IplantDisplayStrings mockDisplayStrings;
    @Mock private UUIDServiceAsync mockUuidService;
    @Mock private AppTemplateWizardAppearance mockAppearance;
    @Mock private IplantAnnouncer mockAnnouncer;

    private AppsEditorPresenterImpl uut;

    @Before public void setUp() {
        uut = new AppsEditorPresenterImpl(mockView, mockEventBus, mockAppTemplateService, mockErrorMessages, mockDisplayStrings, mockUuidService, mockAppearance, mockAnnouncer);
    }

    @Test public void testDoArgumentGroupDelete() {
        AppTemplate mockAppTemplate = mock(AppTemplate.class);
        when(mockView.flush()).thenReturn(mockAppTemplate);
        when(mockAppTemplate.getArgumentGroups()).thenReturn(Lists.newArrayList(mock(ArgumentGroup.class)));

        DeleteArgumentGroupEvent mockEvent = mock(DeleteArgumentGroupEvent.class);
        uut.doArgumentGroupDelete(mockEvent);

        verify(mockAnnouncer).schedule(any(ErrorAnnouncementConfig.class));

        verifyZeroInteractions(mockEventBus, mockAppTemplateService, mockUuidService, mockAppearance);
        verifyNoMoreInteractions(mockAnnouncer);
    }

}
