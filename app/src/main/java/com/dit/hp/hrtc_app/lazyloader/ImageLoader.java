package com.dit.hp.hrtc_app.lazyloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.dit.hp.hrtc_app.R;
import com.dit.hp.hrtc_app.utilities.CommonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private Context context;
    private boolean circleBitmap = false;
    private boolean squareBitmap = false;
    private boolean saveToGallery = false;

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<String, ImageData> imageViews = Collections
            .synchronizedMap(new WeakHashMap<String, ImageData>());
    ExecutorService executorService;

    //  Handler handler = new Handler();// handler to display images in UI thread
    android.os.Handler handler = new android.os.Handler();

    // TODO : change the launcher image later
    int stubId = R.mipmap.ic_launcher;

    public ImageLoader(Context context) {
        this.context = context;
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    public void DisplaySquareImage(String url, ImageView imageView,
                                   ProgressBar pb, TextView updateTv, boolean saveToGallery) {
        circleBitmap = false;
        squareBitmap = true;
        this.saveToGallery = saveToGallery;
        ImageData imgData = new ImageData(imageView, url, pb, updateTv, null);
        imageViews.put(url, imgData);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            if (pb != null) {
                pb.setVisibility(View.GONE);
            }
            if(updateTv!=null){
                updateTv.setVisibility(View.GONE);
            }
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.VISIBLE);
            queuePhoto(url, imageView, pb, updateTv, null);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void displaySquareImage(String url, ImageView imageView,
                                   ProgressBar pb, TextView updateTv, boolean saveToGallery, TextView tapTv) {
        circleBitmap = false;
        squareBitmap = true;
        this.saveToGallery = saveToGallery;
        ImageData imgData = new ImageData(imageView, url, pb, updateTv, tapTv);
        imageViews.put(url, imgData);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            if (pb != null) {
                pb.setVisibility(View.GONE);
            }
            if(updateTv!=null){
                updateTv.setVisibility(View.GONE);
            }
            if(tapTv!=null){
                tapTv.setVisibility(View.VISIBLE);
            }
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        } else {
            if(tapTv!=null){
                tapTv.setVisibility(View.INVISIBLE);
            }
            queuePhoto(url, imageView, pb, updateTv, tapTv);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void DisplayImage(String url, ImageView imageView, ProgressBar pb, TextView updateTv,
                             boolean saveToGallery) {
        squareBitmap = false;
        circleBitmap = false;
        this.saveToGallery = saveToGallery;
        ImageData imgData = new ImageData(imageView, url, pb, updateTv, null);
        imageViews.put(url, imgData);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            if (pb != null) {
                pb.setVisibility(View.GONE);
            }
            if(updateTv!=null){
                updateTv.setVisibility(View.GONE);
            }
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        } else {
            queuePhoto(url, imageView, pb, updateTv, null);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void DisplayCircleImage(String url, ImageView imageView,
                                   ProgressBar pb, TextView updateTv, boolean saveToGallery) {
        circleBitmap = true;
        this.saveToGallery = saveToGallery;
        Log.e("URL########",url);
        ImageData imgData = new ImageData(imageView, url, pb, updateTv, null);
        imageViews.put(url, imgData);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            if (pb != null) {
                pb.setVisibility(View.GONE);
            }
            if(updateTv!=null){
                updateTv.setVisibility(View.GONE);
            }
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        } else {
            queuePhoto(url, imageView, pb, updateTv, null);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private void queuePhoto(String url, ImageView imageView, ProgressBar pb, TextView upTv, TextView tapTv) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, pb, upTv, tapTv);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url, PhotoToLoad p) {
        File f = fileCache.getFile(url);

        // from SD cache
        Bitmap b = decodeFile(f, url,p);
        if (b != null)
            return b;

        // from web
        try {
            Bitmap bitmap = null;

            HttpURLConnection conn = getInputStreamConnection(url);
            InputStream is = conn.getInputStream();

            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            conn.disconnect();
            bitmap = decodeFile(f, url,p);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    private HttpURLConnection getInputStreamConnection(String url) {
        HttpURLConnection conn = null;
        try {
            URL imageUrl = new URL(url);
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
        } catch (Exception e) {

        }
        return conn;
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f, String url, final PhotoToLoad p) {
        try {
            Bitmap bitmap=null;
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);

            // else
            if (saveToGallery) {

                bitmap = createScaledBitmapFromStream(stream1,
                        CommonUtils.getScreenWidth(context),
                        CommonUtils.getScreenHeight(context));
            } else {
                BitmapFactory.decodeStream(stream1, null, o);
                final int REQUIRED_SIZE = 140;

                // Find the correct scale value. It should be the power of 2.
                int mScale = 1;
                while (o.outWidth / mScale / 2 >= REQUIRED_SIZE
                        && o.outHeight / mScale / 2 >= REQUIRED_SIZE)
                    mScale *= 2;

                // decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = mScale;
                FileInputStream stream2 = new FileInputStream(f);
                bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            }

            stream1.close();


            if (squareBitmap) {
                return bitmap;
            } else if (circleBitmap) {
                return CommonUtils.getCircleBitmap(bitmap, true);
            } else {
                return CommonUtils.getRoundCornerBitmap(bitmap);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public ProgressBar progressbar;
        public TextView updateTv;
        public TextView tapTv;

        public PhotoToLoad(String u, ImageView i, ProgressBar p, TextView upTv, TextView ttv) {
            url = u;
            imageView = i;
            progressbar = p;
            updateTv=upTv;
            tapTv=ttv;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad))
                    return;
                Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = (imageViews.get(photoToLoad.url)).getImageUrl();
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
                photoToLoad.imageView.setVisibility(View.VISIBLE);
                if (photoToLoad.progressbar != null)
                    photoToLoad.progressbar.setVisibility(View.GONE);
                if(photoToLoad.updateTv!=null)
                    photoToLoad.updateTv.setVisibility(View.GONE);
                if(photoToLoad.tapTv!=null)
                    photoToLoad.tapTv.setVisibility(View.VISIBLE);
            } else {
                photoToLoad.imageView.setImageResource(stubId);
                photoToLoad.imageView.setVisibility(View.VISIBLE);
                if (photoToLoad.progressbar != null)
                    photoToLoad.progressbar.setVisibility(View.GONE);
                if(photoToLoad.updateTv!=null)
                    photoToLoad.updateTv.setVisibility(View.GONE);
                if(photoToLoad.tapTv!=null)
                    photoToLoad.tapTv.setVisibility(View.VISIBLE);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    @SuppressWarnings("unused")
    private class ImageData {
        ImageView imageView;
        String imageUrl;
        ProgressBar progressbar;
        TextView updateTv;
        TextView tapTv;

        public ImageData(ImageView imgView, String url, ProgressBar pb, TextView tv, TextView tapTv) {
            this.imageView = imgView;
            this.imageUrl = url;
            this.progressbar = pb;
            this.updateTv=tv;
            this.tapTv=tapTv;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

    private Bitmap createScaledBitmapFromStream(InputStream s,
                                                int minimumDesiredBitmapWidth, int minimumDesiredBitmapHeight) {

        System.gc();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = s.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();

            final BitmapFactory.Options decodeBitmapOptions = new BitmapFactory.Options();
            // For further memory savings, you may want to consider using this
            // option
            // Uses 2-bytes instead of default 4 per pixel
            decodeBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

            if (minimumDesiredBitmapWidth > 0 && minimumDesiredBitmapHeight > 0) {
                final BitmapFactory.Options decodeBoundsOptions = new BitmapFactory.Options();
                decodeBoundsOptions.inJustDecodeBounds = true;
                InputStream is = new ByteArrayInputStream(baos.toByteArray());
                BitmapFactory.decodeStream(is, null, decodeBoundsOptions);

                final int originalWidth = decodeBoundsOptions.outWidth;
                final int originalHeight = decodeBoundsOptions.outHeight;

                // inSampleSize prefers multiples of 2, but we prefer to
                // prioritize memory savings
                decodeBitmapOptions.inSampleSize = Math.max(1, Math.min(
                        originalWidth / minimumDesiredBitmapWidth,
                        originalHeight / minimumDesiredBitmapHeight));

            }

            InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
            return BitmapFactory.decodeStream(is2, null, decodeBitmapOptions);

        } catch (IOException e) {
            throw new RuntimeException(e); // this shouldn't happen
        }

    }
}
