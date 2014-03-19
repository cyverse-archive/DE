/**
 * 
 */
package org.iplantc.de.client.viewer.factory;

import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.viewer.commands.HtmlDataViewCommand;
import org.iplantc.de.client.viewer.commands.ImageDataViewCommand;
import org.iplantc.de.client.viewer.commands.PdfDataViewCommand;
import org.iplantc.de.client.viewer.commands.ShellScriptViewCommand;
import org.iplantc.de.client.viewer.commands.TextDataViewCommand;
import org.iplantc.de.client.viewer.commands.ViewCommand;
import org.iplantc.de.client.viewer.commands.VizURLViewerCommand;

/**
 * @author sriram
 * 
 */
public class MimeTypeViewerResolverFactory {

    public static ViewCommand getViewerCommand(MimeType type) {

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
                cmd = new TextDataViewCommand();
                break;

            case HTML:
                cmd = new HtmlDataViewCommand();
                break;

            case XHTML_XML:
                cmd = new HtmlDataViewCommand();
                break;

            case X_SH:
                cmd = new ShellScriptViewCommand();
                break;

            case GIF:
                cmd = new ImageDataViewCommand();
                break;

            case PREVIEW:
                cmd = new TextDataViewCommand();
                break;

            case VIZ:
                cmd = new VizURLViewerCommand();
                break;
            default:
                cmd = new TextDataViewCommand();

        }

        return cmd;

    }
}
