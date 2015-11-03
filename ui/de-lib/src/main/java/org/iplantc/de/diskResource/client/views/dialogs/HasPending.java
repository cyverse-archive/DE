package org.iplantc.de.diskResource.client.views.dialogs;

interface HasPending <O> {

    boolean addPending(O obj);
    
    boolean hasPending();
    
    boolean removePending(O obj);
    
    int getNumPending();
}
