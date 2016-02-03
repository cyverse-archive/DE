package org.iplantc.de.client.models.sharing;

public class SharedResource {

    /**
     * id of the shared resource
     */
    private String id;

    /**
     * name of the shared resource
     */
    private String name;

    public SharedResource(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
