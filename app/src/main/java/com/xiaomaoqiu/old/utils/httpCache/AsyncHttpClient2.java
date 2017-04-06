package com.xiaomaoqiu.old.utils.httpCache;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.xiaomaoqiu.old.config.Config;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;
import java.net.URI;

/**
 * Created by Administrator on 2015/6/23.
 * 实现一个支持cache的http模块
 */
public class AsyncHttpClient2 extends AsyncHttpClient {

    private static String TAG = "AsyncHttp2";

    public enum CacheMode
    {
        CACHE_NO(0),    //禁用缓存
        CACHE_MEM(1),   //内存缓存
        CACHE_FILE(2);  //文件缓存

        CacheMode(int iValue)
        {
            nValue = iValue;
        }

        public static CacheMode valueOf(int iValue)
        {
            switch(iValue)
            {
                case 1:return CACHE_MEM;
                case 2:return CACHE_FILE;
                default:return CACHE_NO;
            }
        }

        private int nValue;
    }

    //URL属性
    public static class UrlAttr
    {
        public CacheMode cacheMode; //缓存模式
        public int nCacheExpireTime; //过期时间,以S为单位
    }

    private HttpCache mFileCache = HttpCache.createFileCache(Config.getHttpCacheDir());
    private HttpCache mMemoryCache = HttpCache.createMemoryCache();


    public class AsyncHttpResponseHandler2 extends AsyncHttpResponseHandler
    {
        private String  m_url;
        private UrlAttr m_urlAttr;

        private AsyncHttpResponseHandler m_oriRespHandler;

        public AsyncHttpResponseHandler2(String strUrl,UrlAttr urlAttr,AsyncHttpResponseHandler oriRespHandler)
        {
            super();
            m_url = strUrl;
            m_urlAttr = urlAttr;
            m_oriRespHandler = oriRespHandler;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            if(m_urlAttr.cacheMode == CacheMode.CACHE_MEM)
            {
                mMemoryCache.put(m_url, new String(responseBody), m_urlAttr.nCacheExpireTime);
            }else if(m_urlAttr.cacheMode == CacheMode.CACHE_FILE)
            {
                mFileCache.put(m_url,new String(responseBody), m_urlAttr.nCacheExpireTime);
            }
            m_oriRespHandler.onSuccess(statusCode,headers,responseBody);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            m_oriRespHandler.onFailure(statusCode,headers,responseBody,error);
        }

        @Override
        public URI getRequestURI() {
            return m_oriRespHandler.getRequestURI();
        }

        @Override
        public Header[] getRequestHeaders() {
            return m_oriRespHandler.getRequestHeaders();
        }

        @Override
        public void setRequestURI(URI requestURI) {
            m_oriRespHandler.setRequestURI(requestURI);
        }

        @Override
        public void setRequestHeaders(Header[] requestHeaders) {
            m_oriRespHandler.setRequestHeaders(requestHeaders);
        }

        /**
         * Sets the charset for the response string. If not set, the default is UTF-8.
         *
         * @param charset to be used for the response string.
         * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Charset</a>
         */
        public void setCharset(final String charset) {
            m_oriRespHandler.setCharset(charset);
        }

        public String getCharset() {
            return m_oriRespHandler.getCharset();
        }

        /**
         * Fired when the requestShopActivities progress, override to handle in your own code
         *
         * @param bytesWritten offset from start of file
         * @param totalSize    total size of file
         */
        public void onProgress(long bytesWritten, long totalSize) {
            m_oriRespHandler.onProgress(bytesWritten,totalSize);
        }

        /**
         * Fired when the requestShopActivities is started, override to handle in your own code
         */
        public void onStart() {
            m_oriRespHandler.onStart();
        }

        /**
         * Fired in all cases when the requestShopActivities is finished, after both success and failure, override to
         * handle in your own code
         */
        public void onFinish() {
            m_oriRespHandler.onFinish();
        }


        /**
         * Fired when a retry occurs, override to handle in your own code
         *
         * @param retryNo number of retry
         */
        public void onRetry(int retryNo) {
            m_oriRespHandler.onRetry(retryNo);
        }

        public void onCancel() {
            m_oriRespHandler.onCancel();
        }

        public void onUserException(Throwable error) {
            m_oriRespHandler.onUserException(error);
        }
    }

    public void get2(String httpApi, ResponseHandlerInterface responseHandler, Object... params) {
        String strUrl = Config.buildHttpApiUrl(httpApi, params);
        UrlAttr urlAttr = Config.getHttpApiAttr(httpApi);

        Log.v("http",strUrl);

        String result = null;
        if (urlAttr.cacheMode == CacheMode.CACHE_MEM) {
            result = mMemoryCache.get(strUrl);
        }else if (urlAttr.cacheMode == CacheMode.CACHE_FILE){
            result = mFileCache.get(strUrl);
        }

        if (result != null) {
            responseHandler.sendStartMessage();
            responseHandler.sendFinishMessage();
            responseHandler.sendSuccessMessage(200, null, result.getBytes());
        }else {
            AsyncHttpResponseHandler httpResponseHandler = (AsyncHttpResponseHandler)responseHandler;
            if(httpResponseHandler != null && urlAttr.cacheMode !=CacheMode.CACHE_NO)
            {
                AsyncHttpResponseHandler2 responseHandler2 = new AsyncHttpResponseHandler2(strUrl,urlAttr,httpResponseHandler);
                get(null, strUrl, null, responseHandler2);
            }else
            {
                Log.w(TAG, "responseHandler is not an AsyncHttpResponseHandler instance or cache is disabled");
                get(null,strUrl,null,responseHandler);
            }
        }
    }

    public void get3(String httpApi,RequestParams reqParams, ResponseHandlerInterface responseHandler, Object... params) {
        String strUrl = Config.buildHttpApiUrl(httpApi, params);
        UrlAttr urlAttr = Config.getHttpApiAttr(httpApi);

        Log.v("http",strUrl);

        String result = null;
        if (urlAttr.cacheMode == CacheMode.CACHE_MEM) {
            result = mMemoryCache.get(strUrl);
        }else if (urlAttr.cacheMode == CacheMode.CACHE_FILE){
            result = mFileCache.get(strUrl);
        }

        if (result != null) {
            responseHandler.sendStartMessage();
            responseHandler.sendFinishMessage();
            responseHandler.sendSuccessMessage(200, null, result.getBytes());
        }else {
            AsyncHttpResponseHandler httpResponseHandler = (AsyncHttpResponseHandler)responseHandler;
            if(httpResponseHandler != null && urlAttr.cacheMode !=CacheMode.CACHE_NO)
            {
                AsyncHttpResponseHandler2 responseHandler2 = new AsyncHttpResponseHandler2(strUrl,urlAttr,httpResponseHandler);
                get(null, strUrl, reqParams, responseHandler2);
            }else
            {
                Log.w(TAG, "responseHandler is not an AsyncHttpResponseHandler instance or cache is disabled");
                get(null,strUrl,reqParams,responseHandler);
            }
        }
    }

    public void post2(String httpApi, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler, Object... params) {
        String strUrl = Config.buildHttpApiUrl(httpApi,params);
        UrlAttr urlAttr = Config.getHttpApiAttr(httpApi);

        Log.v("http",strUrl);

        String result = null;
        if (urlAttr.cacheMode == CacheMode.CACHE_MEM) {
            result = mMemoryCache.get(strUrl);
        }else if (urlAttr.cacheMode == CacheMode.CACHE_FILE) {
            result = mFileCache.get(strUrl);
        }

        if (result != null) {
            responseHandler.sendStartMessage();
            responseHandler.sendFinishMessage();
            responseHandler.sendSuccessMessage(200, null, result.getBytes());
        }else {
            AsyncHttpResponseHandler httpResponseHandler = (AsyncHttpResponseHandler)responseHandler;
            if(httpResponseHandler != null && urlAttr.cacheMode !=CacheMode.CACHE_NO)
            {
                AsyncHttpResponseHandler2 responseHandler2 = new AsyncHttpResponseHandler2(strUrl,urlAttr,httpResponseHandler);
                post(null, strUrl, entity, contentType, responseHandler2);
            }else
            {
                Log.w(TAG, "responseHandler is not an AsyncHttpResponseHandler instance or cache is disabled");
                post(null, strUrl, entity, contentType, responseHandler);
            }
        }
    }

    // 上传文件
    public void uploadFile(String httpApi,String path, ResponseHandlerInterface responseHandler, Object... params)  throws Exception{
        String strUrl = Config.buildHttpApiUrl(httpApi, params);

        Log.v("http",strUrl);

        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            RequestParams reqParams = new RequestParams();
            reqParams.put("uploadfile", file);

            post(strUrl, reqParams, responseHandler);
        }
    }
}
