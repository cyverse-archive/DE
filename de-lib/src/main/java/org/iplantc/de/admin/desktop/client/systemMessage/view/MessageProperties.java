package org.iplantc.de.admin.desktop.client.systemMessage.view;

import org.iplantc.de.client.models.sysMsgs.Message;

import com.google.gwt.editor.client.Editor.Path;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

/**
 * @author jstroot
 */
public interface MessageProperties extends PropertyAccess<Message> {

    ModelKeyProvider<Message> id();

    ValueProvider<Message, Date> activationTime();

    ValueProvider<Message, Date> deactivationTime();

    ValueProvider<Message, String> body();

    ValueProvider<Message, String> type();

    ValueProvider<Message, Boolean> dismissible();

    @Path("type")
    LabelProvider<Message> typeLabel();
}
