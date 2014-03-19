package org.iplantc.de.client.models.apps.integration;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;

public interface SelectionItemGroup extends SelectionItem {

    List<SelectionItem> getArguments();

    void setArguments(List<SelectionItem> arguments);

    @PropertyName("isSingleSelect")
    boolean isSingleSelect();

    @PropertyName("isSingleSelect")
    void setSingleSelect(boolean singleSelect);

    List<SelectionItemGroup> getGroups();

    void setGroups(List<SelectionItemGroup> groups);

    Tree.CheckCascade getSelectionCascade();

    void setSelectionCascade(Tree.CheckCascade value);

}
