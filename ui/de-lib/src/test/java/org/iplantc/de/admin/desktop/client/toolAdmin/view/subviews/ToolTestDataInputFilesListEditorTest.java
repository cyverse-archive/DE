package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

/**
 * @author aramsey
 */
@RunWith(GxtMockitoTestRunner.class)
public class ToolTestDataInputFilesListEditorTest {

    @Mock GridInlineEditing<String> editingMock;
    @Mock Grid<String> gridMock;
    @Mock ListStoreEditor<String> listStoreEditorMock;
    @Mock ListStore<String> listStoreMock;
    @Mock ToolAdminView.ToolAdminViewAppearance appearanceMock;
    @Mock ColumnModel<String> columnModelMock;
    @Mock ColumnConfig<String, String> columnConfigMock;
    @Mock ValueProvider<String, String> valueProviderMock;
    @Mock ModelKeyProvider<String> modelKeyProviderMock;
    @Mock GridSelectionModel<String> gridSelectionModelMock;

    private ToolTestDataInputFilesListEditor uut;

    @Before
    public void setUp() {
        when(appearanceMock.toolTestDataInputFilesWidth()).thenReturn(0);
        when(appearanceMock.toolTestDataInputFilesColumnLabel()).thenReturn("sample");
        when(modelKeyProviderMock.getKey(anyString())).thenReturn("sample");
        when(valueProviderMock.getPath()).thenReturn("sample");
        when(valueProviderMock.getValue(anyString())).thenReturn("sample");
        doReturn(columnConfigMock).when(columnModelMock).getColumn(0);
        when(gridMock.getColumnModel()).thenReturn(columnModelMock);
        when(gridSelectionModelMock.getSelectedItem()).thenReturn("sample");
        when(gridMock.getSelectionModel()).thenReturn(gridSelectionModelMock);
        when(listStoreMock.findModelWithKey(anyString())).thenReturn("sample");

        uut = new ToolTestDataInputFilesListEditor(appearanceMock){
            @Override
            ModelKeyProvider<String> getModelKeyProvider() {
                return modelKeyProviderMock;
            }

            @Override
            ValueProvider<String, String> getValueProvider() {
                return valueProviderMock;
            }

            @Override
            ListStoreEditor<String> getListStoreEditor() {
                return listStoreEditorMock;
            }

            @Override
            ListStore<String> getStringListStore() {
                return listStoreMock;
            }

            @Override
            Grid<String> getStringGrid(ColumnModel<String> columnModel) {
                return gridMock;
            }

            @Override
            GridInlineEditing<String> getStringGridInlineEditing() {
                return editingMock;
            }
        };
        verify(listStoreMock).setAutoCommit(anyBoolean());
        verify(editingMock).addEditor(Matchers.<ColumnConfig<String, String>>any(), Matchers.<TextField>any());
        verify(gridMock.getColumnModel()).getColumn(0);
    }

    @Test
    public void testAddToolTestDataInputFile() {
        String inputFileMock = "";

        /** CALL METHOD UNDER TEST **/
        uut.addToolTestDataInputFile();

        verify(editingMock).cancelEditing();
        verify(listStoreMock).add(anyInt(), eq(inputFileMock));
        verify(listStoreMock).indexOf(inputFileMock);
        verify(editingMock).startEditing(Matchers.<Grid.GridCell>any());

        verifyZeroInteractions(listStoreMock, editingMock);
    }

    @Test
    public void testDeleteToolTestDataInputFile_validSelection() {
        when(gridMock.getSelectionModel().getSelectedItem()).thenReturn("deleteInput");

        /** CALL METHOD UNDER TEST **/
        uut.deleteToolTestDataInputFile();
        verify(listStoreMock).findModelWithKey(eq("deleteInput"));
        verify(listStoreMock).remove(eq(listStoreMock.findModelWithKey("deleteInput")));
    }

    @Test
    public void testDeleteToolTestDataInputFile_invalidSelection() {
        when(gridMock.getSelectionModel().getSelectedItem()).thenReturn(null);
        uut.deleteToolTestDataInputFile();

        verifyZeroInteractions(listStoreMock);
    }

}
