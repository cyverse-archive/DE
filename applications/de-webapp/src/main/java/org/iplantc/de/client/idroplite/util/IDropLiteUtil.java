package org.iplantc.de.client.idroplite.util;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;

import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;

/**
 * Utility class for building an iDrop Lite applet tag with the given JSON parameters for the applet.
 * 
 * @author psarando
 * 
 */
public class IDropLiteUtil {
    public static int DISPLAY_MODE_UPLOAD = 2;
    public static int DISPLAY_MODE_DOWNLOAD = 3;
    static final DEClientConstants constants = GWT.create(DEClientConstants.class);
    static final JsonUtil jsonUtil = JsonUtil.getInstance();

    public static HtmlLayoutContainer getAppletForUpload(JSONObject jsonAppletParams, int width,
            int height) {
        StringBuilder htmlAppletTag = buildAppletTagCommon(jsonAppletParams, width, height);

        htmlAppletTag.append(buildAppletParam("absPath", jsonUtil.getString(jsonAppletParams, "home")));
        htmlAppletTag.append(buildAppletParam("uploadDest",
                jsonUtil.getString(jsonAppletParams, "uploadDest")));
        htmlAppletTag.append(buildAppletParam("displayMode", String.valueOf(DISPLAY_MODE_UPLOAD)));

        if (GXT.isIE()) {
            htmlAppletTag.append("</object>");
        } else {
            htmlAppletTag.append(I18N.DISPLAY.javaError());
            htmlAppletTag.append("</APPLET>");
        }

        System.out.println("tag-->" + htmlAppletTag.toString());

        HtmlLayoutContainer htmlApplet = new HtmlLayoutContainer((htmlAppletTag.toString()));

        return htmlApplet;
    }

    public static HtmlLayoutContainer getAppletForDownload(JSONObject jsonAppletParams, int width,
            int height) {
        StringBuilder htmlAppletTag = buildAppletTagCommon(jsonAppletParams, width, height);
        htmlAppletTag.append(buildAppletParam("displayMode", String.valueOf(DISPLAY_MODE_DOWNLOAD)));
        htmlAppletTag.append(I18N.DISPLAY.javaError());
        if (GXT.isIE()) {
            htmlAppletTag.append("</object>");
        } else {
            htmlAppletTag.append(I18N.DISPLAY.javaError());
            htmlAppletTag.append("</APPLET>");
        }
        System.out.println("tag-->" + htmlAppletTag.toString());

        HtmlLayoutContainer htmlApplet = new HtmlLayoutContainer((htmlAppletTag.toString()));

        return htmlApplet;
    }

    private static StringBuilder buildAppletTagCommon(JSONObject jsonAppletParams, int width, int height) {

        StringBuilder htmlAppletTag = null;

        if (GXT.isIE()) {
            htmlAppletTag = buildIEAppletTagCommon(jsonAppletParams, width, height);
        } else {
            htmlAppletTag = new StringBuilder(Format.substitute(
                    "<APPLET code=\"{0}\" archive=\"{1}\" WIDTH=\"{2}\" HEIGHT=\"{3}\" >",
                    constants.iDropLiteMainClass(), constants.iDropLiteArchivePath(),
                    width, height));
        }

        htmlAppletTag.append(buildAppletParam("mode", "2"));
        htmlAppletTag.append(buildAppletParam("user", jsonUtil.getString(jsonAppletParams, "user")));
        htmlAppletTag.append(buildAppletParam("password",
                jsonUtil.getString(jsonAppletParams, "password")));
        htmlAppletTag.append(buildAppletParam("host", jsonUtil.getString(jsonAppletParams, "host")));
        htmlAppletTag.append(buildAppletParam("port",
                String.valueOf(jsonUtil.getNumber(jsonAppletParams, "port").intValue())));
        htmlAppletTag.append(buildAppletParam("zone", jsonUtil.getString(jsonAppletParams, "zone")));
        htmlAppletTag.append(buildAppletParam("defaultStorageResource",
                jsonUtil.getString(jsonAppletParams, "defaultStorageResource")));
        htmlAppletTag.append(buildAppletParam("key", jsonUtil.getString(jsonAppletParams, "key")));

        return htmlAppletTag;
    }

    private static StringBuilder buildIEAppletTagCommon(JSONObject jsonAppletParams, int width,
            int height) {
        StringBuilder htmlAppletTag = new StringBuilder(
                Format.substitute(
                        "<object classid=\"clsid:8AD9C840-044E-11D1-B3E9-00805F499D93\" width=\"{0}\" height=\"{1}\" "
                                + "name=\"idrop-lite\" "
                                + "codebase =\" http://java.sun.com/products/plugin/autodl/jinstall-1_5_0-windows-i586.cab#Version=1,5,0,0\">",
                        width, height));
        htmlAppletTag.append(buildAppletParam("code", constants.iDropLiteMainClass() + ".class"));
        htmlAppletTag.append(buildAppletParam("archive", constants.iDropLiteArchivePath()));
        htmlAppletTag.append(buildAppletParam("type", "application/x-java-applet;version=1.5.0"));
        htmlAppletTag.append(buildAppletParam("scriptable", "true"));

        return htmlAppletTag;

    }

    private static String buildAppletParam(String name, String value) {
        return Format.substitute("<param name=\"{0}\" value=\"{1}\"/>", name, value);
    }

}
