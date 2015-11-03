package org.iplantc.de.client.models.analysis;

public interface BatchStatus {

    int getTotal();

    int getCompleted();

    int getRunning();

    int getFailed();

    int getSubmitted();

}
