package mgppgg.tioney;

/**
 * Created by pablich on 26/12/2017.
 */

public class AnunDatabase {
    private String titulo;
    private String precio;
    private String url;
    private String UID;
    private String usuario;



    public  AnunDatabase(String t,String p,String url,String e,String usu){
        this.titulo = t;
        this.precio = p;
        this.url = url;
        this.UID = e;
        this.usuario = usu;

    }
    public  AnunDatabase(){
        this.titulo = "";
        this.precio = "";
        this.url = "";
        this.UID = "";

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

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }


}
