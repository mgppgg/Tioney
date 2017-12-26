package mgppgg.tioney;

/**
 * Created by pablich on 26/12/2017.
 */

public class AnunDatabase {
    private String titulo;
    private String precio;
    private String url;
    private String email;


    public  AnunDatabase(String t,String p,String u,String e){
        this.titulo = t;
        this.precio = p;
        this.url = u;
        this.email = e;

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
}
