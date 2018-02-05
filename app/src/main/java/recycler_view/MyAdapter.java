package recycler_view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mgppgg.tioney.AnunDatabase;
import mgppgg.tioney.BaseActivity;
import mgppgg.tioney.MainActivity;
import mgppgg.tioney.Publicar;
import mostrar_Anuncio.MostrarAnun;
import mgppgg.tioney.R;

/**
 * Created by pablich on 16/11/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static ArrayList<AnunDatabase> anunciosDatabase;
    private static ArrayList<String> descripciones;
    private Context context;
    private ProgressDialog dialog;
    private FirebaseStorage  storage;

    public MyAdapter(Context context,ProgressDialog d) {
        anunciosDatabase = new ArrayList<>();
        descripciones = new ArrayList<>();
        this.context = context;
        storage = FirebaseStorage.getInstance();
        dialog = d;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
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
                    int pos = getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), MostrarAnun.class);
                    intent.putExtra("Anuncio",anunciosDatabase.get(pos));
                    intent.putExtra("descripcion",descripciones.get(pos));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final StorageReference filepathDescripcion;

        if(anunciosDatabase.get(holder.getAdapterPosition()).getFotos()>0){
            final StorageReference filepathFoto0 = storage.getReferenceFromUrl(anunciosDatabase.get(holder.getAdapterPosition()).getUrl()+"Foto0");
            Glide.with(context).using(new FirebaseImageLoader()).load(filepathFoto0).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.image);
        }else holder.image.setImageResource(R.drawable.logo_fondonegro);

        descripciones.add("...");
        filepathDescripcion =  storage.getReferenceFromUrl(anunciosDatabase.get(holder.getAdapterPosition()).getUrl() + "Descripcion");

        filepathDescripcion.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @TargetApi(24)
            public void onSuccess(byte[] bytes) {
                String descripcion = new String(bytes, StandardCharsets.UTF_8);
                if(holder.getAdapterPosition()>=0){
                    descripciones.set(holder.getAdapterPosition(),descripcion);
                    holder.titulo.setText(anunciosDatabase.get(holder.getAdapterPosition()).getTitulo());
                    holder.descripcion.setText(descripcion);
                    holder.precio.setText(anunciosDatabase.get(holder.getAdapterPosition()).getPrecio());
                }

                if(holder.getAdapterPosition() >= anunciosDatabase.size()/3){
                    dialog.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Error al descargar los anuncios", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return anunciosDatabase.size();
    }

    public void limpiar(){
        anunciosDatabase.clear();
        descripciones.clear();
        this.notifyDataSetChanged();

    }

    public void actualizar(ArrayList<AnunDatabase> anuns){
        anunciosDatabase.addAll(anuns);
        this.notifyItemRangeInserted(anunciosDatabase.size()-anuns.size(),anuns.size());
        if(anunciosDatabase.size()==0)dialog.dismiss();
    }

    public int getUltimo(){
        return anunciosDatabase.size();
    }




}
