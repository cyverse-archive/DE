package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.event.SelectEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * @author aramsey
 */
@RunWith(GxtMockitoTestRunner.class)
public class ToolContainerEditorTest {

    @Mock ToolAdminView.ToolAdminViewAppearance appearanceMock;
    @Mock ToolDeviceListEditor toolDeviceListEditorMock;
    @Mock ToolVolumeListEditor toolVolumeListEditorMock;
    @Mock ToolVolumesFromListEditor toolVolumesFromListEditorMock;
    @Mock ToolImageEditor toolImageEditorMock;
    @Mock SelectEvent selectEventMock;

    private ToolContainerEditor uut;

    @Before
    public void setUp() {
        uut = new ToolContainerEditor(appearanceMock,
                                      toolDeviceListEditorMock,
                                      toolVolumeListEditorMock,
                                      toolVolumesFromListEditorMock,
                                      toolImageEditorMock);
    }

    @Test
    public void testOnAddDeviceButtonClicked() {
        uut.onAddDeviceButtonClicked(selectEventMock);
        verify(toolDeviceListEditorMock).addDevice();
    }

    @Test
    public void testOnDeleteDeviceButtonClicked() {
        uut.onDeleteDeviceButtonClicked(selectEventMock);
        verify(toolDeviceListEditorMock).deleteDevice();
    }

    @Test
    public void testOnAddVolumesButtonClicked() {
        uut.onAddVolumesButtonClicked(selectEventMock);
        verify(toolVolumeListEditorMock).addVolume();
    }

    @Test
    public void testOnDeleteVolumesButtonClicked() {
        uut.onDeleteVolumesButtonClicked(selectEventMock);
        verify(toolVolumeListEditorMock).deleteVolume();
    }

    @Test
    public void testOnAddVolumesFromButtonClicked() {
        uut.onAddVolumesFromButtonClicked(selectEventMock);
        verify(toolVolumesFromListEditorMock).addVolumesFrom();
    }

    @Test
    public void testOnDeleteVolumesFromButtonClicked() {
        uut.onDeleteVolumesFromButtonClicked(selectEventMock);
        verify(toolVolumesFromListEditorMock).deleteVolumesFrom();
    }

    @Test
    public void testIsValid_bothValid() {
        when(toolImageEditorMock.isValid()).thenReturn(true);
        when(toolVolumesFromListEditorMock.isValid()).thenReturn(true);
        assertEquals(true, uut.isValid());
    }

    @Test
    public void testIsValid_imageValid() {
        when(toolImageEditorMock.isValid()).thenReturn(true);
        when(toolVolumesFromListEditorMock.isValid()).thenReturn(false);
        assertEquals(false, uut.isValid());
    }

    @Test
    public void testIsValid_volumesFromValid() {
        when(toolImageEditorMock.isValid()).thenReturn(false);
        when(toolVolumesFromListEditorMock.isValid()).thenReturn(true);
        assertEquals(false, uut.isValid());
    }

    @Test
    public void testIsValid_bothInvalid() {
        when(toolImageEditorMock.isValid()).thenReturn(false);
        when(toolVolumesFromListEditorMock.isValid()).thenReturn(false);
        assertEquals(false, uut.isValid());
    }
}
