package recycler_view;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

/**
 * Created by pablich on 19/11/2017.
 */

public class Anuncio implements Serializable {

    private String titulo,descripcion,precio,UID,usuario,url,categoria;
    private int fotos;
    private double longitud,latitud;

    public Anuncio(){
        titulo="";
        descripcion="";
        precio="";
        UID="";
        usuario="";
        url="";
        categoria="";
        fotos=0;
        longitud=0;
        latitud=0;
    }

    public void setTitulo(String str){
        this.titulo = str;
    }

    public void setDescripcion(String str){
        this.descripcion = str;
    }

    public void setPrecio(String str){
        this.precio = str;
    }

    public String getTitulo(){
        return titulo;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public String getPrecio(){
        return precio;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
}
