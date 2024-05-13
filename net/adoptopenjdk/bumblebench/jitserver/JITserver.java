//package net.adoptopenjdk.bumblebench.jitserver;
//
//import net.adoptopenjdk.bumblebench.core.MicroBench;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
//public final class JITserver extends MicroBench {
//
//    // Classes and corresponding invocation counts
//    static final HashMap<Class<? extends MicroBench>, Integer> classesToInvocation;
//
//    static {
//        classesToInvocation = option("classesToInvoc", new HashMap<>());
//    }
//
//    @Override
//    protected long doBatch(long numIterations) throws InterruptedException {
//
//        for (long i = 0; i < numIterations; i++) {
//
//            // Use Reflection to call doBatch for required number of invocations.
//            for(Map.Entry<Class<? extends MicroBench>, Integer> classIntegerEntry : classesToInvocation.entrySet()){
//
//                Class<? extends MicroBench> classKey = classIntegerEntry.getKey();
//                Integer invocationCountValue = classIntegerEntry.getValue();
//
//                for(int j = 0; j < invocationCountValue; j++){
//                    try {
//                        Method methodReq = classKey.getMethod("doBatch", long.class);
//                        methodReq.invoke(classKey.newInstance());
//                    } catch (NoSuchMethodException e) {
//                        System.err.println("Class needs to extend doBatch!");
//                        throw new RuntimeException(e);
//                    } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
//                        System.err.println("Could not dynamically initiate doBatch");
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//
//        }
//
//        return numIterations;
//    }
//}
