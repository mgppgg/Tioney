package mgppgg.tioney;

import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * Created by pablich on 30/12/2017.
 */

public class ArrrayUri {

    private int id;
    private String ima;

    public ArrrayUri(int i,String ima){
        this.id=i;
        this.ima = ima;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIma() {
        return ima;
    }

    public void setIma(String ima) {
        this.ima = ima;
    }
}
