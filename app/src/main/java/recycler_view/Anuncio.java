package recycler_view;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.firebase.storage.StorageReference;

/**
 * Created by pablich on 19/11/2017.
 */

public class Anuncio {

    private StorageReference[] imagenes;
    private String titulo,descripcion,precio;

    public Anuncio(){
        imagenes =new  StorageReference[4];
        for(int i =0;i<4;i++){
            imagenes[i] = null;
        }
    }

    public void setIma(StorageReference url1,StorageReference url2,StorageReference url3,StorageReference url4){
        imagenes[0] = url1;
        imagenes[1] = url2;
        imagenes[2] = url3;
        imagenes[3] = url4;

    }

    public StorageReference getIma(int i){
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

}
