package mgppgg.tioney;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;



public class Publicar extends BaseActivity {

    private Button BtnSubir;
    private ImageButton BtnIma1;
    private ImageButton BtnIma2;
    private ImageButton BtnIma3;
    private EditText ETdescripcion;
    private StorageReference storageRef;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        BtnSubir = (Button)findViewById(R.id.BtnSubir);
        BtnIma1 = (ImageButton)findViewById(R.id.imageButton1);
        BtnIma2 = (ImageButton)findViewById(R.id.imageButton2);
        BtnIma3 = (ImageButton)findViewById(R.id.imageButton3);
        ETdescripcion = (EditText)findViewById(R.id.ETdescripcion);

        storageRef = FirebaseStorage.getInstance().getReference();

        BtnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOnlineNet()==true) checkPermission();
                else Toast.makeText(Publicar.this, "Conexión a internet no disponible", Toast.LENGTH_SHORT).show();

            }
        });

        BtnIma1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // BtnIma1.setImageURI();

            }
        });

    }

    @TargetApi(24)
    public void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione imagenes"),10);
    }


    @TargetApi(24)
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        showProgressDialog();


        if(resultCode==RESULT_OK){
            Uri uri = imageReturnedIntent.getData();
            String descripcion = ETdescripcion.getText().toString();
            InputStream streamDescripcion = new ByteArrayInputStream(descripcion.getBytes());
            StorageReference filepathFotos = storageRef.child("Anuncios/"+uri.getLastPathSegment());
            StorageReference filepathDescripcion = storageRef.child("Anuncios/"+ UUID.randomUUID().toString());

            filepathFotos.putFile(uri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideProgressDialog();
                    Toast.makeText(Publicar.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressDialog();
                }
            });

            filepathDescripcion.putStream(streamDescripcion).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideProgressDialog();
                    Toast.makeText(Publicar.this, "Error al subir tetxo", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hideProgressDialog();
                }
            });
        }

    }



    @TargetApi(24)
    private void checkPermission() {

            int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);

            }else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED){

                abrirGaleria();

            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    abrirGaleria();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}

