package recycler_view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import mgppgg.tioney.MostrarAnun;
import mgppgg.tioney.Publicar;
import mgppgg.tioney.R;

/**
 * Created by pablich on 16/11/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static List<Anuncio> listaAnuncios;
    private Context context;
    private static Anuncio anun = new Anuncio();
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
    public MyAdapter(List<Anuncio> list, Context context) {
        this.listaAnuncios =  list;
        this.context = context;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        StorageReference filepathFoto0 = storage.getReferenceFromUrl(listaAnuncios.get(position).getIma(0));

        holder.titulo.setText(listaAnuncios.get(position).getTitulo());
        holder.descripcion.setText(listaAnuncios.get(position).getDescripcion());
        holder.precio.setText(listaAnuncios.get(position).getPrecio());
        Glide.with(context).using(new FirebaseImageLoader()).load(filepathFoto0).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(holder.image);

        anun.setTitulo(listaAnuncios.get(position).getTitulo());
        anun.setDescripcion(listaAnuncios.get(position).getDescripcion());
        anun.setPrecio(listaAnuncios.get(position).getPrecio());
        anun.setIma(listaAnuncios.get(position).getIma(0),listaAnuncios.get(position).getIma(1),listaAnuncios.get(position).getIma(2),listaAnuncios.get(position).getIma(3));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listaAnuncios.size();
    }



}
