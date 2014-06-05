package org.iplantc.de.diskResource.client.views.cells;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.client.models.comments.CommentsAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.comments.presenter.CommentsPresenter;
import org.iplantc.de.commons.client.comments.view.CommentsView;
import org.iplantc.de.commons.client.comments.view.CommentsViewImpl;
import org.iplantc.de.diskResource.client.views.cells.events.ManageMetadataEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ManageSharingEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ShareByDataLinkEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.Dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FIXME This cell needs an appearance
 */
public class DiskResourceActionsCell extends AbstractCell<DiskResource> {

    private String baseID;

    public void setBaseDebugId(String baseID) {
        this.baseID = baseID;
    }

    interface MyCss extends CssResource {
        @ClassName("actions_icon")
        String actionIcon();
    }
    interface Resource extends ClientBundle {

        @Source("DiskResourceActionsCell.css")
        MyCss css();
    }
    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<img name='{0}' title='{1}' class='{2}' src='{3}'></img>")
        SafeHtml imgCell(String name, String toolTip, String className, SafeUri imgSrc);

        @SafeHtmlTemplates.Template("<img id='{4}' name='{0}' title='{1}' class='{2}' src='{3}'></img>")
        SafeHtml debugImgCell(String name, String toolTip, String className, SafeUri imgSrc, String id);
    }

    private final IplantDisplayStrings displayStrings;
    private final IplantResources iplantResources;

    private static Templates templates = GWT.create(Templates.class);
    private static final Resource resources = GWT.create(Resource.class);
    private HasHandlers hasHandlers;

    private final String SHARE_FOLDER_BY_LINK_ACTION;
    private final String SHARE_FILE_BY_LINK_ACTION;
    private final String SHARE_BY_DE_ACTION;
    private final String MANAGE_METADATA_ACTION;
    private final String FAVORITE_ACTION;
    private final String COMMENTS_ACTION;


    public DiskResourceActionsCell() {
        super(CLICK);

        resources.css().ensureInjected();
        displayStrings = I18N.DISPLAY;
        iplantResources = IplantResources.RESOURCES;

        SHARE_FOLDER_BY_LINK_ACTION = displayStrings.share() + " " + displayStrings.path();
        SHARE_FILE_BY_LINK_ACTION = displayStrings.share() + " " + displayStrings.viaPublicLink();
        SHARE_BY_DE_ACTION = displayStrings.share() + " " + displayStrings.viaDiscoveryEnvironment();
        MANAGE_METADATA_ACTION = displayStrings.metadata();
        FAVORITE_ACTION = "Favorite";
        COMMENTS_ACTION = displayStrings.comments();

    }

    @Override
    public void render(Context context, DiskResource value, SafeHtmlBuilder sb) {
        if(value == null){
            return;
        }
        if (value.isFilter() || DiskResourceUtil.inTrash(value)) {
            return;
        }

        String name = null;
        String toolTip = null;
        String className = null;
        SafeUri imgSrc = null;

        String debugId = null;
        if (value instanceof Folder) {
            name = SHARE_FOLDER_BY_LINK_ACTION;
            toolTip = SHARE_FOLDER_BY_LINK_ACTION;
            className = resources.css().actionIcon();
            imgSrc = iplantResources.dataLink().getSafeUri();
            debugId = baseID + "." + value.getId() + DiskResourceModule.Ids.ACTION_CELL_DATA_LINK;
        }
        if ((value instanceof File) && DiskResourceUtil.isOwner(value)) {
            name = SHARE_FILE_BY_LINK_ACTION;
            toolTip = SHARE_FILE_BY_LINK_ACTION;
            className = resources.css().actionIcon();
            imgSrc = iplantResources.linkAdd().getSafeUri();
            debugId = baseID + "." + value.getId() + DiskResourceModule.Ids.ACTION_CELL_DATA_LINK_ADD;
        }


        // Append link action
        if(name != null && className != null && imgSrc != null){
            if(DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID) && (debugId != null)){
                sb.append(templates.debugImgCell(name, toolTip, className, imgSrc, debugId));
            } else {
                sb.append(templates.imgCell(name, toolTip, className, imgSrc));
            }
        }

        debugId = null;
        if (DiskResourceUtil.isOwner(value)) {
            name = SHARE_BY_DE_ACTION;
            toolTip = SHARE_BY_DE_ACTION;
            className = resources.css().actionIcon();
            imgSrc = iplantResources.share().getSafeUri();
            debugId = baseID + "." + value.getId() + DiskResourceModule.Ids.ACTION_CELL_SHARE;
        } else {
            name = null;
            toolTip = null;
            className = null;
            imgSrc = null;
        }

        // Append Share action
        if((name != null) && (className != null) && (imgSrc != null)){
            if(DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID) && (debugId != null)) {
                sb.append(templates.debugImgCell(name, toolTip, className, imgSrc, debugId));
            } else {
                sb.append(templates.imgCell(name, toolTip, className, imgSrc));
            }
        }

        name = MANAGE_METADATA_ACTION;
        toolTip = MANAGE_METADATA_ACTION;
        className = resources.css().actionIcon();
        imgSrc = iplantResources.metadata().getSafeUri();
        debugId = baseID + "." + value.getId() + DiskResourceModule.Ids.ACTION_CELL_METADATA;

        // Append metadata action
        if(name != null && className != null && imgSrc != null){
            if(DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID) && (debugId != null)) {
                sb.append(templates.debugImgCell(name, toolTip, className, imgSrc, debugId));
            } else {
                sb.append(templates.imgCell(name, toolTip, className, imgSrc));
            }
        }

        name = FAVORITE_ACTION;
        toolTip = FAVORITE_ACTION;
        className = resources.css().actionIcon();
        imgSrc = iplantResources.favIcon().getSafeUri();
        debugId = baseID + "." + value.getId() + DiskResourceModule.Ids.ACTION_CELL_FAVORITE;

        // append favorite action
        if (name != null && className != null && imgSrc != null) {
            if (DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID) && (debugId != null)) {
                sb.append(templates.debugImgCell(name, toolTip, className, imgSrc, debugId));
            } else {
                sb.append(templates.imgCell(name, toolTip, className, imgSrc));
            }
        }

        name = COMMENTS_ACTION;
        toolTip = COMMENTS_ACTION;
        className = resources.css().actionIcon();
        imgSrc = iplantResources.userComment().getSafeUri();
        debugId = baseID + "." + value.getId() + DiskResourceModule.Ids.ACTION_CELL_COMMENTS;

        // append comments action
        if (name != null && className != null && imgSrc != null) {
            if (DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID) && (debugId != null)) {
                sb.append(templates.debugImgCell(name, toolTip, className, imgSrc, debugId));
            } else {
                sb.append(templates.imgCell(name, toolTip, className, imgSrc));
            }
        }

    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, DiskResource value, NativeEvent event, ValueUpdater<DiskResource> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (eventTarget.getNodeName().equalsIgnoreCase("img")
                    && parent.isOrHasChild(eventTarget)) {
            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value);
                    break;
                default:
                    break;
            }
        }
    }

    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

    private void doOnClick(Element eventTarget, DiskResource value) {
        if(hasHandlers == null){
            return;
        }

        String action = eventTarget.getAttribute("name");
        if (action.equals(SHARE_FOLDER_BY_LINK_ACTION) || action.equals(SHARE_FILE_BY_LINK_ACTION)) {
            hasHandlers.fireEvent(new ShareByDataLinkEvent(value));
        } else if (action.equalsIgnoreCase(SHARE_BY_DE_ACTION)) {
            hasHandlers.fireEvent(new ManageSharingEvent(value));
        } else if (action.equalsIgnoreCase(MANAGE_METADATA_ACTION)) {
            hasHandlers.fireEvent(new ManageMetadataEvent(value));
        } else if (action.equalsIgnoreCase(COMMENTS_ACTION)) {
            Dialog d = new Dialog();
            d.setHeadingText(I18N.DISPLAY.comments());
            d.remove(d.getButtonBar());
            d.setSize("500px", "400px");
            CommentsView cv = new CommentsViewImpl();
            CommentsPresenter cp = new CommentsPresenter(cv);
            cv.setPresenter(cp);
            cp.loadComments(loadTestComments());
            cp.go(d);
            d.show();

        }

    }

    private List<Comment> loadTestComments() {
        List<Comment> commentsList = new ArrayList<Comment>();
       CommentsAutoBeanFactory factory = GWT.create(CommentsAutoBeanFactory.class);
       UserInfo userInfo = UserInfo.getInstance();

       for (int i = 0;i <10;i++) {
           Comment c = AutoBeanCodex.decode(factory, Comment.class, "{}").as();
            long time = new Date().getTime();
            c.setId(time + "" + i);
           c.setCommentText("foo bar foo barfoo barfoo barfoo barfoo barfoo barfoo barfoo barfoo barfoo barfoo barfoo barfoo bar"
                   + "foo barfoo barfoo barfoo barfoo barfoo barfoo barfoo barfoo bar"
                   + "foo barfoo barfoo barfoo barfoo barfoo bar"
                   + "foo barfoo barfoo barfoo bar");
           c.setCommentedBy(userInfo.getUsername());
            c.setTimestamp(time);
            commentsList.add(c);

       }

        return commentsList;
    }

}
