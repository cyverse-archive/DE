package org.iplantc.de.tools.requests.client.presenter;

// TODO this should be move to ui-commons.

/**
 * This is an interface like Command that accepts a single input parameter.
 * 
 * @param <T> the type of input parameter
 */
public interface Continuation<T> {

    /**
     * The command to execute when appropriate.
     * 
     * @param arg The argument needed to execute the command.
     */
    void execute(T arg);

}
