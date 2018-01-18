package mgppgg.tioney;

/**
 * Created by pablich on 12/12/2017.
 */

public class Usuario {
    private String nombre;
    private String correo;
    private String token;

    public Usuario(String nom,String cor, String t){
        this.nombre = nom;
        this.token = t;
        this.correo = cor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getToken(){
        return token;
    }

    public String getCorreo(){
        return correo;
    }
}
