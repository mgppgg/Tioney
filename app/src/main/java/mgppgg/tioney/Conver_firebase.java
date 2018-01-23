package mgppgg.tioney;

import java.io.Serializable;

/**
 * Created by pablich on 26/12/2017.
 */

public class Conver_firebase implements Serializable {
    private String user;
    private String chatUrl;
    private int nuevo_msg;

    public Conver_firebase(){}
    public Conver_firebase(String u, String url, int nuevo){
        this.user = u;
        this.chatUrl = url;
        this.nuevo_msg = nuevo;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public int getNuevo_msg() {
        return nuevo_msg;
    }

    public void setNuevo_msg(int nuevo_msg) {
        this.nuevo_msg = nuevo_msg;
    }
}
