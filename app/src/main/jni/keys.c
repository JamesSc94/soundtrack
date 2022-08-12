#include <jni.h>
JNIEXPORT jstring JNICALL
Java_com_jamessc94_soundtrack_network_Network_getAPIKey(JNIEnv *env, jobject instance) {
    return (*env)->  NewStringUTF(env, "21e474de325c30f96b3f68d339661f52");
}

JNIEXPORT jstring JNICALL
Java_com_jamessc94_soundtrack_network_Network_getGoogleKey(JNIEnv *env, jobject instance) {
    return (*env)->  NewStringUTF(env, "AIzaSyBNO-q541wuzR6wGaJsUGOfBsktABzM-_w");
}

JNIEXPORT jstring JNICALL
Java_com_jamessc94_soundtrack_network_Network_getNapsterKey(JNIEnv *env, jobject instance) {
    return (*env)->  NewStringUTF(env, "YzVhN2FiZGYtMDNkYS00ZjY0LWFkMzItMWJhMzQyM2VmMzM4");
}