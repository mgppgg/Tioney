package mgppgg.tioney;

import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * Created by pablich on 30/12/2017.
 */

public class ArrrayUri {

    private int id;
    private Uri uri;
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getIma() {
        return ima;
    }

    public void setIma(String ima) {
        this.ima = ima;
    }
}
