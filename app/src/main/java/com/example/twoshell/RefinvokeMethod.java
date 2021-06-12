package com.example.twoshell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RefinvokeMethod {
    public static Object invokeStaticMethod(String class_name,String method_name,Class[] classes,Object[] objects){
        try {
            Class aClass = Class.forName(class_name);
            Method method = aClass.getMethod(method_name, classes);
            return method.invoke(null,objects);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeMethod(String class_name,String method_name,Object obj,Class[] classes,Object[] objects){
        try {
            Class aClass = Class.forName(class_name);
            Method method = aClass.getMethod(method_name, classes);
            return method.invoke(obj,objects);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object invokeDeclaredMethod(String class_name,String method_name,Class[] parameterTypes,Object[] parameterValues)
    {
        try {
            Class class_obj = Class.forName(class_name);
            Method method = class_obj.getDeclaredMethod(method_name,parameterTypes);
            method.setAccessible(true);
            return method.invoke(null,parameterValues);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getField(String class_name,Object obj,String field_name){
        try {
            Class aClass = Class.forName(class_name);
            Field field = aClass.getDeclaredField(field_name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getStaticField(String class_name,String field_name){
        try {
            Class aClass = Class.forName(class_name);
            Field field = aClass.getDeclaredField(field_name);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setField(String class_name,String field_name,Object obj,Object value){
        try {
            Class aClass = Class.forName(class_name);
            Field field = aClass.getDeclaredField(field_name);
            field.setAccessible(true);
            field.set(obj,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStaticField(String class_name,String field_name,Object value){
        try {
            Class aClass = Class.forName(class_name);
            Field field = aClass.getDeclaredField(field_name);
            field.setAccessible(true);
            field.set(null,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
