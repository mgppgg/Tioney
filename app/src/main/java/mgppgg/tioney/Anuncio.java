package mgppgg.tioney;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by pablich on 19/11/2017.
 */

public class Anuncio {

    private Drawable[] imagenes;
    private String titulo,descripcion,precio;

    public Anuncio(){
        imagenes =new  Drawable[4];
        titulo = null;
        descripcion=null;
        precio = null;

        for(int i =0;i<4;i++){
            imagenes[i] = null;
        }
    }

    public void setIma(Drawable ima1,Drawable ima2,Drawable ima3,Drawable ima4){
        imagenes[0] = ima1;
        imagenes[1] = ima2;
        imagenes[2] = ima3;
        imagenes[3] = ima4;

    }

    public Drawable[] getIma(){
        return imagenes;
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
