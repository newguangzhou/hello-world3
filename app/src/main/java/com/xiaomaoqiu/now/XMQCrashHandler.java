package com.xiaomaoqiu.now;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomaoqiu.now.bussiness.SplashActivity;
import com.xiaomaoqiu.now.util.SPUtil;
import com.xiaomaoqiu.now.util.ToastUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

/**
 * 对应用全局未捕获异常的处理核心类，在这里实现优雅的关闭以及应用程序的重启，获得良好的用户体验！
 * 还可以收集用户的使用版本信息、机型信息等
 *
 * @author long
 */
@SuppressLint("WrongConstant")
public class XMQCrashHandler implements UncaughtExceptionHandler {
    private Context context;
    private static XMQCrashHandler handler;
    private static final long TIME_CRASHNOTREOPEN = 10000;//crash之后不重启的时间。
    private static final long QUICK_CRASH_ELAPSE = 10 * 1000;
    public static final int MAX_CRASH_COUNT = 3;

    /**
     * 构造方法私有化
     */
    private XMQCrashHandler() {
    }

    /**
     * 此处不会出现在多线程场景下；
     * 因此不需要考虑多线程场景下多个对象的问题。
     *
     * @return
     */
    public static XMQCrashHandler getMyCrashHandler() {
        if (handler == null) {
            handler = new XMQCrashHandler();
        }
        return handler;
    }

    public void init(Context context) {
        this.context = context;
    }

    /**
     * 程序出错的话调用方法！ 实现将用户信息，堆栈错误信息记录；并且实现应用程序的重启！
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

//        LogUtil.i("lz", "uncaughtException");

        try {
            String errorMsg = getCrashInfo(ex);
//            LogUtil.i("lz", "CrashInfo:" + errorMsg);
            long crashtime = System.currentTimeMillis();
//            LogUtil.e("lz", "crashTime" + crashtime + "::" + "LastCrashTime:" + SPUtil.getCrashTime());
            if (crashtime - SPUtil.getCrashTime() > TIME_CRASHNOTREOPEN) {
//                LogUtil.e("lz", "超过十秒");
                // 两次崩溃时间超过限制才去重启。十秒之内的崩溃第二次连续崩溃，不重启。
//                if (CrashManager.ifRestart(errorMsg)) {
                    // 根据CrashManager的判断来决定重启与否。
//                    LogUtil.e("lz", "要重启");
                CrashReport.postCatchedException(new Exception(errorMsg));
                    Intent intent = new Intent(context, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//这一行不能去掉。
                    context.startActivity(intent);
//                }
            }

//            SPUtil.putCrashTime(crashtime);
//
//            UMengUtil.onKillProcess(context);
            // 最后完成自杀的操作
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            e.printStackTrace();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    /**
     * 获取Crash的信息；方便定位、后期统计。
     * @param ex
     * @return
     * @throws Exception
     */

    public String getCrashInfo(Throwable ex) throws Exception {
        StringBuilder sb = new StringBuilder();
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        // 获取错误的堆栈信息:先获取堆栈信息，然后获取手机内存信息
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        String string = writer.toString();

        sb.append(string);
        packageInfo = pm.getPackageInfo(context.getPackageName(),
                PackageManager.GET_UNINSTALLED_PACKAGES
                        | PackageManager.GET_ACTIVITIES);
        sb.append("VersionCode = " + packageInfo.versionName);
        sb.append("\n");
        // 然后获取用户的手机硬件信息
        Field[] fields = Build.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String name = fields[i].getName();
            sb.append(name + " = ");
            String value = fields[i].get(null).toString();
            sb.append(value);
            sb.append("\n");
        }
        sb.append("Channel="
                +("myTid=" + android.os.Process.myTid())+("\n")
                +("myPid=" + android.os.Process.myPid())+("\n")
                +("myUid=" + android.os.Process.myUid())+("\n")
                +("ThreadId" + Thread.currentThread().getId())+("\n"));
        String errorMsg = sb.toString();
        return errorMsg;
    }




}
