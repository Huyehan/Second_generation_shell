#include <jni.h>
#include <string>
#include <android/log.h>
#include <dlfcn.h>
#include "datadefine.h"

#define LOG_TAG "不落地加载"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

JNINativeMethod *dexfile;
// u4* args 数组参数
// JValue* pResult 返回值指针
typedef void (*openDexFile)(const u4* args,union JValue* pResult);
openDexFile g_openDexFile=NULL;

void* checkFunction(JNINativeMethod *table,const char *name,const char *sig);

void* checkFunction(JNINativeMethod *table,const char *name,const char *sig){
    int i=0;
    JNINativeMethod* method;
    do {
        method = table + (i++);
        if (strcmp(name, method->name) == 0 && strcmp(sig, method->signature) == 0) {
            break;
        }
    } while (method->name != NULL);
    LOGI("Found method pointer...");
    return method->fnPtr;
}

jint JNI_OnLoad(JavaVM *vm,void *reversed){
    JNIEnv env;
    // 加载so文件
    void *sofile=(void *)dlopen("libdvm.so",RTLD_LAZY);
    // 获取模块导出符号
    dexfile= (JNINativeMethod *)(dlsym(sofile, "dvm_dalvik_system_DexFile"));
    // 获取openDexFile函数指针
    g_openDexFile=(openDexFile)checkFunction(dexfile,"openDexFile","([B)I");
    if (g_openDexFile != NULL){
        LOGI("Found openDexFile method...");
    } else{
        LOGI("Not found openDexFile method...");
    }
    // 卸载so
    dlclose(sofile);
    if (vm->GetEnv((void **)&env,JNI_VERSION_1_6) != JNI_OK){
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_twoshell_ShellNativeMethod_loadDexFile(JNIEnv* env, jclass thiz,jbyteArray dex,jlong len){
    jbyte *bytes=env->GetByteArrayElements(dex,NULL);
    // 申请足够大的空间
    ArrayObject *object= (ArrayObject *)(malloc(sizeof(ArrayObject) + len));
    // 初始化字节数组长度
    object->length=len;
    // 拷贝字节数组的内容
    memcpy(object->contents,bytes,len);
    // 构造参数数组
    uint32_t args={*(uint32_t*)&object};
    // 构造返回值
    JValue jRet={0};
    // 调用openDexFile
    g_openDexFile(&args,&jRet);
    LOGI("cookie = %d",jRet.i);
    env->ReleaseByteArrayElements(dex,bytes,0);
    // 返回cookie
    return jRet.i;
}