package org.iplantc.de.server;

import com.google.common.base.Strings;
import com.google.common.io.Closeables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Used to determine whether or not the DE is under maintenance.
 */
public class DiscoveryEnvironmentMaintenance {

    private final String startTime;

    private final String endTime;

    private final boolean underMaintenance;

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public boolean hasMaintenanceTimes() {
        return !(Strings.isNullOrEmpty(startTime) || Strings.isNullOrEmpty(endTime));
    }

    public DiscoveryEnvironmentMaintenance(String maintenanceFileName) {
        File maintenanceFile = new File(maintenanceFileName);
        if (maintenanceFile.exists()) {
            String[] maintenanceTimes = loadMaintenanceTimes(maintenanceFile);
            startTime = maintenanceTimes[0];
            endTime = maintenanceTimes[1];
            underMaintenance = true;
        } else {
            startTime = "";
            endTime = "";
            underMaintenance = false;
        }
    }

    private String[] loadMaintenanceTimes(File maintenanceFile) {
        String[] result = new String[2];
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(maintenanceFile));
            result[0] = Strings.nullToEmpty(in.readLine()).trim();
            result[1] = Strings.nullToEmpty(in.readLine()).trim();
        } catch (IOException e) {
            result[0] = "";
            result[1] = "";
        } finally {
            Closeables.closeQuietly(in);
        }
        return result;
    }
}
