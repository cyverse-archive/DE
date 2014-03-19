package org.iplantc.de.server;

import org.iplantc.clavin.spring.ConfigAliasResolver;
import org.iplantc.de.shared.ConfluenceException;
import org.iplantc.de.shared.services.ConfluenceService;

import com.google.gwt.user.client.rpc.SerializationException;

import org.apache.log4j.Logger;
import org.swift.common.cli.CliClient.ClientException;
import org.swift.common.soap.confluence.RemoteComment;
import org.swift.common.soap.confluence.RemotePage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.servlet.ServletException;

/**
 * A service for interfacing with Confluence via SOAP.
 *
 * @author hariolf
 */
@SuppressWarnings("nls")
public class ConfluenceServlet extends SessionManagementServlet implements ConfluenceService {
    private static final long serialVersionUID = -8576144366505536966L;

    private static final Logger LOG = Logger.getLogger(ConfluenceServlet.class);

    /** A filled star */
    private static final String BLACK_STAR = "&#x2605;";

    /** An outlined star */
    private static final String WHITE_STAR = "&#x2606;";

    /** Name of the session attribute that stores the authentication token */
    private static final String AUTH_TOKEN_ATTR = "confluenceToken";

    /** a wiki template file containing Confluence markup */
    private static final String TEMPLATE_FILE = "wiki_template";

    /** a string in the template that is replaced with the tool name */
    private static final String TOOL_NAME_PLACEHOLDER = "@TOOLNAME";

    /** a string in the template that is replaced with the tool description */
    private static final String DESCRIPTION_PLACEHOLDER = "@DESCRIPTION";

    /** a string in the template that is replaced with rating stars */
    private static final String AVG_RATING_PLACEHOLDER = "@AVGRATING";

    private ConfluenceProperties properties;

    /**
     * The default constructor.
     */
    public ConfluenceServlet() {}

    /**
     * @param properties the configuration settings used to initialize the ConfluenceProperties instance.
     */
    public ConfluenceServlet(Properties properties) {
        this.properties = new ConfluenceProperties(properties);
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialzied.
     * @throws IllegalStateException if the Confluence client configuration settings can't be loaded.
     */
    @Override public void init() throws ServletException {
        super.init();
        if (properties == null) {
            Properties props = ConfigAliasResolver.getRequiredAliasedConfigFrom(getServletContext(), "confluence");
            properties = new ConfluenceProperties(props);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String addPage(String toolName, String description) throws ConfluenceException {
        String content;
        try {
            content = getTemplate();
            content = replaceTemplate(content, toolName, description);
        } catch (IOException e) {
            LOG.error("Can't read wiki template file.", e);
            // if the template cannot be read, use the raw description text instead
            content = description;
        }

        try {
            return getConfluenceClient().addPage(toolName, content);
        } catch (Exception e) {
            throw new ConfluenceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePage(String toolName, int avgRating) throws ConfluenceException {
        String space = properties.getConfluenceSpaceName();
        try {
            IPlantConfluenceClient client = getConfluenceClient();
            long pageId = client.getContentId(toolName, space);
            RemotePage page = client.getPage(pageId);
            String content = page.getContent();

            int start = content.indexOf(BLACK_STAR);
            if (start < 0) {
                start = content.indexOf(WHITE_STAR);
            }
            if (start < 0) {
                return;
            }

            // remove all stars
            content = content.replaceAll(BLACK_STAR, "");
            content = content.replaceAll(WHITE_STAR, "");

            // add the new stars
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < avgRating; i++) {
                stars.append(BLACK_STAR);
            }
            for (int i = 0; i < 5 - avgRating; i++) {
                stars.append(WHITE_STAR);
            }
            content = content.substring(0, start) + stars + content.substring(start);

            // update page
            client.storePage(page, toolName, space, null, content, true, false);
        } catch (Exception e) {
            throw new ConfluenceException(e);
        }
    }

    /**
     * Substitutes placeholders in a template with a tool name and a tool description.
     *
     * @param template the contents of the template file in one string
     * @param toolName name of the DE tool
     * @param description description of the DE Tool
     * @return a string with the tool name and description filled in
     */
    private String replaceTemplate(String template, String toolName, String description) {
        String defaultStars = WHITE_STAR + WHITE_STAR + WHITE_STAR + WHITE_STAR + WHITE_STAR;
        return template.replaceAll(TOOL_NAME_PLACEHOLDER, Matcher.quoteReplacement(toolName))
                .replaceAll(DESCRIPTION_PLACEHOLDER, Matcher.quoteReplacement(description))
                .replace(AVG_RATING_PLACEHOLDER, defaultStars);
    }

    /**
     * Reads the template file from a Resource in the same directory as this class. Although
     * ConfluenceClient can read a file via the --file option, this option is not used here because the
     * file is not guaranteed to be in the file system (it could be in a .jar).
     *
     * @return the contents of TEMPLATE_FILE
     * @throws IOException
     */
    private String getTemplate() throws IOException {
        InputStream stream = ConfluenceServlet.class.getResourceAsStream(TEMPLATE_FILE);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[1024];
            while (true) {
                int charsRead = reader.read(buffer);
                if (charsRead < 0) {
                    break;
                }
                builder.append(Arrays.copyOf(buffer, charsRead));
            }
            return builder.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String addComment(String toolName, int score, String username, String comment)
            throws ConfluenceException {
        String space = properties.getConfluenceSpaceName();
        try {
            comment = generateCommentMarkup(comment, score, username);
            RemoteComment confluenceComment = getConfluenceClient().addComment(space, toolName, comment);
            return String.valueOf(confluenceComment.getId());
        } catch (Exception e) {
            throw new ConfluenceException(e);
        }
    }

    /** adds rating stars and the DE user name to a user-entered comment */
    private String generateCommentMarkup(String comment, int score, String username) {
        StringBuilder markup = new StringBuilder();
        // add full stars
        for (int i = 0; i < score; i++) {
            markup.append(BLACK_STAR);
        }
        // add empty stars so it is 5 stars total
        for (int i = 0; i < 5 - score; i++) {
            markup.append(WHITE_STAR);
        }
        markup.append(" ").append(comment).append(" ");

        // replace placeholder with username
        String suffix = properties.getRatingCommentSuffix();
        suffix = suffix.replace("{0}", username);

        markup.append(suffix);
        return markup.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeComment(String toolName, Long commentId) throws ConfluenceException {
        try {
            getConfluenceClient().removeComment(commentId);
        } catch (Exception e) {
            throw new ConfluenceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void editComment(String toolName, int score, String username, Long commentId,
            String newComment) throws ConfluenceException {
        try {
            newComment = generateCommentMarkup(newComment, score, username);
            getConfluenceClient().editComment(commentId, newComment);
        } catch (Exception e) {
            throw new ConfluenceException(e);
        }
    }

    @Override
    public String movePage(String oldAppName, String newAppName) throws ConfluenceException {
        try {
            return getConfluenceClient().movePage(oldAppName, newAppName);
        } catch (Exception e) {
            throw new ConfluenceException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getComment(long commentId) throws ConfluenceException {
        try {
            String comment = getConfluenceClient().getComment(commentId);
            return removeCommentMarkup(comment);
        } catch (Exception e) {
            throw new ConfluenceException(e);
        }
    }

    /** extracts the raw text from a comment by removing stars and the user name */
    private String removeCommentMarkup(String comment) {
        // remove suffix
        String suffixMarker = properties.getRatingCommentSuffix();
        int markerLen = suffixMarker.indexOf(' ');
        if (markerLen >= 0) {
            // marker = beginning of the display string up to the first space char
            suffixMarker = suffixMarker.substring(0, markerLen);
            int suffixStart = comment.lastIndexOf(suffixMarker);
            if (suffixStart >= 0) {
                comment = comment.substring(0, suffixStart);
                comment = comment.trim();
            }
        }

        // remove chars added at the beginning by generateCommentMarkup()
        int prefixLen = BLACK_STAR.length() * 5 + 1; // assume BLACK_STAR.len == WHITE_STAR.len
        if (comment.length() >= prefixLen) {
            comment = comment.substring(prefixLen);
        }

        return comment;
    }

    private IPlantConfluenceClient getConfluenceClient() throws SerializationException, ClientException {
        String authToken = getAttribute(AUTH_TOKEN_ATTR);
        return new IPlantConfluenceClient(properties, authToken);
    }
}
