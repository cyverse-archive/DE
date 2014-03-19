package org.iplantc.de.resources.client.uiapps.widgets;

import org.iplantc.de.resources.client.uiapps.widgets.help.AppContextHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.AppItemCategoryHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.CheckboxInputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.DoubleInputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.DoubleSelectionHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.EnvironmentVariableHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.FileInputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.FileOutputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.FolderInputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.FolderOutputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.InfoContextHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.IntegerInputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.IntegerSelectionHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.MultiFileInputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.MultiFileOutputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.TextInputHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.TextSelectionHelp;
import org.iplantc.de.resources.client.uiapps.widgets.help.TreeSelectionHelp;

import com.google.gwt.i18n.client.Messages;

public interface AppsWidgetsContextualHelpMessages extends Messages, AppContextHelp, MultiFileInputHelp, FileInputHelp, FolderInputHelp, TextSelectionHelp, IntegerSelectionHelp, DoubleSelectionHelp,
        TreeSelectionHelp, TextInputHelp, CheckboxInputHelp, EnvironmentVariableHelp, IntegerInputHelp, DoubleInputHelp, FileOutputHelp, FolderOutputHelp, MultiFileOutputHelp, AppItemCategoryHelp,
        InfoContextHelp {

    String excludeReference();

    String toolTip();

    String argumentOption();

    String argumentOrder();

    String doNotPass();

}