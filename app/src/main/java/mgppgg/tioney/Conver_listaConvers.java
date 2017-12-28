package mgppgg.tioney;

import java.io.Serializable;

/**
 * Created by pablich on 26/12/2017.
 */

public class Conver_listaConvers implements Serializable {
    private String user;
    private String UID;
    private String chatUrl;

    public Conver_listaConvers(){}
    public Conver_listaConvers(String u,String url,String id){
        this.user = u;
        this.chatUrl = url;
        this.UID = id;
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

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
