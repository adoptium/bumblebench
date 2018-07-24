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

package net.adoptopenjdk.bumblebench.string;

import java.util.Locale;
import java.util.Random;

import net.adoptopenjdk.bumblebench.core.MiniBench;

import java.io.File;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * 
 * on z13 and newer z/Architectures with vector facilities enabled, string
 * toUpper and toLower can be accelerated by vector instructions.
 * 
 * This benchmark can generate strings of fixed/variable lengths and invokes
 * toUpper/toLower to measure string conversion perf.
 * 
 * The reported score is in terms of number of string conversions per sec.
 * String length is by default 877, and can be randomnized using options.
 * 
 * Note: adding to much randomnization to bumblebench workloads seems to make
 * the benchmark framework a little to aggressive in terms of estimating the
 * target threashold.
 * 
 * */

public final class StringConversionBench extends MiniBench {
}
