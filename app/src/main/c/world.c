#include <jni.h>

JNIEXPORT jstring JNICALL Java_com_frio_neptune_world_WorldJNIBridge_load_world(JNIEnv* env, jobject /* this */) {
    // TODO
    return (*env)->NewStringUTF(env, "WorldJNIBridge load_world");
}