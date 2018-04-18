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

if [[ ! -f $SDK_DIR/bin/java ]]; then
    echo "Java not found"
    exit -1
fi 

echo "Using this Java SDK"
$SDK_DIR/bin/java -version

# HOME_DIR is the root of bumblebench
cd ../../
HOME_DIR=`pwd`

echo Using $HOME_DIR as bumblebench home directory.

rm -f ./*.o
rm -f ./*.so


CPP_DIR="$HOME_DIR/net/adoptopenjdk/bumblebench/jni/c++/"
export INC_PATH=" -I$HOME_DIR -I$CPP_DIR -I$SDK_DIR/include/zos -I$SDK_DIR/include/ "

#
# Generate header files for our JNI Java class
#
$SDK_DIR/bin/javah net.adoptopenjdk.bumblebench.jni.CallOverheadTestcases


#
#		Compile the C++ libraries
#		Step 1. ascii->ebcdic conversion
#
#
cd $CPP_DIR

echo "Using HOME_DIR $HOME_DIR"
echo "Using JDK_HOME $JDK_HOME"
echo "Clearing old builds"

rm -fv ./*.so
rm -fv ./*.o


CPP_DIR="$HOME_DIR/net/adoptopenjdk/bumblebench/jni/c++/"
cd $CPP_DIR

rm -f ./*.o
rm -f ./*.so

mv $HOME_DIR/*.h $CPP_DIR/


CPP_FILES=`find . | grep '\.cpp' | tr '\n' ' '`

CXXFLAGS="-shared -v -fPIC -std=c++11 -O1"
INC_PATH="-I$CPP_DIR -I$JDK_HOME/include/ -I$JDK_HOME/include/linux "


# use g++. gcc requires -lstdc++
g++ $CXXFLAGS -o libjnibench.so $INC_PATH $CPP_FILES

mv $CPP_DIR/*.so $HOME_DIR/

cd $HOME_DIR