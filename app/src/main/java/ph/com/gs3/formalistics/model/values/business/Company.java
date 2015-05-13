package ph.com.gs3.formalistics.model.values.business;

import java.io.Serializable;

public class Company implements Serializable {

    private int id;
    private int webId;
    private String name;
    private String server;

    @Override
    public String toString() {
        return webId + " " + name + "@" + server;
    }

    // {{ Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        if (server != null) {
            server = server.trim();
        } else {
            server = "";
        }

        // make sure that the server contains http
        if (!server.contains("http://") && !server.isEmpty()) {
            this.server = "http://" + server;
        } else {
            this.server = server;
        }
    }

    // }}

}
