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
#include <assert.h>

namespace JNIBenchmark {

class CallOverheadPortal {
public:
	static jclass getJClass(JNIEnv* env) {
		jclass jclazz = env->FindClass(
				"net/adoptopenjdk/bumblebench/jni/CallOverheadTestcases");
		assert(jclazz != nullptr);
		return jclazz;
	}

	static jfieldID getHandleFieldID(JNIEnv* env, const char* fieldName,
			const char* fieldType) {

		static jfieldID fid = env->GetFieldID(
				CallOverheadPortal::getJClass(env), fieldName, fieldType);

		assert(fid != nullptr);
		return fid;
	}

	static void setLongHandle(JNIEnv* env, jobject jdb, jlong val,
			const char* fieldName) {
		env->SetLongField(jdb, getHandleFieldID(env, fieldName, "J"), val);
	}

	static void setStaticLongHandle(JNIEnv* env, jclass jcls, jlong val,
			const char* fieldName) {
		jfieldID fid = env->GetStaticFieldID(jcls, fieldName, "J");
		env->SetStaticLongField(jcls, fid, val);
	}
};
}
