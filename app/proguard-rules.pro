#-libraryjars libs/baidumapapi_v3_5_0.jar
#-libraryjars libs/locSDK_5.3.jar
#-libraryjars libs/MiPush_SDK_Client_2_2_16.jar
#-keep class vi.com.gdi.bgl.android.**{*;}
#-keep class com.dhtvip.discEasy.broadcastReceiver.**{*;}

-keep class com.baidu.**{*;}
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class com.dhtvip.discEasy.broadcastReceiver.**{*;}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}