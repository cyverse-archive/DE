package org.iplantc.de.client.models.apps.integration;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;

public interface SelectionItemGroup extends SelectionItem {

    String ARGUMENTS_KEY = "arguments";
    String GROUPS_KEY = "groups";
    String SINGLE_SELECT_KEY = "isSingleSelect";

    @PropertyName(ARGUMENTS_KEY)
    List<SelectionItem> getArguments();

    @PropertyName(ARGUMENTS_KEY)
    void setArguments(List<SelectionItem> arguments);

    @PropertyName(SINGLE_SELECT_KEY)
    boolean isSingleSelect();

    @PropertyName(SINGLE_SELECT_KEY)
    void setSingleSelect(boolean singleSelect);

    @PropertyName(GROUPS_KEY)
    List<SelectionItemGroup> getGroups();

    @PropertyName(GROUPS_KEY)
    void setGroups(List<SelectionItemGroup> groups);

    Tree.CheckCascade getSelectionCascade();

    void setSelectionCascade(Tree.CheckCascade value);

}
