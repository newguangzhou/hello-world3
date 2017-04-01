package com.xiaomaoqiu.pet.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.xiaomaoqiu.pet.utils.httpCache.AsyncHttpClient2;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步加截图片类
 *
 * @author Hai Jiang
 * @Email 672335219@qq.ciom
 * @Data 2014-2-26
 */
public enum AsyncImageTask {
    INSTANCE;
    //开线程池  
    ExecutorService executorService = Executors.newCachedThreadPool();
    //缓存图片 把图片的软引用放到map中  
    private Map<String, SoftReference<Bitmap>> imageMap = new HashMap<String, SoftReference<Bitmap>>();

    //ID为标记,标记哪条记录image . 这个ID来自于自定义adapter的getView()方法中其中一个参数position  
    public Bitmap loadImage(final ImageView view, final String imageUrl,
                              final ImageCallback callback) {

        if (view == null)
            return null;

        if (null == imageUrl || imageUrl.isEmpty())
            return null;

        //先看缓存(Map)中是否存在  
        if (imageMap.containsKey(imageUrl)) {
            SoftReference<Bitmap> softReference = imageMap.get(imageUrl);
            if (softReference != null) {
                Bitmap drawable = softReference.get();
                if (drawable != null) {
                    callback.imageLoaded(imageUrl,drawable, view);
                    return drawable;
                }
            }
        }

        //主线程更新图片  
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                callback.imageLoaded(imageUrl,(Bitmap) message.obj, view);
            }
        };

        AsyncHttpClient2 client2 = new AsyncHttpClient2();
        client2.get(imageUrl, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    Bitmap drawable =  BitmapFactory.decodeByteArray(responseBody, 0 , responseBody.length);
                    imageMap.put(imageUrl, new SoftReference<Bitmap>(drawable));
                    Message message = handler.obtainMessage(0, drawable);
                    handler.sendMessage(message);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("image.load.error", " url :" + imageUrl + "   http error :" + error.getMessage());
            }
        });

//        //加载图片的线程
//        executorService.submit(
//                //加载图片的线程
//                new Thread() {
//                    public void run() {
//                        //加载图片
//                        Bitmap drawable = AsyncImageTask.loadImageByUrl(imageUrl);
//                        //加入缓存集合中 注意  这里就要把得到的图片变成软引用放到map中了
//                        imageMap.put(imageUrl, new SoftReference<Bitmap>(drawable));
//                        //通知消息主线程更新UI  . 这里就是是否能异步刷新的留意点.
//                        Message message = handler.obtainMessage(0, drawable);
//                        handler.sendMessage(message);
//                    }
//                });
        return null;
        //到这里就获取图片的静态方法就完了
}

    //根据图片地址加载图片，并保存为Drawable  
    //这里不用说了吧.都是一些基本的.从API从可以看  
    public static Bitmap loadImageByUrl(final String imageUrl) {
        URL url;
        InputStream inputStream = null;
        try {
            url = new URL(imageUrl);
            inputStream = (InputStream) url.getContent();
            //String rep_msg = inputStream.toString();
            //Log.i("image.http.log", rep_msg);
            Bitmap bitmap =  BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //利用接口回调，更新图片UI    
    public interface ImageCallback {
        void imageLoaded(String url,Bitmap obj, ImageView view);
    }

}
