package recycler_view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mostrar_Anuncio.MostrarAnun;
import mgppgg.tioney.R;

/**
 * Created by pablich on 16/11/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static List<Anuncio> listaAnuncios;
    private static List<String> urls;
    private Context context;
    private static Anuncio anun;
    private FirebaseStorage  storage;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public TextView titulo,descripcion,precio;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView)v.findViewById(R.id.IVfotoCard);
            titulo = (TextView)v.findViewById(R.id.TVtituloCard);
            descripcion = (TextView)v.findViewById(R.id.TVdescripcionCard);
            precio = (TextView)v.findViewById(R.id.TVprecioCard);

            v.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    anun = listaAnuncios.get(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), MostrarAnun.class);
                    intent.putExtra("Anuncio", anun);
                    v.getContext().startActivity(intent);

                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<String> url, Context context) {
        urls = url;
        listaAnuncios = new ArrayList<>();
        this.context = context;
        anun = new Anuncio();
        storage = FirebaseStorage.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Anuncio a = new Anuncio();
        final String paths[] = new String[4];
        final StorageReference filepathTitulo,filepathDescripcion,filepathPrecio;

        for(int i =0;i<4;i++)paths[i]=urls.get(position) + "Foto" + i;
        final StorageReference filepathFoto0 = storage.getReferenceFromUrl(paths[0]);

        filepathTitulo = storage.getReferenceFromUrl(urls.get(position) + "Titulo");
        filepathDescripcion =  storage.getReferenceFromUrl(urls.get(position) + "Descripcion");
        filepathPrecio =  storage.getReferenceFromUrl(urls.get(position) + "Precio");

        filepathTitulo.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @TargetApi(24)
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                Log.d("data123", "hola2");
                String titulo = new String(bytes, StandardCharsets.UTF_8);
                a.setTitulo(titulo);

                filepathDescripcion.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @TargetApi(24)
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        Log.d("data123", "hola3:");
                        String descripcion = new String(bytes, StandardCharsets.UTF_8);
                        a.setDescripcion(descripcion);

                        filepathPrecio.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @TargetApi(24)
                            public void onSuccess(byte[] bytes) {
                                // Use the bytes to display the image
                                Log.d("data123", "hola4");
                                String precio = new String(bytes, StandardCharsets.UTF_8);
                                a.setPrecio(precio);
                                a.setIma(paths[0],paths[1],paths[2],paths[3]);
                                listaAnuncios.add(a);

                                Log.d("data123","posicion"+position);

                                /*if(c[0]+1 == dataSnapshot.getChildrenCount()){
                                    Log.d("data123","holaaaaaaaaaaa");
                                    Adapter = new MyAdapter(list, context);
                                    rv.setAdapter(Adapter);
                                    progress.dismiss();
                                }*/

                                holder.titulo.setText(a.getTitulo());
                                holder.descripcion.setText(a.getDescripcion());
                                holder.precio.setText(a.getPrecio());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Error al descargar los anuncios", Toast.LENGTH_SHORT).show();
            }
        });

        Glide.with(context).using(new FirebaseImageLoader()).load(filepathFoto0).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.image);


        /*anun.setTitulo(listaAnuncios.get(position).getTitulo());
        anun.setDescripcion(listaAnuncios.get(position).getDescripcion());
        anun.setPrecio(listaAnuncios.get(position).getPrecio());
        anun.setIma(listaAnuncios.get(position).getIma(0),listaAnuncios.get(position).getIma(1),listaAnuncios.get(position).getIma(2),listaAnuncios.get(position).getIma(3));
        */
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return urls.size();
    }



}
