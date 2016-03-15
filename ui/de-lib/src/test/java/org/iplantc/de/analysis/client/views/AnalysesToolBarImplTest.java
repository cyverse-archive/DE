package org.iplantc.de.analysis.client.views;

import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.CANCELED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.COMPLETED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.FAILED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.HELD;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.IDLE;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.REMOVED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.RUNNING;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.SUBMISSION_ERR;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.SUBMITTED;
import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.UNKNOWN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(GxtMockitoTestRunner.class)
public class AnalysesToolBarImplTest {
    @Mock SelectionChangedEvent<Analysis> mockSelectionEvent;

    @Mock MenuItem goToFolderMiMock;
    @Mock MenuItem viewParamsMiMock;
    @Mock MenuItem relaunchMock;
    @Mock MenuItem cancelMiMock;
    @Mock MenuItem deleteMiMock;
    @Mock MenuItem renameMiMock;
    @Mock MenuItem updateCommentsMiMock;
    @Mock AnalysesView.Appearance appearanceMock;
    @Mock PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loaderMock;
    @Mock AnalysesView.Presenter presenterMock;
    @Mock
    UserInfo mockUserInfo;
    List<Analysis> currentSelectionMock;

    @Mock
    TextButton share_menuMock;
    @Mock
    MenuItem shareCollabMIMock;
    //@Mock
    //MenuItem shareSupportMIMock;

    private AnalysesToolBarImpl uut;

    @Before public void setUp() {
        uut = new AnalysesToolBarImpl(appearanceMock,
                                      presenterMock,
                                      loaderMock);
        currentSelectionMock =    spy(new ArrayList<Analysis>());
        mockMenuItems(uut);
    }
    void mockMenuItems(AnalysesToolBarImpl uut){
        uut.goToFolderMI = goToFolderMiMock;
        uut.viewParamsMI = viewParamsMiMock;
        uut.relaunchMI = relaunchMock;
        uut.cancelMI = cancelMiMock;
        uut.deleteMI = deleteMiMock;
        uut.renameMI = renameMiMock;
        uut.updateCommentsMI = updateCommentsMiMock;
        uut.currentSelection = currentSelectionMock;
        uut.share_menu = share_menuMock;
        uut.shareCollabMI = shareCollabMIMock;
      //  uut.shareSupportMI = shareSupportMIMock;
        uut.userInfo = mockUserInfo;
    }

    @Test public void testOnSelectionChanged_ZeroSelected() {
        when(mockSelectionEvent.getSelection()).thenReturn(Collections.<Analysis>emptyList());
        uut.onSelectionChanged(mockSelectionEvent);

        verify(goToFolderMiMock).setEnabled(eq(false));
        verify(viewParamsMiMock).setEnabled(eq(false));
        verify(relaunchMock).setEnabled(eq(false));
        verify(cancelMiMock).setEnabled(eq(false));
        verify(deleteMiMock).setEnabled(eq(false));
        verify(renameMiMock).setEnabled(eq(false));
        verify(updateCommentsMiMock).setEnabled(eq(false));
        verify(share_menuMock).setEnabled(eq(false));
        verify(shareCollabMIMock).setEnabled(eq(false));
     //   verify(shareSupportMIMock).setEnabled(eq(false));
    }



    @Test public void testOnSelectionChanged_OneSelected_appEnabled() {
        uut = new AnalysesToolBarImpl(appearanceMock, presenterMock, loaderMock){
            @Override
            boolean canCancelSelection(final List<Analysis> selection){
                return true;
            }

            @Override
            boolean canDeleteSelection(List<Analysis> selection) {
                return true;
            }
        };
        mockMenuItems(uut);
        final Analysis mockAnalysis = mock(Analysis.class);
        // Selected analysis' app is Enabled
        when(mockAnalysis.isAppDisabled()).thenReturn(false);
        when(mockAnalysis.getUserName()).thenReturn("user@iplantcollaborative.org");
        when(mockUserInfo.getFullUsername()).thenReturn("user@iplantcollaborative.org");
        when(mockSelectionEvent.getSelection()).thenReturn(Lists.newArrayList(mockAnalysis));
        when(mockAnalysis.getStatus()).thenReturn(COMPLETED.toString());
        uut.onSelectionChanged(mockSelectionEvent);

        verify(goToFolderMiMock).setEnabled(eq(true));
        verify(viewParamsMiMock).setEnabled(eq(true));
        verify(relaunchMock).setEnabled(eq(true));
        verify(cancelMiMock).setEnabled(eq(true));
        verify(deleteMiMock).setEnabled(eq(true));
        verify(renameMiMock).setEnabled(eq(true));
        verify(updateCommentsMiMock).setEnabled(eq(true));
    }

    @Test public void testOnSelectionChanged_OneSelected_appDisabled() {
        uut = new AnalysesToolBarImpl(appearanceMock, presenterMock, loaderMock){
            @Override
            boolean canCancelSelection(final List<Analysis> selection){
                return true;
            }

            @Override
            boolean canDeleteSelection(List<Analysis> selection) {
                return true;
            }
        };
        mockMenuItems(uut);
        final Analysis mockAnalysis = mock(Analysis.class);
        // Selected analysis' app is disabled
        when(mockAnalysis.isAppDisabled()).thenReturn(true);
        when(mockSelectionEvent.getSelection()).thenReturn(Lists.newArrayList(mockAnalysis));
        when(mockAnalysis.getUserName()).thenReturn("user@iplantcollaborative.org");
        when(mockUserInfo.getFullUsername()).thenReturn("user@iplantcollaborative.org");
        when(mockAnalysis.getStatus()).thenReturn(COMPLETED.toString());
        uut.onSelectionChanged(mockSelectionEvent);

        verify(goToFolderMiMock).setEnabled(eq(true));
        verify(viewParamsMiMock).setEnabled(eq(true));
        verify(relaunchMock).setEnabled(eq(false));
        verify(cancelMiMock).setEnabled(eq(true));
        verify(deleteMiMock).setEnabled(eq(true));
        verify(renameMiMock).setEnabled(eq(true));
        verify(updateCommentsMiMock).setEnabled(eq(true));
    }

    @Test public void testOnSelectionChanged_ManySelected() {
        uut = new AnalysesToolBarImpl(appearanceMock, presenterMock, loaderMock){
            @Override
            boolean canCancelSelection(final List<Analysis> selection){
                return true;
            }

            @Override
            boolean canDeleteSelection(List<Analysis> selection) {
                return true;
            }
        };
        mockMenuItems(uut);
        final Analysis mockAnalysis = mock(Analysis.class);
        final Analysis mockAnalysis2 = mock(Analysis.class);
        // Selected analysis' app is Enabled
        when(mockAnalysis.isAppDisabled()).thenReturn(false);
        when(mockSelectionEvent.getSelection()).thenReturn(Lists.newArrayList(mockAnalysis, mockAnalysis2));

        when(mockAnalysis.getUserName()).thenReturn("user@iplantcollaborative.org");
        when(mockUserInfo.getFullUsername()).thenReturn("user@iplantcollaborative.org");
        when(mockAnalysis.getStatus()).thenReturn(COMPLETED.toString());

        when(mockAnalysis2.getUserName()).thenReturn("user@iplantcollaborative.org");
        when(mockUserInfo.getFullUsername()).thenReturn("user@iplantcollaborative.org");
        when(mockAnalysis2.getStatus()).thenReturn(FAILED.toString());

        uut.onSelectionChanged(mockSelectionEvent);

        verify(goToFolderMiMock).setEnabled(eq(false));
        verify(viewParamsMiMock).setEnabled(eq(false));
        verify(relaunchMock).setEnabled(eq(false));
        verify(cancelMiMock).setEnabled(eq(true));
        verify(deleteMiMock).setEnabled(eq(true));
        verify(renameMiMock).setEnabled(eq(false));
        verify(updateCommentsMiMock).setEnabled(eq(false));
    }

    @Test public void testCanCancelSelection() {
        Analysis mock1 = mock(Analysis.class);

        when(mock1.getStatus()).thenReturn(SUBMITTED.toString());
        assertTrue("Selection should be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(RUNNING.toString());
        assertTrue("Selection should be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(IDLE.toString());
        assertTrue("Selection should be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(UNKNOWN.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(HELD.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(FAILED.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(SUBMISSION_ERR.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(REMOVED.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));
    }

    @Test public void testCanDeleteSelection() {
        Analysis mock1 = mock(Analysis.class);
        Analysis mock2 = mock(Analysis.class);
        Analysis mock3 = mock(Analysis.class);
        Analysis mock4 = mock(Analysis.class);

        when(mock1.getStatus()).thenReturn(SUBMITTED.toString());
        when(mock2.getStatus()).thenReturn(RUNNING.toString());
        assertFalse("Selection should not be deletable", uut.canDeleteSelection(Lists.newArrayList(mock1, mock2, mock3)));

        when(mock1.getStatus()).thenReturn(RUNNING.toString());
        when(mock2.getStatus()).thenReturn(COMPLETED.toString());
        assertFalse("Selection should not be deletable", uut.canDeleteSelection(Lists.newArrayList(mock3, mock1, mock2)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(RUNNING.toString());
        assertFalse("Selection should not be deletable", uut.canDeleteSelection(Lists.newArrayList(mock1, mock2, mock3)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(FAILED.toString());
        when(mock3.getStatus()).thenReturn(RUNNING.toString());
        assertFalse("Selection should not be deletable", uut.canDeleteSelection(Lists.newArrayList(mock1, mock2, mock3)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(FAILED.toString());
        when(mock3.getStatus()).thenReturn(COMPLETED.toString());
        when(mock4.getStatus()).thenReturn(CANCELED.toString());
        assertTrue("Selection should be deletable", uut.canDeleteSelection(Lists.newArrayList(mock1, mock2, mock3)));
    }

    @Test public void testCanShareSelection() {
        Analysis mock1 = mock(Analysis.class);
        Analysis mock2 = mock(Analysis.class);

        when(mock1.getStatus()).thenReturn(SUBMITTED.toString());
        when(mock2.getStatus()).thenReturn(RUNNING.toString());
        when(mock1.isSharable()).thenReturn(true);
        when(mock2.isSharable()).thenReturn(true);
        assertFalse("Selection should not be sharable", uut.isSharable(Lists.newArrayList(mock1, mock2)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(RUNNING.toString());
        when(mock1.isSharable()).thenReturn(true);
        when(mock2.isSharable()).thenReturn(true);
        assertFalse("Selection should not be sharable", uut.isSharable(Lists.newArrayList(mock1, mock2)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(FAILED.toString());
        when(mock1.isSharable()).thenReturn(false);
        when(mock2.isSharable()).thenReturn(false);
        assertFalse("Selection should not be sharable", uut.isSharable(Lists.newArrayList(mock1, mock2)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(FAILED.toString());
        when(mock1.isSharable()).thenReturn(false);
        when(mock2.isSharable()).thenReturn(true);
        assertFalse("Selection should not be sharable", uut.isSharable(Lists.newArrayList(mock1, mock2)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(FAILED.toString());
        when(mock1.isSharable()).thenReturn(true);
        when(mock2.isSharable()).thenReturn(true);
        assertTrue("Selection should  be sharable", uut.isSharable(Lists.newArrayList(mock1, mock2)));

    }
}
