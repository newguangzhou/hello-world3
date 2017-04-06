package com.xiaomaoqiu.old.utils;

import com.loopj.android.http.ResponseHandlerInterface;
import com.xiaomaoqiu.old.utils.httpCache.AsyncHttpClient2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;

public class HttpUtil {
    private static     AsyncHttpClient2 client =new AsyncHttpClient2();    //实例话对象
    static
    {
        client.setTimeout(11000);   //设置链接超时，如果不设置，默认为10s
    }

    public static void get2(String httpApi,AsyncHttpResponseHandler res,Object... params)
    {
        client.get2(httpApi, res, params);
    }

    public static void get3(String httpApi,RequestParams requestParams, AsyncHttpResponseHandler res,Object... params)
    {
        client.get3(httpApi,requestParams, res, params);
    }

    public static void post2(String httpApi,HttpEntity entity, String contentType,AsyncHttpResponseHandler res,Object... params)
    {
        client.post2(httpApi, entity, contentType, res, params);
    }

    public static void uploadFile(String httpApi,String path, ResponseHandlerInterface responseHandler, Object... params)  throws Exception {
        client.uploadFile(httpApi,path,responseHandler,params);
    }

    //用一个完整url获取一个string对象
    public static void get(String urlString,AsyncHttpResponseHandler res)
    {
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,AsyncHttpResponseHandler res)   //url里面带参数
    {
        client.get(urlString, params,res);
    }
    public static void get(String urlString,JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
        client.get(urlString, params,res);
    }
    public static void get(String uString, BinaryHttpResponseHandler bHandler)   //下载数据使用，会返回byte数据
    {
        client.get(uString, bHandler);
    }
    public static AsyncHttpClient getClient()
    {
        return client;
    }
}