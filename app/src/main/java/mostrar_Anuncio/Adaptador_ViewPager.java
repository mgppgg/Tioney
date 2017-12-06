package mostrar_Anuncio;

/**
 * Created by pablich on 02/12/2017.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import recycler_view.Anuncio;

public class Adaptador_ViewPager extends PagerAdapter {
    Context Context;
    Anuncio anun;
    FirebaseStorage storage;
    ImageView imagenes[];


    Adaptador_ViewPager(Context context, Anuncio anun,FirebaseStorage sto) {
        this.Context = context;
        this.anun = anun;
        this.storage = sto;
        this.imagenes =new ImageView[4];
    }

    @Override
    public int getCount() {
        int cont=0;
        for(int b =0;b<4;b++){
            if(anun.getIma(b)!=null)cont++;
        }

        return cont;
    }



    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView image = new ImageView(Context);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        StorageReference filepathFoto = storage.getReferenceFromUrl(anun.getIma(i));
        Glide.with(Context).using(new FirebaseImageLoader()).load(filepathFoto).diskCacheStrategy(DiskCacheStrategy.NONE).into(image);
        container.addView(image, 0);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        (container).removeView((ImageView) obj);
    }
}