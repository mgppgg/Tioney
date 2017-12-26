package mostrar_Anuncio;

/**
 * Created by pablich on 02/12/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

import recycler_view.Anuncio;

import static android.R.attr.width;
import static com.firebase.ui.storage.R.attr.height;

public class Adaptador_ViewPager extends PagerAdapter {
    private Context Context;
    private Anuncio anun;
    private FirebaseStorage storage;


    Adaptador_ViewPager(Context context, Anuncio anun,FirebaseStorage sto) {
        this.Context = context;
        this.anun = anun;
        this.storage = sto;
    }

    @Override
    public int getCount() {
        return 2;

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