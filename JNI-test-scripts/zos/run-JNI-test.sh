#!/bin/bash
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
######

JAVA=$SDK_DIR/bin/java 


#
# use bumblebench directory as test home dir
cd ../..
HOME_DIR=`pwd`

#
#		z/OS specific. Default to test 64-bit
#
cp -f libxplinkjnibench64.so libjnibench.so

#
#  set up library path for z/OS
#
export LIBPATH=./:$LIBPATH


# Add -DBumbleBench.listOptions to see all available options
$JAVA -jar BumbleBench.jar NoParamNoRetBench

$JAVA -jar BumbleBench.jar ParamNoRetBench

$JAVA -jar BumbleBench.jar ParamAndRetBench

$JAVA -jar BumbleBench.jar SetLongFieldBench

$JAVA -jar BumbleBench.jar SetStaticLongFieldBench

$JAVA -jar BumbleBench.jar ArrayReadWriteRegionBench
