package org.iplantc.de.resources.client.uiapps.widgets;

import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.ArgumentGroupLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.CheckboxInputLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.DoubleInputLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.DoubleSelectionLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.EnvironmentVariableLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.FileInputTypeLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.FileOutputLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.FolderInputTypeLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.FolderOutputLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.InfoTypeLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.IntegerInputLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.IntegerSelectionLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.ListCreationColumnHeaders;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.MultiFileInputLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.MultiFileOutputLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.MultiLineTextLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.ReferenceSelectorLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.TextInputLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.TextSelectionLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.TreeSelectionLabels;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface AppsWidgetsPropertyPanelLabels extends Messages, ArgumentGroupLabels, InfoTypeLabels, FileInputTypeLabels, FolderInputTypeLabels, MultiFileInputLabels, TextSelectionLabels,
        IntegerSelectionLabels, DoubleSelectionLabels, TreeSelectionLabels, TextInputLabels, MultiLineTextLabels, CheckboxInputLabels, EnvironmentVariableLabels, IntegerInputLabels,
        DoubleInputLabels, FileOutputLabels, FolderOutputLabels, MultiFileOutputLabels, ListCreationColumnHeaders, ReferenceSelectorLabels {

    String appDefaultName();

    String appDescriptionEmptyText();

    String appDescriptionLabel();

    String appNameEmptyText();

    String appNameLabel();

    String argumentOption();

    String argumentOptionEmptyText();

    String detailsPanelHeader(String name);

    String detailsPanelDefaultText();

    String doNotPass();

    String excludeWhenEmpty();

    SafeHtml isRequired();

    SafeHtml isVisible();

    SafeHtml doNotDisplay();

    String toolTipEmptyText();

    String toolTipText();

    String toolUsedEmptyText();

    String toolUsedLabel();

    String validatorRulesLabel();

}