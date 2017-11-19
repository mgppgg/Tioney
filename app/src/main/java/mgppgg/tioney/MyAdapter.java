package mgppgg.tioney;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by pablich on 16/11/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Anuncio> listaAnuncios;
    private StorageReference storageRef;
    private StorageReference filepathTitulo;
    private StorageReference filepathTFoto0;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public TextView titulo;
        public ViewHolder(View v) {
            super(v);
            image = (ImageView)v.findViewById(R.id.IVfoto);
            titulo = (TextView)v.findViewById(R.id.TVtitulo);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Anuncio> list, Context context) {
        this.listaAnuncios =  list;
        this.context = context;
        storageRef = FirebaseStorage.getInstance().getReference();
        filepathTitulo = storageRef.child("Anuncios/" + "Titulo");
        filepathTFoto0 = storageRef.child("Anuncios/" + "Foto0");
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
        Anuncio anuncio = listaAnuncios.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        Glide.with(context).using(new FirebaseImageLoader()).load(filepathTFoto0).into(holder.image);



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return listaAnuncios.size();
    }
}
