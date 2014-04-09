/**
 * 
 */
package org.iplantc.de.analysis.client.views.cells;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * @author sriram
 * 
 */
public abstract class AnalysisTimeStampCell extends AbstractCell<Analysis> {

    public String getFormattedDate(long date) {
        if (date == 0) {
            return "";
        }
        return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM).format(
                new Date(date));
    }

}
