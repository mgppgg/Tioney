package mgppgg.tioney;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by pablich on 19/11/2017.
 */

public class Anuncio {

    private String[] imagenes;
    private String titulo,descripcion,precio;

    public Anuncio(String tit){
        imagenes =new  String[4];
        this.titulo = tit;
        descripcion=null;
        precio = null;

        for(int i =0;i<4;i++){
            imagenes[i] = null;
        }
    }

    public void setIma(String url1,String url2,String url3,String url4){
        imagenes[0] = url1;
        imagenes[1] = url2;
        imagenes[2] = url3;
        imagenes[3] = url4;

    }

    public String getIma(int i){
        return imagenes[i];
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
