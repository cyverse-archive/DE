package org.iplantc.de.server;

import org.iplantc.de.shared.services.AboutApplicationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletException;

/**
 * Communicates application information to include as "about" data regarding the current build and client UserAgent.
 *
 * This servlet will include information about services once the modeling of software components has been completed.
 *
 * @see org.iplantc.de.client.services.AboutApplicationService
 * @author lenards
 */
@SuppressWarnings("nls")
public class AboutApplicationServlet extends RemoteServiceServlet implements AboutApplicationService {

    private static final long serialVersionUID          = 6046105023536377635L;
    private static final String DEFAULT_RELEASE_VERSION = "unversioned";
    private static final String MANIFEST_LOC            = "/META-INF/MANIFEST.MF";
    private static final String BUILD_NUMBER_ATTR       = "Jenkins-Build-Number";
    private static final String BUILD_ID_ATTR           = "Jenkins-Build-ID";
    private static final String BUILD_COMMIT_ATTR       = "Git-Commit";
    private static final String BUILD_BRANCH_ATTR       = "Git-Branch";
    private static final String BUILD_JDK_ATTR          = "Build-Jdk";

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(AboutApplicationServlet.class);

    /**
     * The DE configuration properties.
     */
    private DiscoveryEnvironmentProperties deProps;
    private Attributes manifestAttrs;

    /**
     * The default constructor.
     */
    public AboutApplicationServlet() {}

    /**
     * @param deProps the DE configuration properties.
     */
    public AboutApplicationServlet(DiscoveryEnvironmentProperties deProps) {
        this.deProps = deProps;
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if the discovery environment properties can't be loaded.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (deProps == null) {
            deProps = DiscoveryEnvironmentProperties.getDiscoveryEnvironmentProperties(getServletContext());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAboutInfo() {
        return produceInfo();
    }

    /**
     * Produces the information used to build the about application screen.
     *
     * @return a JSON string containing the build number and release version.
     */
    private String produceInfo() {
        JSONObject json = new JSONObject();

        json.put("release", getReleaseVersion());
        json.put("build", getBuildName());
        json.put("buildNumber", getBuildNumber());
        json.put("buildId", getManifestAttribute(BUILD_ID_ATTR));
        json.put("buildCommit", getManifestAttribute(BUILD_COMMIT_ATTR));
        json.put("buildBranch", getManifestAttribute(BUILD_BRANCH_ATTR));
        json.put("buildJdk", getManifestAttribute(BUILD_JDK_ATTR));

        String response = json.toString(4);
        LOG.debug("the about application JSON is: " + response);
        return response;
    }

    /**
     * Get the release version to report in the about application screen. If the release version is
     * available in the DiscoveryEnvironmentProperties, then that release version will be used.
     * Otherwise, the default release version will be used.
     * 
     * @return a string representation of the release version.
     */
    private String getReleaseVersion() {
        String version = deProps.getReleaseVersion();
        return (StringUtils.isNotEmpty(version)) ? version : DEFAULT_RELEASE_VERSION;
    }

    /**
     * Get the build name to report in the about application screen. If the build name is available in
     * the DiscoveryEnvironmentProperties, then that build name will be used. Otherwise, the build number
     * will be used.
     * 
     * @return a string representation of the build name.
     */
    private String getBuildName() {
        String buildName = deProps.getDefaultBuildNumber();
        return StringUtils.isNotEmpty(buildName) ? buildName : getBuildNumber();
    }

    /**
     * Get the build number from the project manifest.
     * 
     * @return a string representation of the build number.
     */
    private String getBuildNumber() {
        return getManifestAttribute(BUILD_NUMBER_ATTR);
    }

    /**
     * Attempts to load the main {@link Attributes} from the manifest file.
     */
    private void getManifestAttributes() {
        try {
            Manifest manifest = new Manifest(getServletContext().getResourceAsStream(MANIFEST_LOC));
            manifestAttrs = manifest.getMainAttributes();
        } catch (Exception e) {
            LOG.error("unable to get Manifest Attributes", e);
        }
    }

    /**
     * Attempts to obtain an attribute's value from the manifest file.
     * 
     * @return the attribute's value or an empty value (null or the empty string) if the manifest or its
     *         attribute is not available.
     */
    private String getManifestAttribute(String attr) {
        if (manifestAttrs == null) {
            getManifestAttributes();
        }

        if (manifestAttrs != null) {
            return manifestAttrs.getValue(attr);
        }

        return null;
    }
}
