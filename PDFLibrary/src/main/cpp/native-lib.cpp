#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_wind_niveales_com_mupdf_1port_MainAct_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
