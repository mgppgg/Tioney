package mgppgg.tioney;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by manug on 24/10/2017.
 */

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog mProgressDialog;

    public void showProgressDialog(Context context,String texto) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(texto);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val  = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void snackBar(String text ){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text,  Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        snackbar.setActionTextColor(Color.BLACK);
        sbView.setBackgroundColor(Color.RED);
        snackbar.show();
    }


}
