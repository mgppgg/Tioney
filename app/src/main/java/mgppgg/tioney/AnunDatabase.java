package mgppgg.tioney;

import android.location.Location;

/**
 * Created by pablich on 26/12/2017.
 */

public class AnunDatabase {
    private String titulo;
    private String precio;
    private String url;
    private String UID;
    private String usuario;
    private int fotos;
    private Location local;



    public  AnunDatabase(String t,String p,String url,String e,String usu,int foto,Location l){
        this.titulo = t;
        this.precio = p;
        this.url = url;
        this.UID = e;
        this.usuario = usu;
        this.fotos = foto;
        this.local=l;

    }
    public  AnunDatabase(){
        this.titulo = "";
        this.precio = "";
        this.url = "";
        this.UID = "";
        this.usuario="";
        this.fotos=0;

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

    public int getFotos() {
        return fotos;
    }

    public void setFotos(int fotos) {
        this.fotos = fotos;
    }

    public Location getLocal() {
        return local;
    }

    public void setLocal(Location local) {
        this.local = local;
    }
}
