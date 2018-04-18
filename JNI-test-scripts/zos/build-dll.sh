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

rm -f ./*.so
rm -f ./*.o
rm -f ./*.asmlist

CPP_FILES=`find . | grep '\.cpp' | tr '\n' ' '`

if [[ ! -f "EBCDIC_DONE" ]]; then
	iconv -fISO8859-1 -tIBM-1047 CallOverheadTestcases.cpp > CallOverheadTestcases.cpp-ebc
	iconv -fISO8859-1 -tIBM-1047 Portal.h > Portal.h-ebc
	rm -f Portal.h CallOverheadTestcases.cpp
	mv Portal.h-ebc Portal.h
	mv CallOverheadTestcases.cpp-ebc CallOverheadTestcases.cpp
	touch EBCDIC_DONE
fi 

mv $HOME_DIR/*.h $CPP_DIR/

#
#		Compile the C++ libraries
#		Step 2. compile
#
#
# make a Std non-xplink JNI benchmark DLL
#
#
#		NOTE: xlc can't handle files with the same name. Hence, the **JavaObject.cpp file names.
#	
echo  "Making 32-bit standard non-xplink JNI benchmark DLL"
export CXXFLAGS="-qlist=no-xplink.asmlist -Dnullptr=NULL -Wc,convlit(ISO8859-1) -Wc,NOANSIALIAS -q32 -Wc,noxplink -O -qlanglvl=extended0x -Wc,DLL,EXPORTALL -Wa,DLL -Wc,ARCH(7) -Wc,TUNE(10) "
xlC $CXXFLAGS -o libstdlinkjnibench.so $INC_PATH $CPP_FILES


echo  "Making 32-bit xplink JNI benchmark DLL"
export CXXFLAGS="-qlist=xplink32.asmlist -Dnullptr=NULL -Wc,convlit(ISO8859-1) -Wc,NOANSIALIAS -q32 -Wc,xplink -O -qlanglvl=extended0x -Wc,DLL,EXPORTALL -Wa,DLL -Wc,ARCH(7) -Wc,TUNE(10) "
xlC $CXXFLAGS -o libxplinkjnibench31.so $INC_PATH $CPP_FILES


echo  "Making 64-bit xplink JNI benchmark DLL"
rm -f ./*.o
export CXXFLAGS=" -qlist=xplink64.asmlist -Dnullptr=NULL -Wc,convlit(ISO8859-1) -Wc,NOANSIALIAS -Wc,xplink -Wc,lp64 -O -qlanglvl=extended0x -Wc,DLL,EXPORTALL -Wa,DLL -Wc,ARCH(7) -Wc,TUNE(10) "
xlC $CXXFLAGS -o libxplinkjnibench64.so $INC_PATH $CPP_FILES



#
#
#	Move all DLL to bumblebench home directory
#
mv $CPP_DIR/*.so $HOME_DIR/
cd $HOME_DIR