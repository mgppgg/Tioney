package mgppgg.tioney;

/**
 * Created by pablich on 26/12/2017.
 */

public class AnunDatabase {
    private String titulo;
    private String precio;
    private String url;
    private String email;
    private String usuario;


    public  AnunDatabase(String t,String p,String u,String e,String usu){
        this.titulo = t;
        this.precio = p;
        this.url = u;
        this.email = e;
        this.usuario = usu;

    }
    public  AnunDatabase(){
        this.titulo = "";
        this.precio = "";
        this.url = "";
        this.email = "";

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
