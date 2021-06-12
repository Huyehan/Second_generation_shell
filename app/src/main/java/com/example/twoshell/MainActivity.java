package com.example.twoshell;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static String TAG="不落地加载";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        byte[] dex = readDex();
        Log.i(TAG,"dex文件加载完成");
        Object activityThreadObject=RefinvokeMethod.invokeStaticMethod(
                "android.app.ActivityThread",
                "currentActivityThread",
                new Class[]{},new Object[]{}
        );
        String packageName=getPackageName();
        ArrayMap packages= (ArrayMap) RefinvokeMethod.getField(
                "android.app.ActivityThread",
                activityThreadObject, "mPackages");
        WeakReference weakReference= (WeakReference) packages.get(packageName);
        ClassLoader oldClassLoader= (ClassLoader) RefinvokeMethod.getField(
                "android.app.LoadedApk",
                weakReference.get(),"mClassLoader");
        ShellClassLoader shellClassLoader=new ShellClassLoader(
                getApplicationContext(),
                getPackageCodePath(),
                getCacheDir().toString(),
                null,oldClassLoader,dex
        );
        RefinvokeMethod.setField(
                "android.app.LoadedApk",
                "mClassLoader",
                weakReference.get(),
                shellClassLoader);
        Class<?> clazz = null;
        try {
            clazz = shellClassLoader.loadClass("com.example.sourceapk.MainActivity");
            Log.i(TAG,"MainActivity加载完毕");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(getApplicationContext(),clazz));
        finish();
    }

    public byte[] readDex(){
        ByteArrayOutputStream baos=null;
        try {
            InputStream is = getAssets().open("classes.dex");
            baos=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int len=0;
            while ((len=is.read(buffer)) != -1){
                baos.write(buffer,0,len);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}