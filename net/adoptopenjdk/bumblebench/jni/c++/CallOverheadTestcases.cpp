/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
#include <jni.h>
#include <assert.h>

#include "net_adoptopenjdk_bumblebench_jni_CallOverheadTestcases.h"
#include "Portal.h"

void Java_net_adoptopenjdk_bumblebench_jni_CallOverheadTestcases_testNoParamNoRet(
		JNIEnv * env, jobject jobj) {
	return;
}

void Java_net_adoptopenjdk_bumblebench_jni_CallOverheadTestcases_testParamNoRet(
		JNIEnv* env, jobject jobj, jlong val) {
	val += 22;

	return;
}

jdouble Java_net_adoptopenjdk_bumblebench_jni_CallOverheadTestcases_testParamAndRet(
		JNIEnv* env, jobject jobj, jlong jl, jint ji, jboolean jbool, jbyte jb,
		jchar jc, jshort js, jfloat jf1, jfloat jf2, jfloat jf3, jfloat jf4,
		jfloat jf5, jdouble jd1, jdouble jd2, jdouble jd3) {

	ji += 2;

	jdouble retVal = 0;

	if ((ji + jl) >= (jf1 + jf2 + jf5)) {
		retVal = jd1 + jd2;
	} else {
		retVal = jd2;
	}

	return retVal;
}

//  this is a non-static function that sets a non-static longField to the input value
void Java_net_adoptopenjdk_bumblebench_jni_CallOverheadTestcases_testSetLongField(
		JNIEnv* env, jobject jobj, jlong val) {

	JNIBenchmark::CallOverheadPortal::setLongHandle(env, jobj, val,
			"longField");
	return;
}

//  this is a static call that sets a static long field to the input value
void Java_net_adoptopenjdk_bumblebench_jni_CallOverheadTestcases_testSetLongFieldStatic(
		JNIEnv* env, jclass jcls, jlong val) {

	JNIBenchmark::CallOverheadPortal::setStaticLongHandle(env, jcls, val,
			"longFieldStatic");
	return;
}

// test: avoiding full array copies
//  takes an input array of long, add val to each element and writes the result back to
// a output long[] via jni
void Java_net_adoptopenjdk_bumblebench_jni_CallOverheadTestcases_testArrayReadWriteRegion(
		JNIEnv* env, jobject jobj, jlongArray input, jlong val,
		jlongArray output, jint len) {

	jlong inputRegion[len];
	jlong outputRegion[len];

	env->GetLongArrayRegion(input, 0, len, inputRegion);

	for (jsize i = 0; i < len; ++i) {
		outputRegion[i] = inputRegion[i] + val;
	}

	env->SetLongArrayRegion(output, 0, len, outputRegion);
	return;
}
