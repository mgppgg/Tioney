package mgppgg.tioney;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class Publicar extends AppCompatActivity {

    private Button BtnSubir;
    UploadTask uploadTask;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        BtnSubir = (Button)findViewById(R.id.BtnSubir);

        BtnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storageRef = storage.getReference();

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
                });


            }
        });

    }
}
