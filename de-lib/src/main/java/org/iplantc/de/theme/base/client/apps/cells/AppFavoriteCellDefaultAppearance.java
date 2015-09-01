package org.iplantc.de.theme.base.client.apps.cells;

import org.iplantc.de.apps.client.views.grid.cells.AppFavoriteCell;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class AppFavoriteCellDefaultAppearance implements AppFavoriteCell.AppFavoriteCellAppearance {
    public static interface FavoriteTemplates extends SafeHtmlTemplates {

        @Template("<span name='{0}' class='{1}' qtip='{2}'> </span>")
        SafeHtml cell(String imgName, String imgClassName, String imgToolTip);

        @Template("<span id='{3}' name='{0}' class='{1}' qtip='{2}'> </span>")
        SafeHtml debugCell(String imgName, String imgClassName, String imgToolTip, String debugId);
    }

    interface FavoriteCellStyle extends CssResource {
        String favorite();

        String favoriteDisabled();

        String favoriteAdd();
    }

    interface FavoriteCellResources extends ClientBundle {

        // KLUDGE Duplicate resource
        @Source("../fav_remove.png")
        ImageResource favIconDelete();

        // KLUDGE Duplicate resource
        @Source("../not_fav.png")
        ImageResource disabledFavIcon();

        // KLUDGE Duplicate resource
        @Source("../fav_add.png")
        ImageResource favIconAdd();

        // KLUDGE Duplicate resource
        @Source("../fav.png")
        ImageResource favIcon();

        @Source("FavoriteCellStyle.css")
        FavoriteCellStyle css();
    }


    private final FavoriteTemplates templates;
    private final FavoriteCellResources resources;
    private final IplantDisplayStrings iplantDisplayStrings;

    public AppFavoriteCellDefaultAppearance() {
        this(GWT.<FavoriteTemplates> create(FavoriteTemplates.class),
             GWT.<FavoriteCellResources> create(FavoriteCellResources.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    AppFavoriteCellDefaultAppearance(final FavoriteTemplates templates,
                                     final FavoriteCellResources resources,
                                     final IplantDisplayStrings iplantDisplayStrings) {
        this.templates = templates;
        this.resources = resources;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.resources.css().ensureInjected();
    }

    @Override
    public String addAppToFav() {
        return iplantDisplayStrings.addAppToFav();
    }

    @Override
    public String appUnavailable() {
        return iplantDisplayStrings.appUnavailable();
    }

    @Override
    public String favoriteClass() {
        return resources.css().favorite();
    }

    @Override
    public String favoriteDisabledClass() {
        return resources.css().favoriteDisabled();
    }

    @Override
    public String favoriteAddClass() {
        return resources.css().favoriteAdd();
    }

    @Override
    public String remAppFromFav() {
        return iplantDisplayStrings.remAppFromFav();
    }

    @Override
    public void render(final SafeHtmlBuilder sb,
                       final String imgName,
                       final String imgClassName,
                       final String imgToolTip,
                       final String debugId) {
        if(DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(debugId)){
            sb.append(templates.debugCell(imgName, imgClassName, imgToolTip, debugId));
        } else {
            sb.append(templates.cell(imgName, imgClassName, imgToolTip));
        }
    }
}
