package org.iplantc.de.diskResource.client.presenters.metadata;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.MetadataTemplateViewDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GxtMockitoTestRunner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sriram on 6/13/16.
 */
@RunWith(GxtMockitoTestRunner.class)
public class MetadataPresenterImplTest {

    @Mock  DiskResource resource;
    @Mock  MetadataView view;
    @Mock  DiskResourceServiceFacade drService;
    @Mock  List<MetadataTemplateInfo> templates;
    @Mock  MetadataTemplateViewDialog templateView;
    @Mock  List<DiskResourceMetadata> userMdList;
    @Mock  DiskResourceAutoBeanFactory autoBeanFactory;
    @Mock MetadataView.Presenter.Appearance appearance;

    private MetadataPresenterImpl presenter;
    @Before
    public void setUp() {
       presenter = new MetadataPresenterImpl(resource,view,drService);
    }

    @Test
    public void testIsDirtyOnEmpty(){
        List<DiskResourceMetadata> userMetadata = new ArrayList<>();
        when(view.getUserMetadata()).thenReturn(userMetadata);
        when(userMdList.size()).thenReturn(0);
        when(view.isDirty()).thenReturn(false);
        Assert.assertEquals(false,presenter.isDirty());
    }

    @Test
    public void testIsDirtyNonEmpty() {
        List<DiskResourceMetadata> userMetadata = new ArrayList<>();
        DiskResourceMetadata md1 = mock(DiskResourceMetadata.class);
        DiskResourceMetadata md2 = mock(DiskResourceMetadata.class);
        userMetadata.add(md1);
        userMetadata.add(md2);
        when(view.getUserMetadata()).thenReturn(userMetadata);
        when(userMdList.size()).thenReturn(0);
        when(view.isDirty()).thenReturn(false);
        Assert.assertEquals(false,presenter.isDirty());
  }

    @Test
    public void testIsDirtyEdited() {
        List<DiskResourceMetadata> userMetadata = new ArrayList<>();
        DiskResourceMetadata md1 = mock(DiskResourceMetadata.class);
        DiskResourceMetadata md2 = mock(DiskResourceMetadata.class);
        userMetadata.add(md1);
        userMetadata.add(md2);
        when(view.getUserMetadata()).thenReturn(userMetadata);
        when(userMdList.size()).thenReturn(1);
        when(view.isDirty()).thenReturn(true);
        Assert.assertEquals(true,presenter.isDirty());
    }

    @Test
    public void testOnImportNull() {
        view.addToUserMetadata(null);
        Assert.assertEquals(view.getUserMetadata().size(), 0);
    }

    @Test
    public void testOnImportFromIrodsAvu() {
        List<DiskResourceMetadata> irodsAvu = new ArrayList<>();
        DiskResourceMetadata md1 = mock(DiskResourceMetadata.class);
        DiskResourceMetadata md2 = mock(DiskResourceMetadata.class);
        irodsAvu.add(md1);
        irodsAvu.add(md2);
        view.addToUserMetadata(irodsAvu);
        when(view.getUserMetadata()).thenReturn(irodsAvu);
        Assert.assertEquals(view.getUserMetadata().size(),2);
    }
}
