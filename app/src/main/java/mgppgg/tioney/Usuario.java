package mgppgg.tioney;

/**
 * Created by pablich on 12/12/2017.
 */

public class Usuario {
    private String nombre;
    private String correo;
    private int numAnun;

    public Usuario(String nom,String cor, int n){
        this.nombre = nom;
        this.numAnun = n;
        this.correo = cor;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNumAnun(){
        return numAnun;
    }

    public String getCorreo(){
        return correo;
    }
}
