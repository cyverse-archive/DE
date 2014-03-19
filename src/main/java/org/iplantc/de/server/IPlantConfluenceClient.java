package org.iplantc.de.server;

import com.martiansoftware.jsap.JSAP;

import org.apache.log4j.Logger;
import org.swift.common.soap.confluence.InvalidSessionException;
import org.swift.common.soap.confluence.RemoteComment;
import org.swift.common.soap.confluence.RemotePage;
import org.swift.confluence.cli.ConfluenceClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.rmi.RemoteException;

/**
 * A subclass of ConfluenceClient that adds methods for adding and updating tool ratings.
 *
 * @author hariolf
 *
 */
public class IPlantConfluenceClient extends ConfluenceClient {
    private static final Logger LOG = Logger.getLogger(IPlantConfluenceClient.class);

    private final ConfluenceProperties properties;

    /**
     * Creates a new instance and initializes address/user/password from a {@link Properties} instance.
     *
     * @param properties
     * @param authToken a token for an active Confluence session, or null if not already logged in
     * @throws ClientException
     */
    public IPlantConfluenceClient(ConfluenceProperties properties, String authToken)
            throws ClientException {
        this.properties = properties;
        if (authToken != null) {
            token = authToken;
        }
    }

    /**
     * Creates a new page in the iPlant wiki as a child of the "List of Applications" page.
     *
     * @param title the page title
     * @param content the page content
     * @return
     * @throws RemoteException
     * @throws ClientException
     */
    public String addPage(final String title, final String content) throws RemoteException,
            ClientException {
        final String parent = properties.getConfluenceParentPage();
        final String space = properties.getConfluenceSpaceName();

        final String safeTitle = title.replaceAll("[:@/\\\\|^#;\\[\\]{}<>]", "_") //$NON-NLS-1$ //$NON-NLS-2$
                .replaceAll("^[$~]", "_").replaceAll("^[.]+", "_"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        callService(new ServiceCall<Void>() {
            @Override
            public Void doit() throws RemoteException, ClientException {
                RemotePage page = null;

                try {
                    try {
                        page = getPage(safeTitle, space);
                    } catch (ClientException cx) {
                        LOG.debug("Page does not exist?\n" + cx.getMessage());
                        // page does not exist, continue creating page.
                    }

                    if (page == null) {
                        // page does not exist, create it here.
                        page = new RemotePage();
                        storePage(page, safeTitle, space, parent, content, false, true);
                    }
                } catch (RemoteException rx) {
                    LOG.error(rx.getMessage());
                    throw rx;
                } catch (ClientException cx) {
                    LOG.error(cx.getMessage());
                    throw cx;
                }

                return null;
            }
        });

        try {
            return properties.getConfluenceSpaceUrl() + URLEncoder.encode(safeTitle, "UTF-8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new ClientException("Unable to encode the documentaiton URL");
        }
    }

    public String movePage(final String oldTitle, final String newTitle) throws RemoteException,
            ClientException {
        final String parent = properties.getConfluenceParentPage();
        final String space = properties.getConfluenceSpaceName();
        callService(new ServiceCall<Void>() {
            @Override
            public Void doit() throws RemoteException, ClientException {
                try {
                    if (getPage(oldTitle, space) != null) {
                        renamePage(space, oldTitle, parent, space, newTitle);
                    }
                    return null;
                } catch (RemoteException rx) {
                    LOG.error(rx.getMessage());
                    throw rx;
                } catch (ClientException cx) {
                    LOG.error(cx.getMessage());
                    throw cx;
                }
            }
        });

        try {
            return properties.getConfluenceSpaceUrl() + URLEncoder.encode(newTitle, "UTF-8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new ClientException("Unable to encode the documentaiton URL");
        }

    }

    /**
     * Adds a comment to an existing Confluence page and returns an object containing the new comment's
     * ID, etc.
     *
     * @param space the Confluence space the page lives in
     * @param pageTitle the title of the page to add a comment to
     * @param text the comment text
     * @return a RemoteComment instance
     * @throws RemoteException
     * @throws ClientException
     */
    public RemoteComment addComment(final String space, final String pageTitle, String text)
            throws RemoteException, ClientException {
        RemotePage page = callService(new ServiceCall<RemotePage>() {
            @Override
            public RemotePage doit() throws RemoteException, ClientException {
                return getPage(pageTitle, space);
            }
        });
        final RemoteComment comment = new RemoteComment();
        comment.setPageId(page.getId());
        comment.setContent(text);

        return callService(new ServiceCall<RemoteComment>() {
            @Override
            public RemoteComment doit() throws RemoteException {
                return service.addComment(token, comment);
            }
        });
    }

    /**
     * Changes an existing comment.
     *
     * @param commentId the comment's ID in Confluence
     * @param newText the new comment; must have the correct ID and service address set
     * @throws RemoteException
     * @throws ClientException
     */
    public void editComment(long commentId, String newText) throws RemoteException, ClientException {
        final RemoteComment newComment = new RemoteComment();
        newComment.setId(commentId);
        newComment.setUrl(address);
        newComment.setContent(newText);

        callService(new ServiceCall<Void>() {
            @Override
            public Void doit() throws RemoteException {
                service.editComment(token, newComment);
                return null;
            }
        });
    }

    /**
     * Removes a comment
     *
     * @param commentId the comment's ID in Confluence
     * @throws RemoteException
     * @throws ClientException
     */
    public void removeComment(final long commentId) throws RemoteException, ClientException {
        callService(new ServiceCall<Void>() {
            @Override
            public Void doit() throws RemoteException {
                service.removeComment(token, commentId);
                return null;
            }
        });
    }

    /**
     * Retrieves a comment from a Confluence page.
     *
     * @param commentId the comment ID in Confluence
     * @return the comment text
     * @throws ClientException
     * @throws RemoteException
     */
    public String getComment(final long commentId) throws RemoteException, ClientException {
        return callService(new ServiceCall<String>() {
            @Override
            public String doit() throws RemoteException {
                return service.getComment(token, commentId).getContent();
            }
        });
    }

    /**
     * Logs a user into Confluence and sets the authentication token.
     *
     * @throws ClientException
     */
    private void iplantLogin() throws ClientException {
        String address = properties.getConfluenceBaseUrl();
        String user = properties.getConfluenceUser();
        String password = properties.getConfluencePassword();

        ExitCode code = doWork(new String[] {"-a", "login", "--server", address, "--user", user, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                "--password", password}); //$NON-NLS-1$
        if (code != ExitCode.SUCCESS)
            throw new ClientException("Login failure! Exit code = " + code); //$NON-NLS-1$
    }

    /** like getContentId(String, String, false) but can be used without going through doWork() */
    @Override
    public long getContentId(final String title, final String space)
            throws java.rmi.RemoteException, ClientException {
        return callService(new ServiceCall<Long>() {
            @Override
            public Long doit() throws ClientException, RemoteException {
                return getContentId(title, space, false);
            }
        });
    }

    /** calls the confluence service and handles authentication */
    private <T> T callService(ServiceCall<T> call) throws ClientException, RemoteException {
        // first make sure the service is initialized
        if (service == null) {
            iplantLogin();
        } else {
            // init some fields that aren't set if the login code in ConfluenceClient isn't called
            jsap = new JSAP();
            jsapResult = jsap.parse(new String[] {});
        }

        T result;
        try {
            result = call.doit();
        } catch (InvalidSessionException e) {
            // if the session timed out, log in and try again
            iplantLogin();
            result = call.doit();
        }
        return result;
    }

    @Override
    public String getToken() {
        if (token == null) {
            try {
                iplantLogin();
            } catch (ClientException e) {
                log.error("Cannot login", e); //$NON-NLS-1$
                return null;
            }
        }
        return token;
    }

    private interface ServiceCall<T> {
        /** performs the service call */
        T doit() throws ClientException, RemoteException;
    }
}