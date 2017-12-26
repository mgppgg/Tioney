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

    private String[] imagenes;
    private String titulo,descripcion,precio,email,usuario;

    public Anuncio(){
        imagenes =new  String[4];
        for(int i =0;i<4;i++){
            imagenes[i] = null;
        }
    }


    public void setIma(String url1, String url2, String url3, String url4){
        imagenes[0] = url1;
        imagenes[1] = url2;
        imagenes[2] = url3;
        imagenes[3] = url4;

    }

    public void setNull(int n){
        imagenes[n] = null;
    }

    public String getIma(int i){
        return imagenes[i];
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
