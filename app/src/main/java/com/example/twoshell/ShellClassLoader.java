package com.example.twoshell;

import android.content.Context;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class ShellClassLoader extends DexClassLoader {

    public static String TAG="不落地加载";
    public Context context;
    public ClassLoader classLoader;
    public int cookie;

    public ShellClassLoader(Context context, String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent, byte[] dexbyte) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        this.context=context;
        this.classLoader=parent;
        this.cookie=ShellNativeMethod.loadDexFile(dexbyte,dexbyte.length);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz=null;
        String[] classNameList=getClassNameList(this.cookie);
        for (String className : classNameList) {
            if (name.equals(className)){
                clazz=defineClass(className.replace('.','/'),this.context.getClassLoader(),this.cookie);
            }else {
                defineClass(className.replace('.','/'),this.context.getClassLoader(),this.cookie);
            }
        }
        if (clazz == null){
            clazz=super.findClass(name);
        }
        return clazz;
    }

    private Class defineClass(String name,ClassLoader cl,int cookie)
    {
        Class ca = (Class)RefinvokeMethod.invokeDeclaredMethod(DexFile.class.getName(),"defineClassNative",new Class[]{String.class,ClassLoader.class,int.class},new Object[]{name,cl,cookie});
        return ca;
    }

    private String[] getClassNameList(int cookie) {
        return (String[]) RefinvokeMethod.invokeDeclaredMethod(
                DexFile.class.getName(),
                "getClassNameList",
                new Class[]{int.class},
                new Object[]{cookie});
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return (Class<?>) RefinvokeMethod.invokeDeclaredMethod(
                "dalvik.system.DexFile",
                "defineClassNative",
                new Class[]{String.class,ClassLoader.class,int.class},
                new Object[]{name,this.classLoader,this.cookie});
    }
}
