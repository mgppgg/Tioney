package mgppgg.tioney;

import android.net.Uri;

/**
 * Created by pablich on 30/12/2017.
 */

public class ArrrayUri {

    private int id;
    private Uri uri;

    public ArrrayUri(int i,Uri u){
        this.id=i;
        this.uri = u;
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
}
