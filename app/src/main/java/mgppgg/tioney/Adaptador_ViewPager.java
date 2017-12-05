package mgppgg.tioney;

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


    Adaptador_ViewPager(Context context, Anuncio anun,FirebaseStorage sto) {
        this.Context = context;
        this.anun = anun;
        this.storage = sto;
    }

    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

    private StorageReference[] sliderImagesId = new StorageReference[]{
           /* R.drawable.image1, R.drawable.image2, R.drawable.cat,
            R.drawable.image1, R.drawable.image2, R.drawable.cat,*/
           //anun.getIma(0),anun.getIma(1),anun.getIma(2),anun.getIma(3),
    };

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView image = new ImageView(Context);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
       // mImageView.setImageResource(sliderImagesId[i]);*/
        StorageReference filepathFoto = storage.getReferenceFromUrl(anun.getIma(0));
        Glide.with(Context).using(new FirebaseImageLoader()).load(filepathFoto).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(image);
        container.addView(image, 0);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        (container).removeView((ImageView) obj);
    }
}