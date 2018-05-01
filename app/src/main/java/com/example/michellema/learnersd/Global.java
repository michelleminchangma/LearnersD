package com.example.michellema.learnersd;

/**
 * Created by michellema on 2018-04-01.
 */

public class Global {
    private static Global instance = new Global();

    public static Global getInstance() {
        return instance;
    }

    public static void setInstance(Global instance) {
        Global.instance = instance;
    }

    private String user = "default_user";
    private String url = "https://fr.wikipedia.org/wiki/Montreal";

    private Global() {
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
