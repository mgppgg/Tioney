package mgppgg.tioney;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by pablich on 26/12/2017.
 */

public class AnunDatabase implements Serializable {
    private String titulo;
    private String precio;
    private String descripcion;
    private String url;
    private String UID;
    private String usuario;
    private String categoria;
    private int fotos;
    private double longitud;
    private double latitud;
    private long fecha;

    public  AnunDatabase(String t,String p,String des,String url,String e,String usu,String cat,int foto,double longi,double lat,long f){
        this.titulo = t;
        this.precio = p;
        this.descripcion = des;
        this.url = url;
        this.UID = e;
        this.usuario = usu;
        this.categoria = cat;
        this.fotos = foto;
        this.longitud=longi;
        this.latitud=lat;
        this.fecha = f;

    }
    public  AnunDatabase(){
        this.titulo = "";
        this.precio = "";
        this.descripcion = "";
        this.url = "";
        this.UID = "";
        this.usuario="";
        this.categoria="";
        this.fotos=0;
        this.latitud=0;
        this.longitud=0;
        this.fecha = 0;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

}
