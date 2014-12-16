package org.iplantc.de.apps.client.views.cells;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOUT;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOVER;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * FIXME Create appearance
 * 
 * @author jstroot
 * 
 */
public class AppRatingCell extends AbstractCell<App> {

    private final class DeleteRatingCallBack implements AsyncCallback<String> {
        private App value;
        private final Element parent;

        private DeleteRatingCallBack(App value, Element parent) {
            this.value = value;
            this.parent = parent;
        }

        @Override
         public void
                 onSuccess(String result) {
             value.getRating()
                  .setUserRating(0);
             value.getRating()
                  .setCommentId(0);

             if (result == null
                     || result.isEmpty()) {
                 value.getRating()
                      .setAverageRating(0);
             } else {
                value = getUpdateRating(value, result);
             }

            resetRatingStarColors(parent, value);
         }

        @Override
         public void
                 onFailure(Throwable caught) {
             ErrorHandler.post(caught.getMessage(),
                               caught);
         }
    }

    interface MyCss extends CssResource {
        @ClassName("apps_icon")
        String appsIcon();

        @ClassName("disabled_unrate_button")
        String disabledUnrateButton();
    }

    interface Resources extends ClientBundle {
        @Source("AppRatingCell.css")
        MyCss css();
    }

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<img name=\"{0}\" title=\"{1}\" class=\"{2}\" src=\"{3}\"></img>")
                SafeHtml
 imgCell(String name, String toolTip, String className, SafeUri imgSrc);
    }

    public static enum RATING_CONSTANT {

        HATE_IT(I18N.DISPLAY.hateIt()),
        DID_NOT_LIKE_IT(I18N.DISPLAY.didNotLike()),
        LIKED_IT(I18N.DISPLAY.likedIt()),
        REALLY_LIKED_IT(I18N.DISPLAY.reallyLikedIt()),
        LOVED_IT(I18N.DISPLAY.lovedIt());

        private String displayText;

        private RATING_CONSTANT(String displaytext) {
            this.displayText = displaytext;
        }

        /**
         * Returns a string that identifies the RATING_CONSTANT.
         * 
         * @return
         */
        public String getTypeString() {
            return toString().toLowerCase();
        }

        /**
         * Null-safe and case insensitive variant of valueOf(String)
         * 
         * @param typeString name of an EXECUTION_STATUS constant
         * @return
         */
        public static RATING_CONSTANT fromTypeString(String typeString) {
            if (typeString == null || typeString.isEmpty()) {
                return null;
            }

            return valueOf(typeString.toUpperCase());
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    /**
     * Create a singleton instance of the templates used to render the cell.
     */
    private static Templates templates = GWT.create(Templates.class);
    private static final Resources resources = GWT.create(Resources.class);
    private final List<String> ratings;
    private final JsonUtil jsonUtil;

    public AppRatingCell() {
        super(CLICK, MOUSEOVER, MOUSEOUT);
        resources.css().ensureInjected();
        jsonUtil = JsonUtil.getInstance();

        ratings = new ArrayList<String>();
        ratings.add(0, RATING_CONSTANT.HATE_IT.displayText);
        ratings.add(1, RATING_CONSTANT.DID_NOT_LIKE_IT.displayText);
        ratings.add(2, RATING_CONSTANT.LIKED_IT.displayText);
        ratings.add(3, RATING_CONSTANT.REALLY_LIKED_IT.displayText);
        ratings.add(4, RATING_CONSTANT.LOVED_IT.displayText);

    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        int rating = (int)((value.getRating().getUserRating() != 0) ? value.getRating().getUserRating()
                                                                   : Math.floor(value.getRating()
                                                                                     .getAverageRating()));
        int total = value.getRating().getTotal();
        // Build five rating stars
        for (int i = 0; i < ratings.size(); i++) {
            if (i < rating) {
                if (value.getRating().getUserRating() != 0) {
                    sb.append(templates.imgCell("Rating-" + i,
                                                ratings.get(i),
                                                resources.css().appsIcon(),
                                                IplantResources.RESOURCES.goldStar().getSafeUri()));
                } else {
                    sb.append(templates.imgCell("Rating-" + i,
                                                ratings.get(i),
                                                resources.css().appsIcon(),
                                                IplantResources.RESOURCES.redStar().getSafeUri()));
                }
            } else {
                sb.append(templates.imgCell("Rating-" + i,
                                            ratings.get(i),
                                            resources.css().appsIcon(),
                                            IplantResources.RESOURCES.whiteStar().getSafeUri()));
            }
        }

        // Determine if user has rated the app, and if so, add the unrate icon/button
        if (value.getRating().getUserRating() > 0) {
            // Add unrate icon
            sb.append(templates.imgCell("Unrate",
                                        "Unrate",
                                        resources.css().appsIcon(),
                                        IplantResources.RESOURCES.deleteRating().getSafeUri()));
        } else {
            sb.append(templates.imgCell("Unrate",
                                        "Unrate",
                                        resources.css().disabledUnrateButton(),
                                        IplantResources.RESOURCES.deleteRating().getSafeUri()));
        }

        // append total ratings
        sb.appendHtmlConstant("<span name='total' title='No.of ratings'>(" + total + ")</span>");
    }

    @Override
    public boolean handlesSelection() {
        return true;
    }

    @Override
    public void onBrowserEvent(Cell.Context context,
                               Element parent,
                               App value,
                               NativeEvent event,
                               ValueUpdater<App> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (eventTarget.getNodeName().equalsIgnoreCase("img") && parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    onRatingClicked(parent, eventTarget, value);
                    break;
                case Event.ONMOUSEOVER:
                    onRatingMouseOver(parent, eventTarget);
                    break;
                case Event.ONMOUSEOUT:
                    resetRatingStarColors(parent, value);
                    break;
                default:
                    break;
            }
        }
    }

    private void resetRatingStarColors(Element parent, App value) {
        int rating = (int)((value.getRating().getUserRating() != 0) ? value.getRating().getUserRating()
                                                                   : Math.floor(value.getRating()
                                                                                     .getAverageRating()));
        int total = value.getRating().getTotal();

        for (int i = 0; i < parent.getChildCount(); i++) {
            Element child = Element.as(parent.getChild(i));
            // Reset rating stars
            if (child.getAttribute("name").startsWith("Rating")) {
                if (i < rating) {
                    if (value.getRating().getUserRating() != 0) {
                        child.setAttribute("src", IplantResources.RESOURCES.goldStar()
                                                                           .getSafeUri()
                                                                           .asString());
                    } else {
                        child.setAttribute("src", IplantResources.RESOURCES.redStar()
                                                                           .getSafeUri()
                                                                           .asString());
                    }
                } else {
                    child.setAttribute("src", IplantResources.RESOURCES.whiteStar()
                                                                       .getSafeUri()
                                                                       .asString());
                }
            } else if (child.getAttribute("name").equalsIgnoreCase("unrate")) {
                // Show/Hide unrate button
                if (value.getRating().getUserRating() != 0) {
                    child.getStyle().setDisplay(Display.BLOCK);
                    child.setAttribute("src", IplantResources.RESOURCES.deleteRating()
                                                                       .getSafeUri()
                                                                       .asString());
                } else {
                    // if there is no rating, hide the cell.
                    child.getStyle().setDisplay(Display.NONE);
                }
            } else if (child.getAttribute("name").equalsIgnoreCase("total")) {
                child.setInnerText("(" + total + ")");
            }
        }

    }

    private void onRatingMouseOver(Element parent, Element eventTarget) {
        boolean setWhiteStar = false;
        if (eventTarget.getAttribute("name").startsWith("Rating")) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                Element child = Element.as(parent.getChild(i));
                if (child.getAttribute("name").startsWith("Rating")) {
                    if (setWhiteStar) {
                        child.setAttribute("src", IplantResources.RESOURCES.whiteStar()
                                                                           .getSafeUri()
                                                                           .asString());
                    } else {
                        child.setAttribute("src", IplantResources.RESOURCES.goldStar()
                                                                           .getSafeUri()
                                                                           .asString());
                    }
                    if (child.getAttribute("name").equals(eventTarget.getAttribute("name"))) {
                        setWhiteStar = true;
                    }
                }
            }
        } else if (eventTarget.getAttribute("name").equalsIgnoreCase("unrate")) {
            eventTarget.setAttribute("src", IplantResources.RESOURCES.deleteRatingHover()
                                                                     .getSafeUri()
                                                                     .asString());
        }
    }

    private void onRatingClicked(final Element parent, Element eventTarget, final App value) {
        if (eventTarget.getAttribute("name").startsWith("Rating")) {
            String[] g = eventTarget.getAttribute("name").split("-");
            final int score = Integer.parseInt(g[1]) + 1;
            persistRating(value, score, parent);
        } else if (eventTarget.getAttribute("name").equalsIgnoreCase("unrate")) {
            // Hide unrate button
            eventTarget.getStyle().setDisplay(Display.NONE);
            onUnrate(parent, value);
        }

    }

    private void onUnrate(final Element parent, final App value) {
        Long commentId = null;
        try {
            AppFeedback feedback = value.getRating();
            if (feedback != null) {
                commentId = feedback.getCommentId();
            }
        } catch (NumberFormatException e) {
            // comment id empty or not a number, leave it null and proceed
        }

        ServicesInjector.INSTANCE.getAppUserServiceFacade().deleteRating(value.getId(),
                                                                         value.getWikiUrl(),
                                                                         commentId,
                                                                         new DeleteRatingCallBack(value,
                                                                                                 parent));

    }

    /** saves a rating to the database and the wiki page */
    private void persistRating(final App app, final int score, final Element parent) {

        AsyncCallback<String> callback = new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                app.getRating().setUserRating(score);
                resetRatingStarColors(parent, getUpdateRating(app, result));
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO post user friendly error message.
                ErrorHandler.post(caught);
            }
        };

        ServicesInjector.INSTANCE.getAppUserServiceFacade().rateApp(app.getWikiUrl(),
                                                                    app.getId(),
                                                                    score,
                                                                    app.getRating().getCommentId(),
                                                                    app.getIntegratorEmail(),
                                                                    callback);
    }

    private App getUpdateRating(final App app, String result) {
        JSONObject jsonObj = jsonUtil.getObject(result);
        if (jsonObj != null) {
            double newAverage = jsonUtil.getNumber(jsonObj, "average").doubleValue(); //$NON-NLS-1$
            app.getRating().setAverageRating(newAverage);
            int total = jsonUtil.getNumber(jsonObj, "total").intValue();
            app.getRating().setTotal(total);
        }

        return app;
    }

}
