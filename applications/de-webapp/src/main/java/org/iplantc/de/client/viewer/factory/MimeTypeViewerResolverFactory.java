/**
 * 
 */
package org.iplantc.de.client.viewer.factory;

import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.viewer.commands.HtmlDataViewCommand;
import org.iplantc.de.client.viewer.commands.ImageDataViewCommand;
import org.iplantc.de.client.viewer.commands.PdfDataViewCommand;
import org.iplantc.de.client.viewer.commands.TextDataViewCommand;
import org.iplantc.de.client.viewer.commands.ViewCommand;
import org.iplantc.de.client.viewer.commands.VizURLViewerCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sriram
 * 
 */
public class MimeTypeViewerResolverFactory {

    public static final Map<MimeType, String> modeMap = new HashMap<>();

    static Logger LOG = Logger.getLogger("viewer");

    static {
        modeMap.put(MimeType.X_SH, "shell");
        modeMap.put(MimeType.X_PYTHON, "python");
        modeMap.put(MimeType.X_PERL, "perl");
        modeMap.put(MimeType.X_RSRC, "r");
        modeMap.put(MimeType.X_WEB_MARKDOWN, "markdown");

    }

    public static ViewCommand getViewerCommand(MimeType type) {

        LOG.log(Level.SEVERE, "mime->" + type.toString());

        ViewCommand cmd = null;

        switch (type) {

            case PNG:
                cmd = new ImageDataViewCommand();
                break;

            case JPEG:
                cmd = new ImageDataViewCommand();
                break;

            case PDF:
                cmd = new PdfDataViewCommand();
                break;

            case PLAIN:
                cmd = new TextDataViewCommand(null);
                break;

            case HTML:
                cmd = new HtmlDataViewCommand();
                break;

            case XHTML_XML:
                cmd = new HtmlDataViewCommand();
                break;

            case X_SH:
                cmd = new TextDataViewCommand(modeMap.get(type));
                break;

            case GIF:
                cmd = new ImageDataViewCommand();
                break;

            case PREVIEW:
                cmd = new TextDataViewCommand(null);
                break;

            case VIZ:
                cmd = new VizURLViewerCommand();
                break;

            case X_PYTHON:
                cmd = new TextDataViewCommand(modeMap.get(type));
                LOG.log(Level.SEVERE, "mode-->" + modeMap.get(type));
                break;

            case X_RSRC:
                cmd = new TextDataViewCommand(modeMap.get(type));
                break;

            case X_PERL:
                cmd = new TextDataViewCommand(modeMap.get(type));
                break;

            case X_WEB_MARKDOWN:
                cmd = new TextDataViewCommand(modeMap.get(type));
                break;

            default:
                cmd = new TextDataViewCommand(null);

        }

        return cmd;

    }
}
