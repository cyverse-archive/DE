package org.iplantc.de.shared;

/**
 * A singleton service that provides an asynchronous proxy to services that require the user's
 * authentication to be validated.
 * 
 * @author Dennis Roberts
 */
public class SharedAuthenticationValidatingServiceFacade extends SharedServiceFacade {
    /**
     * the name of the service.
     */
    private static final String DE_SERVICE = "auth-de-service";

    /**
     * The single instance of this class.
     */
    private static SharedAuthenticationValidatingServiceFacade instance;

    /**
     * Initializes a new service facade.
     */
    protected SharedAuthenticationValidatingServiceFacade() {
        super(DE_SERVICE);
    }

    /**
     * Gets the single instance of this class, creating an instance if one hasn't been created yet.
     * 
     * @return the single instance of this class.
     */
    public static SharedAuthenticationValidatingServiceFacade getInstance() {
        if (instance == null) {
            instance = new SharedAuthenticationValidatingServiceFacade();
        }
        return instance;
    }
}
