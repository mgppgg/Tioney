package mgppgg.tioney;

/**
 * Created by pablich on 26/12/2017.
 */

public class Conver_listaConvers {
    private String user;
    private String chatUrl;

    public Conver_listaConvers(){}
    public Conver_listaConvers(String u,String url){
        this.user = u;
        this.chatUrl = url;
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
}
