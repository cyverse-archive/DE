package org.iplantc.de.diskResource.client.search.views.cells;

import com.google.gwtmockito.GxtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * @author jstroot
 * 
 */
@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceSearchCellTest {

    private DiskResourceSearchCell unitUnderTest;

    @Mock DiskResourceSearchCell.DiskResourceSearchCellAppearance appearanceMock;
    @Mock DiskResourceQueryForm searchFormMock;

    @Before public void setUp() {
        unitUnderTest = new DiskResourceSearchCell(searchFormMock, appearanceMock);
    }

    @Test public void placeHolderTest() {

    }

}
