package org.iplantc.de.diskResource.client.views;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.grid.LiveGridCheckBoxSelectionModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GxtMockitoTestRunner.class)
public class LiveGridCheckBoxSelectionModelTest {

    private LiveGridCheckBoxSelectionModel uut;

    @Before public void setUp() {

        uut = new LiveGridCheckBoxSelectionModel();

    }

    /**
     * Verifies that unit under test handles LiveGridViewUpdate events
     */
    @Test public void testBindGrid() {

    }

    /**
     *
     */
    @Test public void testIsSelected() {

    }

    /**
     *
     */
    @Test public void testOnUpdate() {

        // Verifies that selected items are marked as selected when they re-enter the view.
    }


    @Test public void testSelectAll() {

    }
}