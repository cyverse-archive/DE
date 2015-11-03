package org.iplantc.de.theme.base.client.diskResource.grid.cells;

import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceFavoriteCell;
import org.iplantc.de.resources.client.FavoriteCellStyle;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.apps.cells.AppFavoriteCellDefaultAppearance;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class DiskResourceFavoriteCellDefaultAppearance implements DiskResourceFavoriteCell.Appearance {
    private final AppFavoriteCellDefaultAppearance.FavoriteTemplates favoriteTemplates;
    private final FavoriteCellStyle favoriteCellStyle;
    private final IplantDisplayStrings iplantDisplayStrings;

    public DiskResourceFavoriteCellDefaultAppearance() {
        this(GWT.<AppFavoriteCellDefaultAppearance.FavoriteTemplates> create(AppFavoriteCellDefaultAppearance.FavoriteTemplates.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class)
        );
    }

    DiskResourceFavoriteCellDefaultAppearance(final AppFavoriteCellDefaultAppearance.FavoriteTemplates favoriteTemplates,
                                              final IplantResources iplantResources,
                                              final IplantDisplayStrings iplantDisplayStrings) {
        this.favoriteTemplates = favoriteTemplates;
        this.favoriteCellStyle = iplantResources.favoriteCss();
        this.iplantDisplayStrings = iplantDisplayStrings;

        this.favoriteCellStyle.ensureInjected();
    }

    @Override
    public String addToFavoriteTooltip() {
        return iplantDisplayStrings.addAppToFav();
    }

    @Override
    public String favoriteClass() {
        return favoriteCellStyle.favorite();
    }

    @Override
    public String favoriteDisabledClass() {
        return favoriteCellStyle.favoriteDisabled();
    }

    @Override
    public String removeFromFavoriteTooltip() {
        return iplantDisplayStrings.remAppFromFav();
    }

    @Override
    public void render(final SafeHtmlBuilder sb,
                       final String imgName,
                       final String imgClassName,
                       final String imgToolTip,
                       final String baseID,
                       final String debugId) {

        if(DebugInfo.isDebugIdEnabled()
            && !Strings.isNullOrEmpty(baseID)){
            sb.append(favoriteTemplates.debugCell(imgName, imgClassName, imgToolTip, debugId));
        } else {
            sb.append(favoriteTemplates.cell(imgName, imgClassName, imgToolTip));
        }
    }
}
