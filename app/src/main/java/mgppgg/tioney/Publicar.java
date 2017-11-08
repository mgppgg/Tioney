package mgppgg.tioney;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.R.attr.data;


public class Publicar extends AppCompatActivity {

    private Button BtnSubir;
    //UploadTask uploadTask;
    //FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private static final int GALLERY_INTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        BtnSubir = (Button)findViewById(R.id.BtnSubir);

        storageRef = FirebaseStorage.getInstance().getReference();

        BtnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                abrirGaleria();

                /*storageRef = storage.getReference();

                Uri file = Uri.fromFile(new File("0/DCIM/Camera/IMG_20160724_161338.jpg"));
                StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                uploadTask = riversRef.putFile(file);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        @SuppressWarnings("VisibleForTests")Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });*/


            }
        });

    }


    public void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione imagenes"),GALLERY_INTENT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            Uri uri = imageReturnedIntent.getData();
            StorageReference filepath = storageRef.child("fotos").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(Publicar.this, "Subida correcta", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}
