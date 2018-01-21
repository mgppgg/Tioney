package mgppgg.tioney;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import recycler_view.Anuncio;



public class Publicar extends BaseActivity {

    private Button BtnSubir, BtnBorrar;
    private ImageButton BtnIma1;
    private ImageButton BtnIma2;
    private ImageButton BtnIma3;
    private ImageButton BtnIma4;
    private int i;
    private String key;
    private ArrayList<ArrrayUri> arrayUris;
    private ArrayList<ImageButton> imageButtons;
    private EditText ETdescripcion;
    private EditText ETtitulo;
    private EditText ETprecio;
    private Anuncio anun2;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference database;
    private FirebaseUser user;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        anun2 = null;
        anun2 = (Anuncio) getIntent().getSerializableExtra("Anuncio");

        BtnSubir = (Button) findViewById(R.id.BtnSubir);
        BtnBorrar = (Button) findViewById(R.id.BtnBorrarAnun);
        BtnIma1 = (ImageButton) findViewById(R.id.imageButton1);
        BtnIma2 = (ImageButton) findViewById(R.id.imageButton2);
        BtnIma3 = (ImageButton) findViewById(R.id.imageButton3);
        BtnIma4 = (ImageButton) findViewById(R.id.imageButton4);
        ETdescripcion = (EditText) findViewById(R.id.ETdescripcion);
        ETtitulo = (EditText) findViewById(R.id.ETtitulo);
        ETprecio = (EditText) findViewById(R.id.ETprecio);
        i = -1;
        arrayUris = new ArrayList<>();

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (anun2 != null) {
            imageButtons = new ArrayList<>();
            imageButtons.add(BtnIma1);
            imageButtons.add(BtnIma2);
            imageButtons.add(BtnIma3);
            imageButtons.add(BtnIma4);

            BtnBorrar.setVisibility(View.VISIBLE);
            BtnSubir.setText("Editar");

            ETtitulo.setText(anun2.getTitulo());
            ETdescripcion.setText(anun2.getDescripcion());
            ETprecio.setText(anun2.getPrecio());
            if (anun2.getFotos() > 0) {
                for (int n = 0; n < anun2.getFotos(); n++) {
                    StorageReference filepathFoto = storage.getReferenceFromUrl(anun2.getUrl() + "Foto" + n);
                    Glide.with(this).using(new FirebaseImageLoader()).load(filepathFoto).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(imageButtons.get(n));
                }
            }

            database.child("Usuarios").child(user.getUid()).child("Anuncios").addListenerForSingleValueEvent(new ValueEventListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AnunDatabase a = postSnapshot.getValue(AnunDatabase.class);
                        if (Objects.equals(a.getTitulo(), anun2.getTitulo()))
                            key = postSnapshot.getKey();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        BtnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnlineNet()) {
                    if (anun2 == null) confirmacion("¿Esta seguro de subir el anuncio?", 0);
                    else confirmacion("¿Esta seguro de editar el anuncio?", 0);
                } else snackBar("Sin conexión a internet");

            }
        });

        BtnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnlineNet()) confirmacion("¿Esta seguro de borrar el anuncio?", 1);
                else snackBar("Sin conexión a internet");
            }
        });

        BtnIma1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 0;
                checkPermission();
            }
        });

        BtnIma2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 1;
                checkPermission();
            }
        });

        BtnIma3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 2;
                checkPermission();
            }
        });

        BtnIma4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 3;
                checkPermission();
            }
        });


    }

    @TargetApi(24)
    public void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione imagen"), 1);
    }


    @TargetApi(24)
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        boolean seguir = true;

        if (resultCode == RESULT_OK) {
            String ima = compressImage(imageReturnedIntent.getData());
            ArrrayUri a = new ArrrayUri(i, ima);

            for (int c = arrayUris.size() - 1; c >= 0 && seguir; c--) {
                if (arrayUris.get(c).getId() == a.getId()) {
                    arrayUris.set(c, a);
                    seguir = false;
                }
            }
            if (seguir) arrayUris.add(a);

            switch (i) {
                case 0:
                    BtnIma1.setImageURI(imageReturnedIntent.getData());
                    break;
                case 1:
                    BtnIma2.setImageURI(imageReturnedIntent.getData());
                    break;
                case 2:
                    BtnIma3.setImageURI(imageReturnedIntent.getData());
                    break;
                case 3:
                    BtnIma4.setImageURI(imageReturnedIntent.getData());
                    break;
            }
        }

    }

    @TargetApi(24)
    private void checkPermission() {

        int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);

        } else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {

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

    public void subir() {

        showProgressDialog(this, "Creando anuncio..");
        final String descripcion, titulo, precio, ID, UID, url, usuario;
        ID = UUID.randomUUID().toString();
        if (validar()) {
            descripcion = ETdescripcion.getText().toString();
            titulo = ETtitulo.getText().toString();
            precio = ETprecio.getText().toString();
            UID = user.getUid();

            if (anun2 == null) {

                usuario = user.getDisplayName();
                url = "gs://tioney-40377.appspot.com/Anuncios/" + ID + "/";
                final InputStream streamDescripcion = new ByteArrayInputStream(descripcion.getBytes());
                final StorageReference filepathDescripcion = storageRef.child("Anuncios/" + ID + "/" + "Descripcion");

                String key1 = database.child("Usuarios").child(user.getUid()).child("Anuncios").push().getKey();
                final Map<String, Object> map = new HashMap<>();
                AnunDatabase anun = new AnunDatabase(titulo, precio, url, UID, usuario, arrayUris.size());
                map.put(key1, anun);

                if (i > -1) {

                    for (int b = 0; b < arrayUris.size(); b++) {

                        arrayUris.get(b).setId(b);

                        StorageReference filepathFotos = storageRef.child("Anuncios/" + ID + "/" + "Foto" + b);
                        Uri file = Uri.fromFile(new File(arrayUris.get(b).getIma()));

                        filepathFotos.putFile(file).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(Publicar.this, "Error al subir fotos", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        });

                    }
                }

                filepathDescripcion.putStream(streamDescripcion).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Publicar.this, "Error al subir anuncio", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        database.child("Usuarios").child(user.getUid()).child("Anuncios").updateChildren(map);
                        database.child("Anuncios1").updateChildren(map);

                        hideProgressDialog();

                        Toast.makeText(Publicar.this, "Publicación subida correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            } else {
                int cont = 0;
                final StorageReference filepathDescripcion = storage.getReferenceFromUrl(anun2.getUrl() + "Descripcion");
                final InputStream streamDescripcion = new ByteArrayInputStream(descripcion.getBytes());

                for (int c = 0; c < arrayUris.size(); c++) {
                    if (arrayUris.get(c).getId() > anun2.getFotos() - 1) cont++;
                }
                final int finalCont = cont;


                filepathDescripcion.putStream(streamDescripcion).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Publicar.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        if (i > -1) {

                            for (int b = 0; b < arrayUris.size(); b++) {

                                StorageReference filepathFotos = storage.getReferenceFromUrl(anun2.getUrl() + "Foto" + arrayUris.get(b).getId());
                                Uri file = Uri.fromFile(new File(arrayUris.get(b).getIma()));

                                filepathFotos.putFile(file).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(Publicar.this, "Error al subir fotos", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    }
                                });

                            }

                        }

                        if (finalCont > 0) {
                            database.child("Usuarios").child(UID).child("Anuncios").child(key).child("fotos").setValue(anun2.getFotos() + finalCont);
                            database.child("Anuncios1").child(key).child("fotos").setValue(anun2.getFotos() + finalCont);
                        }
                        database.child("Usuarios").child(UID).child("Anuncios").child(key).child("titulo").setValue(titulo);
                        database.child("Usuarios").child(UID).child("Anuncios").child(key).child("precio").setValue(precio);
                        database.child("Anuncios1").child(key).child("titulo").setValue(titulo);
                        database.child("Anuncios1").child(key).child("precio").setValue(precio);

                        hideProgressDialog();
                        Toast.makeText(Publicar.this, "Actualizado. Desliza abajo para comprobar", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });


            }

        }


    }


    public boolean validar() {
        boolean v = true;
        if (ETdescripcion.getText().toString().isEmpty()) {
            ETdescripcion.setError("Obligatorio");
            v = false;
        }
        if (ETtitulo.getText().toString().isEmpty()) {
            ETtitulo.setError("Obligatorio");
            v = false;
        }
        if (ETprecio.getText().toString().isEmpty()) {
            ETprecio.setError("Obligatorio");
            v = false;
        }
        if (!v) hideProgressDialog();
        return v;
    }

    public void borrrarAnun() {

        storage.getReferenceFromUrl(anun2.getUrl() + "Descripcion").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                for (int m = 0; m < anun2.getFotos(); m++) {
                    storage.getReferenceFromUrl(anun2.getUrl() + "Foto" + m).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(Publicar.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                database.child("Usuarios").child(user.getUid()).child("Anuncios").child(key).removeValue();
                database.child("Anuncios1").child(key).removeValue();

                Toast.makeText(Publicar.this, "Borrado. Desliza hacia abajo para comprobar", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Publicar.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });


        finish();
    }


    public void confirmacion(String msg, final int opcion) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(msg);
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (opcion == 0) subir();
                        if (opcion == 1) borrrarAnun();
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    public String compressImage(Uri imageUri) {

        String filePath = getRealPathFromURI(imageUri.toString());
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 916.0f;
        float maxWidth = 812.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}

