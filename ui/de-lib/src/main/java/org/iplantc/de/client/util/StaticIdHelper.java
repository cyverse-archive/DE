package org.iplantc.de.client.util;

import com.google.common.base.Preconditions;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * @author aramsey
 */
public class StaticIdHelper {

    private static StaticIdHelper INSTANCE;

    StaticIdHelper() {

    }

    public static StaticIdHelper getInstance(){
        if(INSTANCE == null) {
            INSTANCE = new StaticIdHelper();
        }

        return INSTANCE;
    }

    /**
     * Sets the static IDs for the column headers in a grid
     * This does not work if called simply within a view's onEnsureDebugId method
     * as the headers will not have been rendered yet.
     * This can be called successfully using the grid's ViewReadyHandler (which can
     * then be added to the grid within the onEnsureDebugId method)
     * @param baseID
     * @param grid
     * @param cm
     */
    public void gridColumnHeaders(String baseID, Grid<?> grid, ColumnModel<?> cm) {
        Preconditions.checkNotNull(baseID);
        Preconditions.checkNotNull(grid);
        Preconditions.checkNotNull(cm);

        ColumnHeader<?> header = grid.getView().getHeader();

        String columnWrapSelectorClassName = header.getAppearance().columnsWrapSelector();
        String headRowClassName = "." + header.getAppearance().styles().headRow();

        XElement colWrapElement = (XElement)header.getElement().select(columnWrapSelectorClassName).getItem(0);
        Element table = colWrapElement.getElementsByTagName("table").getItem(0);
        //There are two tbody elements, the first holds only H/W information for the header cells
        //The second tbody has the column header information
        XElement tbody = (XElement)table.getElementsByTagName("tbody").getItem(1);
        XElement tr = (XElement)tbody.select(headRowClassName).getItem(0);
        NodeList<Element> tds = tr.getElementsByTagName("td");

        for (int i = 0; i < tds.getLength(); i++) {
            String headerName = cm.getColumn(i).getHeader().asString().replace(" ", "_");
            tds.getItem(i).setId(baseID + "." + headerName);
        }
    }

}
