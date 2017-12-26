package mgppgg.tioney;

/**
 * Created by pablich on 26/12/2017.
 */

public class Conver_listaConvers {
    private String email;
    private String user;

    public Conver_listaConvers(){}
    public Conver_listaConvers(String e,String u){
        this.email = e;
        this.user = u;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
