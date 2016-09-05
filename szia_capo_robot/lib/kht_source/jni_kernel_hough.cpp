#include <jni.h>
#include <iterator>
#include "kht.h"
#include "jni_kernel_hough.h"

unsigned char* as_unsigned_char_array(JNIEnv * env, jbyteArray array) {
    int len = env->GetArrayLength (array);
    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

jobject as_line_list(JNIEnv * env,lines_list_t lines){
	jclass cls = env->FindClass("java/util/ArrayList");
	jmethodID constructor = env->GetMethodID(cls, "<init>", "()V");
    jobject list = env->NewObject(cls, constructor);
    jmethodID add = env->GetMethodID(cls, "add", "(Ljava/lang/Object;)Z");
    
    jclass line_cls = env->FindClass("pl/edu/agh/capo/common/Line");
    jmethodID line_constructor = env->GetMethodID(line_cls, "<init>", "(DD)V");
   	for (size_t k=0, end=lines.size(); k!=end; ++k){
		const line_t &line = lines[k];	
		jobject jline = env->NewObject(line_cls, line_constructor, line.theta, line.rho);	
		env->CallVoidMethod(list, add, jline);
	}
	return list;
}

JNIEXPORT jobject JNICALL Java_pl_edu_agh_capo_hough_jni_JniKernelHough_kht
  (JNIEnv * env, jobject method, jbyteArray binary_image, jlong image_width, jlong image_height, jlong cluster_min_size, jdouble cluster_min_deviation,
  jdouble delta, jdouble kernel_min_height, jdouble n_sigmas) {
  	
  	lines_list_t* lines = new lines_list_t();
  	kht((*lines), as_unsigned_char_array(env, binary_image), image_width, image_height, cluster_min_size, cluster_min_deviation, delta,
          kernel_min_height, n_sigmas);
    
    return as_line_list(env, (*lines));
}
