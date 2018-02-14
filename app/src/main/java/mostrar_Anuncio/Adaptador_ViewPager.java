package mostrar_Anuncio;

/**
 * Created by pablich on 02/12/2017.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import mgppgg.tioney.AnunDatabase;
import mgppgg.tioney.R;


public class Adaptador_ViewPager extends PagerAdapter {
    private Context Context;
    private AnunDatabase anun;
    private FirebaseStorage storage;


    Adaptador_ViewPager(Context context, AnunDatabase anun,FirebaseStorage sto) {
        this.Context = context;
        this.anun = anun;
        this.storage = sto;
    }

    @Override
    public int getCount() {
        if(anun.getFotos()>0) return anun.getFotos();
        else return 1;

    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView image = new ImageView(Context);
        image.setImageResource(R.drawable.logo);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        StorageReference filepathFoto = storage.getReferenceFromUrl(anun.getUrl()+"Foto"+i);
        if(anun.getFotos()>0)Glide.with(Context).using(new FirebaseImageLoader()).load(filepathFoto).diskCacheStrategy(DiskCacheStrategy.NONE).into(image);
        container.addView(image, 0);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        (container).removeView((ImageView) obj);
    }
}