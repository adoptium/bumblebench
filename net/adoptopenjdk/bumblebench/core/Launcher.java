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

package net.adoptopenjdk.bumblebench.core;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;



public class Launcher extends Util {

	public static void main(String[] args) throws Exception {
		loadPropertiesFrom("/net/adoptopenjdk/bumblebench/core/BumbleBench.properties");
		String packagePath = option("packages", defaultPackagePath);
		if (false) {
			// We considered doing this to be more tolerant of user error.
			// However, given the confusion that can occur with paths (which are
			// usually directory names, while the packages list is composed of
			// package names), this seemed more likely to cause additional confusion.
			//
			packagePath = packagePath.replace('/', '.').replace('\\', '.');
		}
		String[] packages = packagePath.split("[:;]");

		if (args.length < 1) {
			err().println("\n-= BumbleBench series " + BumbleBench.SERIES_NUMBER + " version " + BumbleBench.VERSION_NUMBER + "." + BumbleBench.REVISION_NUMBER + " =-\n");
			listBenchmarksIn(System.getProperty("java.class.path").split(":")[0], packages);
			System.exit(1);
		}

		String testName = args[0].replace('.', '$');
		if (testName.equals("JITserver")){
			parseJitServerOptions();
		}

		Class testClass = loadTestClass(packages, testName);
		int numParallelInstances = option("parallelInstances", 0);
		if (numParallelInstances >= 1)
			runBumbleMainOn(ParallelBench.create(numParallelInstances, testClass));
		else if (option("measureStartup", false))
			runBumbleMainOn(StartupBench.create(testClass));
		else
			runBumbleMainOn((BumbleBench)testClass.newInstance());
	}
	public static void parseJitServerOptions() throws IOException {
		File file = new File("./JITServerArgs.txt");
		System.getProperties().load(new FileInputStream(file));
	}

	static void runBumbleMainOn(BumbleBench instance) throws NoSuchMethodException, IllegalAccessException {
		Method mainMethod = instance.getClass().getMethod(option("bumbleMain", "bumbleMain"));
		try {
			mainMethod.invoke(instance);
		} catch (InvocationTargetException e) {
			// Strip off the distracting InvocationTargetException so we don't confuse the user
			throw new RuntimeException(e.getCause());
		}
	}

	static final String defaultPackagePath = ":"
			+ ":net.adoptopenjdk.bumblebench.collections"
			+ ":net.adoptopenjdk.bumblebench.crypto"
			+ ":net.adoptopenjdk.bumblebench.examples"
			+ ":net.adoptopenjdk.bumblebench.gpu"
			+ ":net.adoptopenjdk.bumblebench.lambda"
			+ ":net.adoptopenjdk.bumblebench.math"
			+ ":net.adoptopenjdk.bumblebench.indy"
			+ ":net.adoptopenjdk.bumblebench.daa"
			+ ":net.adoptopenjdk.bumblebench.json"
			+ ":net.adoptopenjdk.bumblebench.string"
			+ ":net.adoptopenjdk.bumblebench.humble"
			+ ":net.adoptopenjdk.bumblebench.arraycopy"
			+ ":net.adoptopenjdk.bumblebench.jitserver"
			;

	public static Class loadTestClass(String[] packageNames, String name) throws ClassNotFoundException, IOException {
		ClassNotFoundException typicalException = null;
		for (String packageName: packageNames) {
			Class testClass = loadTestClass(packageName, name);
			if (testClass != null)
				return testClass;
		}
		throw new ClassNotFoundException(name);
	}

	public static Class loadTestClass(String packageName, String name) throws ClassNotFoundException, IOException {
		String filePath = packageName.replace('.', '/');
		String fileName = '/' + qualifiedFileName(filePath, name) + ".properties";
		if (loadPropertiesFrom(fileName)) {
			// Found a properties file, ergo we've got the right package.  We're
			// committed now.  Load the class indicated by the properties file.
			// If it's found, that's an error.
			return Class.forName(qualifiedClassName(packageName, option("class", name)));
		} else {
			// No properties file.  Try the default.  If it's not found, that's
			// not an error; it just means we're looking at the wrong package.
			try {
				return Class.forName(qualifiedClassName(packageName, name));
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
	}

	static boolean loadPropertiesFrom(String propertiesFileName) throws IOException {
		InputStream stream = Launcher.class.getResourceAsStream(propertiesFileName);
		if (stream == null) {
			if (DEBUG_OPTIONS)
				out().println("No such properties file: " + propertiesFileName);
			return false;
		} else {
			if (DEBUG_OPTIONS)
				out().println("Reading properties from: " + propertiesFileName);
			System.getProperties().load(stream);
			stream.close();
			return true;
		}
	}

	private static String qualifiedClassName(String packageName, String className) {
		if (packageName.equals(""))
			return className;
		else
			return packageName + "." + className;
	}

	private static String qualifiedFileName(String path, String fileName) {
		if (path.equals(""))
			return fileName;
		else
			return path + "/" + fileName;
	}

	static void listBenchmarksIn(String jarFileName, String[] packages) throws Exception {
		ClassLoader loader = Launcher.class.getClassLoader();
		ZipFile jarFile = new ZipFile(jarFileName);
		int unsupportedClasses = 0;
		for (java.util.Enumeration<?> enumeration = jarFile.entries(); enumeration.hasMoreElements(); ) {
			ZipEntry entry = (ZipEntry) enumeration.nextElement();
			String entryName = entry.getName();
			if (entryName.endsWith(".class")) {
				String className = entryName.substring(0, entryName.length()-6).replace('/','.');
				try {
					Class c = Class.forName(className, false, loader); // Testcase <clinit> can be expensive, so load without initializing
					if (!Modifier.isAbstract(c.getModifiers()) && BumbleBench.class.isAssignableFrom(c))
						System.out.println(nameFromPath(c.getCanonicalName(), packages));
				} catch (UnsupportedClassVersionError e) {
					unsupportedClasses += 1;
				} catch (NoClassDefFoundError e) {}
			}
		}
		if (unsupportedClasses >= 1) {
			err().println("NOTE: There are additional benchmarks available that cannot be run by your java version");
		}
	}

	static String nameFromPath(String name, String[] packages) {
		String bestMatch = "";
		for (String p: packages) {
			if (name.startsWith(p) && p.length() > bestMatch.length()) {
				bestMatch = p;
			}
		}
		if (bestMatch.equals(""))
			return name;
		else
			return name.substring(bestMatch.length()+1);
	}

}

