package recycler_view;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
    private static ArrayList<Anuncio> listaAnuncios;
    private ArrayList<AnunDatabase> anunciosDatabase;
    private Context context;
    private ProgressDialog dialog;
    private static Anuncio anun;
    private FirebaseStorage  storage;

    public MyAdapter(ArrayList<AnunDatabase> url, Context context,ProgressDialog d) {
        this.anunciosDatabase = new ArrayList<>();
        listaAnuncios = new ArrayList<>();
        this.context = context;
        anun = new Anuncio();
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
                    anun = listaAnuncios.get(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), MostrarAnun.class);
                    intent.putExtra("Anuncio", anun);
                    v.getContext().startActivity(intent);

                }
            });
        }
    }

    /*private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.PBcargar_mas);
        }
    }*/


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Anuncio a = new Anuncio();
        final StorageReference filepathDescripcion;

        if(anunciosDatabase.get(position).getFotos()>0){
            final StorageReference filepathFoto0 = storage.getReferenceFromUrl(anunciosDatabase.get(position).getUrl()+"Foto0");
            Glide.with(context).using(new FirebaseImageLoader()).load(filepathFoto0).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(holder.image);
        }

        listaAnuncios.add(a);
        filepathDescripcion =  storage.getReferenceFromUrl(anunciosDatabase.get(position).getUrl() + "Descripcion");

        filepathDescripcion.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @TargetApi(24)
            public void onSuccess(byte[] bytes) {
                String descripcion = new String(bytes, StandardCharsets.UTF_8);
                listaAnuncios.get(position).setDescripcion(descripcion);
                listaAnuncios.get(position).setTitulo(anunciosDatabase.get(position).getTitulo());
                listaAnuncios.get(position).setPrecio(anunciosDatabase.get(position).getPrecio());
                listaAnuncios.get(position).setUID(anunciosDatabase.get(position).getUID());
                listaAnuncios.get(position).setUsuario(anunciosDatabase.get(position).getUsuario());
                listaAnuncios.get(position).setUrl(anunciosDatabase.get(position).getUrl());
                listaAnuncios.get(position).setCategoria(anunciosDatabase.get(position).getCategoria());
                listaAnuncios.get(position).setFotos(anunciosDatabase.get(position).getFotos());
                listaAnuncios.get(position).setLongitud(anunciosDatabase.get(position).getLongitud());
                listaAnuncios.get(position).setLatitud(anunciosDatabase.get(position).getLatitud());

                holder.titulo.setText(a.getTitulo());
                holder.descripcion.setText(a.getDescripcion());
                holder.precio.setText(a.getPrecio());

                if(position > anunciosDatabase.size()/2)dialog.dismiss();

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
        //if(anunciosDatabase.size()==0)dialog.dismiss();
        return anunciosDatabase.size();
    }

    public void limpiar(){
        //Log.d("wwwwwww","anuns:"+anuns.size());
        Log.d("wwwwwww","anuns:"+anunciosDatabase.size());
        anunciosDatabase.clear();
        listaAnuncios.clear();
        this.notifyDataSetChanged();
        Log.d("wwwwwww","anuns:"+anunciosDatabase.size());

    }

    public void actualizar(ArrayList<AnunDatabase> anuns){
        anunciosDatabase.addAll(anuns);
        if(anunciosDatabase.size()==0) this.notifyItemRangeInserted(0,anuns.size());
        else this.notifyItemRangeInserted(anunciosDatabase.size()-1,anuns.size());
    }




}
