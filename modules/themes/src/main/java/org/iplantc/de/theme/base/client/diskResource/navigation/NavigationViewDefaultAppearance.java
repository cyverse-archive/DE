package org.iplantc.de.theme.base.client.diskResource.navigation;

import org.iplantc.de.client.models.diskResources.DiskResourceFavorite;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceErrorMessages;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;
import com.sencha.gxt.widget.core.client.tree.TreeView;

/**
 * @author jstroot
 */
public class NavigationViewDefaultAppearance implements NavigationView.Appearance {
    public interface NavigationViewResources extends ClientBundle {
        @Source("DataCollapse.css")
        DataCollapseStyle dataCollapseStyle();

        @Source("folder_view.gif")
        ImageResource magicFolder();

        @Source("folder_star.png")
        ImageResource favoritesFolder();

        @Source("tree_collapse.png")
        ImageResource treeCollapse();

        @Source("tree_collapse_hover.png")
        ImageResource treeCollapseHover();
    }

    interface Templates extends SafeHtmlTemplates {
        @Template("<span style='color:red;font-style:italic;'>{0}</span>")
        SafeHtml filterText(String name);
    }

    final class CustomTreeStyle extends TreeStyle {

        private final Tree.TreeAppearance appearance;

        public CustomTreeStyle(final Tree.TreeAppearance appearance) {
            this.appearance = appearance;
        }

        @Override
        public ImageResource getLeafIcon() {
            return appearance.closeNodeIcon();
        }

    }

    private final class CustomTreeView extends TreeView<Folder> {
        @Override
        public void onTextChange(Tree.TreeNode<Folder> node, SafeHtml text) {
            Element textEl = getTextElement(node);
            if (textEl != null) {
                Folder folder = node.getModel();
                if (!folder.isFilter()) {
                    textEl.setInnerHTML(Util.isEmptyString(text.asString()) ? "&#160;" : text.asString());
                } else {
                    textEl.setInnerHTML(Util.isEmptyString(text.asString()) ? "&#160;" : "<span style='color:red;font-style:italic;'>" + text.asString() + "</span>");
                }
            }
        }
    }

    private final IplantDisplayStrings iplantDisplayStrings;
    private final DiskResourceErrorMessages errorMessages;
    private final NavigationDisplayStrings displayStrings;
    private final NavigationViewResources navigationViewResources;
    private final DiskResourceMessages diskResourceMessages;
    private final Templates templates;

    public NavigationViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<NavigationDisplayStrings> create(NavigationDisplayStrings.class),
             GWT.<DiskResourceErrorMessages> create(DiskResourceErrorMessages.class),
             GWT.<NavigationViewResources> create(NavigationViewResources.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<Templates> create(Templates.class));
    }

    NavigationViewDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                    final NavigationDisplayStrings displayStrings,
                                    final DiskResourceErrorMessages errorMessages,
                                    final NavigationViewResources navigationViewResources,
                                    final DiskResourceMessages diskResourceMessages,
                                    final Templates templates) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
        this.errorMessages = errorMessages;
        this.navigationViewResources = navigationViewResources;
        this.diskResourceMessages = diskResourceMessages;
        this.templates = templates;
        this.navigationViewResources.dataCollapseStyle().ensureInjected();
    }

    @Override
    public String dataDragDropStatusText(int size) {
        return diskResourceMessages.dataDragDropStatusText(size).asString();
    }

    @Override
    public IconProvider<Folder> getIconProvider() {
        return new IconProvider<Folder>() {
            @Override
            public ImageResource getIcon(Folder model) {
                if(model instanceof DiskResourceQueryTemplate){
                    return navigationViewResources.magicFolder();
                } else if (model instanceof DiskResourceFavorite){
                    return navigationViewResources.favoritesFolder();
                }
                return null;
            }
        };
    }

    @Override
    public TreeStyle getTreeStyle(Tree.TreeAppearance appearance) {
        return new CustomTreeStyle(appearance);
    }

    @Override
    public TreeView<Folder> getTreeView() {
        return new CustomTreeView();
    }

    @Override
    public String permissionErrorMessage() {
        return errorMessages.permissionErrorMessage();
    }

    @Override
    public String treeCollapseHoverStyle() {
        return navigationViewResources.dataCollapseStyle().collapseHover();
    }

    @Override
    public String treeCollapseStyle() {
        return navigationViewResources.dataCollapseStyle().collapse();
    }

    @Override
    public String treeCollapseToolTip() {
        return diskResourceMessages.collapseAll();
    }

    @Override
    public String headingText() {
        return displayStrings.headingText();
    }

    @Override
    public SafeHtml treeNodeFilterText(String name) {
        return templates.filterText(name);
    }
}
