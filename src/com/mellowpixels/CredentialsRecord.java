package com.mellowpixels;

public class CredentialsRecord {

    private String resourceType = null;
    private String resource = null;
    private String login = null;
    private String password = null;

    public CredentialsRecord(String resourceType, String resource, String login, String password) {
        this.resourceType = resourceType;
        this.resource = resource;
        this.login = login;
        this.password = password;
    }


    public String getResourceType() {
        return this.resourceType;
    }
    public void setResourceType(String resourceType){
        this.resourceType = resourceType;
    }

    public String getResource() {
        return this.resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getLogin() {
        return this.login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
