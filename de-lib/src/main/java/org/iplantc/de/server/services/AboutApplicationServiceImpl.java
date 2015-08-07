package org.iplantc.de.server.services;

import org.iplantc.de.shared.services.AboutApplicationService;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletConfig;

/**
 * Communicates application information to include as "about" data regarding the current build and client UserAgent.
 * <p/>
 * This servlet will include information about services once the modeling of software components has been completed.
 *
 * @author lenards, jstroot
 */
public class AboutApplicationServiceImpl implements AboutApplicationService {

    private static final String BUILD_BRANCH_ATTR = "Git-Branch";
    private static final String BUILD_COMMIT_ATTR = "Git-Ref";
    private static final String BUILD_ID_ATTR = "Jenkins-Build-ID";
    private static final String BUILD_JDK_ATTR = "Build-Jdk";
    private static final String BUILD_NUMBER_ATTR = "Jenkins-Build-Number";
    private static final String DEFAULT_RELEASE_VERSION = "unversioned";
    private static final String MANIFEST_LOC = "/META-INF/MANIFEST.MF";
    private static final long serialVersionUID = 6046105023536377635L;
    /**
     * The logger for error and informational messages.
     */
    private final Logger LOG = LoggerFactory.getLogger(AboutApplicationServiceImpl.class);
    private String defaultBuildNumber;
    private Attributes manifestAttrs;
    private String releaseVersion;
    private final ServletConfig servletConfig;

    public AboutApplicationServiceImpl(final String defaultBuildNumber,
                                       final String releaseVersion,
                                       final ServletConfig servletConfig){
        this.defaultBuildNumber = defaultBuildNumber;
        this.releaseVersion = releaseVersion;
        this.servletConfig = servletConfig;
    }

    @Override
    public String getAboutInfo() {
        return produceInfo();
    }

    /**
     * Get the build name to report in the about application screen. If the build name is available in
     * the container properties, then that build name will be used. Otherwise, the build number
     * will be used.
     *
     * @return a string representation of the build name.
     */
    private String getBuildName() {
        return StringUtils.isNotEmpty(defaultBuildNumber) ? defaultBuildNumber : getBuildNumber();
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
     * Attempts to obtain an attribute's value from the manifest file.
     *
     * @return the attribute's value or an empty value (null or the empty string) if the manifest or its
     * attribute is not available.
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

    /**
     * Attempts to load the main {@link Attributes} from the manifest file.
     */
    private void getManifestAttributes() {
        try {
            Manifest manifest = new Manifest(servletConfig.getServletContext().getResourceAsStream(MANIFEST_LOC));
            manifestAttrs = manifest.getMainAttributes();
        } catch (Exception e) {
            LOG.error("unable to get Manifest Attributes", e);
        }
    }

    /**
     * Get the release version to report in the about application screen. If the release version is
     * available in the container properties, then that release version will be used.
     * Otherwise, the default release version will be used.
     *
     * @return a string representation of the release version.
     */
    private String getReleaseVersion() {
        return (StringUtils.isNotEmpty(releaseVersion)) ? releaseVersion : DEFAULT_RELEASE_VERSION;
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
        LOG.trace("Generated About JSON: {}", response);
        return response;
    }
}
