package org.iplantc.de.systemMessages.client.presenter;

import org.iplantc.de.shared.services.ProvidesTime;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import java.io.IOException;
import java.util.Date;

/**
 * This class renders an activation time.
 */
final class ActivationTimeRenderer implements Renderer<Date> {

    private static final ProvidesTime CLOCK = GWT.create(ProvidesTime.class);

    private static boolean withinPreviousWeek(final Date successor, final Date predecessor) {
        if (predecessor.after(successor)) {
            return false;
        }
        return CalendarUtil.getDaysBetween(predecessor, successor) < 7;
    }


    /**
     * @see Renderer<T>#render(T)
     */
    @Override
    public String render(final Date activationTime) {
        final Date now = CLOCK.now();
        String actMsg = "";
        if (CalendarUtil.isSameDate(now, activationTime)) {
            actMsg = org.iplantc.de.resources.client.messages.I18N.DISPLAY.today();
        } else if (withinPreviousWeek(now, activationTime)) {
            actMsg = DateTimeFormat.getFormat("cccc").format(activationTime);
        } else {
            actMsg = DateTimeFormat.getFormat("dd MMMM yyyy").format(activationTime);
        }
        return actMsg;
    }

    /**
     * @see Renderer<T>#render(T, Appendable)
     */
    @Override
    public void render(final Date activationTime, final Appendable appendable) throws IOException {
        appendable.append(render(activationTime));
    }

}
