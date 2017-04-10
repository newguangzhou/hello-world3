package com.xiaomaoqiu.old.config;

import android.content.Context;
import android.util.Log;

import com.xiaomaoqiu.old.utils.httpCache.AsyncHttpClient2;
import com.xiaomaoqiu.pet.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Config {
    private static String TAG = "Config";
    //配置表参见res/raw/config.xml
    private static Element configPri;//主配置
    private static Element configAlt;//从配置
    private static String httpCache;

    /*加载xml文件
    app Context对象
    node res下的xml相对路径如 res中资源res.xml.xxx.xml,其xmlUri=xxx.xml
    */
    private static Element LoadXml( Context app, int resId){
        try {
            //得到 DocumentBuilderFactory 对象, 由该对象可以得到 DocumentBuilder 对象
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            //得到DocumentBuilder对象
            DocumentBuilder builder=factory.newDocumentBuilder();
            //得到代表整个xml的Document对象

            InputStream is = app.getResources().openRawResource( resId );
            Document document=builder.parse( is );
            //得到 "根节点"
            return document.getDocumentElement();
        } catch (ParserConfigurationException e) {
            Log.v(TAG, "xml parser failed" + e.getMessage());
        } catch (SAXException e) {
            Log.v(TAG, "xml parser failed" + e.getMessage());
        } catch (IOException e) {
            Log.v(TAG, "xml parser failed" + e.getMessage());
        }
        return null;
    }

    /*获取一个XML结点的子节点
    node xml节点
    strNodePath 以'/'分隔的节点路径
    */
    private static Element getChildElementByPath(Element node,String strNodePath)
    {
        String[] path = strNodePath.split("/");
        Element dir = node;
        for( int i = 0; i < path.length; ++i ){
            NodeList items = dir.getElementsByTagName( path[i] );
            if( items.getLength() == 0 ){
                return null;
            }
            dir = (Element) items.item(0);
        }
        return dir;
    }


    public static void init( Context context ){
        parseConfig( context );
    }

    private  static void parseConfig( Context context ){
        Element root= LoadXml(context, R.raw.config);

        String strPri = root.getAttribute("configPri");
        String strAlt = root.getAttribute("configAlt");

        //获取根节点的所有config的节点config.xml
        NodeList items=root.getElementsByTagName("config");

        //查找configPri and configAlt
        for(int i=0;i<items.getLength();i++)
        {
            Element item=(Element)items.item(i);
            String configName = item.getAttribute("name");
            if( configName == strAlt ){
                configAlt = item;
            }
            else if( configName == strPri){
                configPri = item;
            }
        }
        return;
    }

    static private Element getConfigElement(String nodePath)
    {
        Element cfg = getChildElementByPath(configPri, nodePath);
        if(cfg == null)
            cfg = getChildElementByPath(configAlt,nodePath);
        return cfg;
    }

    static private String getKeyValue( String nodePath, String valueName ){
        Element cfg = getConfigElement(nodePath);
        if(cfg == null) return null;
        return cfg.getAttribute(valueName);
    }


    public static AsyncHttpClient2.UrlAttr getHttpApiAttr(String httpApi)
    {
        AsyncHttpClient2.UrlAttr urlAttr = new AsyncHttpClient2.UrlAttr();
        Element cfg = getConfigElement("HttpApi/" + httpApi);
        if(cfg == null)
        {
            Log.w(TAG, "getUrlAttr for " + httpApi + " failed");
            urlAttr.cacheMode = AsyncHttpClient2.CacheMode.CACHE_NO;
            urlAttr.nCacheExpireTime=0;
        }else {
            String strCache = cfg.getAttribute("cache_mode");
            if(strCache.isEmpty()) strCache ="0";
            urlAttr.cacheMode = AsyncHttpClient2.CacheMode.valueOf(Integer.parseInt(strCache));
            String strExpire = cfg.getAttribute("expire_time");
            if(strExpire.isEmpty()) strExpire ="0";
            urlAttr.nCacheExpireTime = Integer.parseInt(strExpire);
        }
        return urlAttr;
    }


    //通过urlkey获得配置文件配置的url，根据params格式化配置文件的url得到完整的url
    public static String buildHttpApiUrl(String httpApi, Object... params)
    {
        Element cfg = getConfigElement("HttpApi/" + httpApi);
        assert(cfg != null);

        String url = cfg.getAttribute("url");

        if (params != null && params.length > 0){
            url = String.format(url, params);
        }
        return url;
    }



    public static String getConfigTag()
    {
        return getPathText("tag");
    }

    public static String getMiPushAppID()
    {
        return getPathText("MiPush/APPID");
    }

    public static String getMiPushAppKey()
    {
        return getPathText("MiPush/APPKEY");
    }

    public static String getHttpCacheDir() {
        if (httpCache == null) {
            httpCache = getPathText("Http/CacheDir");
        }
        return httpCache;
    }


    protected static String getPathText(String strPath)
    {
        String ret = "";
        Element ele = getConfigElement(strPath);
        if(ele != null)
        {
            ret = ele.getTextContent();
        }
        return ret;
    }
}
