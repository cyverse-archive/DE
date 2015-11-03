package org.iplantc.de.theme.base.client.apps.cells;

import org.iplantc.de.apps.client.views.grid.cells.AppRatingCell;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;

import java.util.List;

/**
 * @author jstroot
 */
public class AppRatingCellDefaultAppearance implements AppRatingCell.AppRatingCellAppearance {

    interface MyCss extends CssResource {
        @ClassName("apps_icon")
        String appsIcon();

        @ClassName("disabled_unrate_button")
        String disabledUnrateButton();
    }

    interface Resources extends ClientBundle {
        @Source("AppRatingCell.css")
        MyCss css();

        @Source("../delete_rating.png")
        ImageResource deleteRating();

        @Source("../delete_rating_hover.png")
        ImageResource deleteRatingHover();

        @Source("../star-gold.gif")
        ImageResource goldStar();

        @Source("../star-red.gif")
        ImageResource redStar();

        @Source("../star-white.gif")
        ImageResource whiteStar();
    }

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {
        @Template("<img name='{0}' title='{1}' class='{2}' src='{3}'></img>")
        SafeHtml imgCell(String name, String toolTip, String className, SafeUri imgSrc);
    }

    private final Templates templates;
    private final Resources resources;
    private final List<String> ratings;
    private final AppsMessages appMsgs;

    public AppRatingCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<Resources> create(Resources.class),
             GWT.<AppsMessages> create(AppsMessages.class));
    }

    AppRatingCellDefaultAppearance(final Templates templates,
                                   final Resources resources,
                                   final AppsMessages appsMessages) {
        this.templates = templates;
        this.resources = resources;
        this.resources.css().ensureInjected();
        this.appMsgs = appsMessages;
        ratings = Lists.newArrayList();
        ratings.add(0, appsMessages.hateIt());
        ratings.add(1, appsMessages.didNotLike());
        ratings.add(2, appsMessages.likedIt());
        ratings.add(3, appsMessages.reallyLikedIt());
        ratings.add(4, appsMessages.lovedIt());
    }

    @Override
    public int getRatingScore(Element eventTarget) {
        String[] g = eventTarget.getAttribute("name").split("-");
        return Integer.parseInt(g[1]) + 1;
    }

    @Override
    public boolean isRatingCell(Element eventTarget) {
        return eventTarget.getAttribute("name").startsWith("Rating");
    }

    @Override
    public boolean isUnRateCell(Element eventTarget) {
        return eventTarget.getAttribute("name").equalsIgnoreCase("unrate");
    }

    @Override
    public void onMouseOut(Element parent, App value) {
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
                        child.setAttribute("src", resources.goldStar().getSafeUri().asString());
                    } else {
                        child.setAttribute("src", resources.redStar().getSafeUri().asString());
                    }
                } else {
                    child.setAttribute("src", resources.whiteStar().getSafeUri().asString());
                }
            } else if (child.getAttribute("name").equalsIgnoreCase("unrate")) {
                // Show/Hide unrate button
                if (value.getRating().getUserRating() != 0) {
                    child.getStyle().setDisplay(Style.Display.BLOCK);
                    child.setAttribute("src", resources.deleteRating().getSafeUri().asString());
                } else {
                    // if there is no rating, hide the cell.
                    child.getStyle().setDisplay(Style.Display.NONE);
                }
            } else if (child.getAttribute("name").equalsIgnoreCase("total")) {
                child.setInnerText("(" + total + ")");
            }
        }
    }

    @Override
    public void onMouseOver(Element parent, Element eventTarget, App app) {

        if (app.getAppType().equalsIgnoreCase(App.EXTERNAL_APP)) {
            eventTarget.setTitle(appMsgs.featureNotSupported());
            return;
        }

        boolean setWhiteStar = false;
        if (eventTarget.getAttribute("name").startsWith("Rating")) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                Element child = Element.as(parent.getChild(i));
                if (child.getAttribute("name").startsWith("Rating")) {
                    if (setWhiteStar) {
                        child.setAttribute("src", resources.whiteStar().getSafeUri().asString());
                    } else {
                        child.setAttribute("src", resources.goldStar().getSafeUri().asString());
                    }
                    if (child.getAttribute("name").equals(eventTarget.getAttribute("name"))) {
                        setWhiteStar = true;
                    }
                }
            }
        } else if (eventTarget.getAttribute("name").equalsIgnoreCase("unrate")) {
            eventTarget.setAttribute("src", resources.deleteRatingHover().getSafeUri().asString());
        }
    }

    @Override
    public void onUnRate(Element eventTarget) {
        // Hide unrate button
        eventTarget.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void render(SafeHtmlBuilder sb, App app) {
        if (app == null) {
            return;
        }
        int rating = (int)((app.getRating().getUserRating() != 0) ? app.getRating().getUserRating()
                                                                 : Math.floor(app.getRating()
                                                                                 .getAverageRating()));
        int total = app.getRating().getTotal();
        // Build five rating stars
        for (int i = 0; i < ratings.size(); i++) {
            if (i < rating) {
                if (app.getRating().getUserRating() != 0) {
                    sb.append(templates.imgCell("Rating-" + i,
                                                ratings.get(i),
                                                resources.css().appsIcon(),
                                                resources.goldStar().getSafeUri()));
                } else {
                    sb.append(templates.imgCell("Rating-" + i,
                                                ratings.get(i),
                                                resources.css().appsIcon(),
                                                resources.redStar().getSafeUri()));
                }
            } else {
                sb.append(templates.imgCell("Rating-" + i,
                                            ratings.get(i),
                                            resources.css().appsIcon(),
                                            resources.whiteStar().getSafeUri()));
            }
        }

        // Determine if user has rated the app, and if so, add the unrate icon/button
        if (app.getRating().getUserRating() > 0) {
            // Add unrate icon
            sb.append(templates.imgCell("Unrate",
                                        "Unrate",
                                        resources.css().appsIcon(),
                                        resources.deleteRating().getSafeUri()));
        } else {
            sb.append(templates.imgCell("Unrate",
                                        "Unrate",
                                        resources.css().disabledUnrateButton(),
                                        resources.deleteRating().getSafeUri()));
        }

        // append total ratings
        sb.appendHtmlConstant("<span name='total' title='No.of ratings'>(" + total + ")</span>");
    }
}
