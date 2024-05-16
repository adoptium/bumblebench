package net.adoptopenjdk.bumblebench.jitserver;

import net.adoptopenjdk.bumblebench.core.MicroBench;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class JITserver extends MicroBench {

    // Classes and corresponding invocation counts
    static final HashMap<Class<? extends MicroBench>, Integer> classesToInvocation;
    static final boolean isMultiThreaded;

    static {
        classesToInvocation = option("classesToInvoc", new HashMap<>());
        isMultiThreaded = option("multi-threaded", true);
    }

    @Override
    protected long doBatch(long numIterations) throws InterruptedException {

        ArrayList<Thread> threads = new ArrayList<>();

        for (long i = 0; i < numIterations; i++) {

            // Use Reflection to call doBatch for required number of invocations.
            for(Map.Entry<Class<? extends MicroBench>, Integer> classIntegerEntry : classesToInvocation.entrySet()){

                Class<? extends MicroBench> classKey = classIntegerEntry.getKey();
                Integer invocationCountValue = classIntegerEntry.getValue();

                for(int j = 0; j < invocationCountValue; j++){
                    Method methodReq;
                    try {
                        methodReq = classKey.getDeclaredMethod("doBatch", long.class);
                    } catch (NoSuchMethodException e) {
                        System.err.println("doBatch not implemented");
                        throw new RuntimeException(e);
                    }
                    methodReq.setAccessible(true);

                    if(isMultiThreaded){
                        // Thread responsible for spawning a doBatch
                        Thread t = getThread(methodReq, classKey);
                        threads.add(t);
                    }
                    else {
                        try {
                            methodReq.invoke(classKey.newInstance(), 1);
                        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                            System.err.println("Could not dynamically initiate doBatch");
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        for(Thread thread : threads){
            thread.join();
        }

        return numIterations;
    }

    private static Thread getThread(Method methodReq, Class<? extends MicroBench> classKey) {
        Thread t = new Thread(() -> {
            try {
                methodReq.invoke(classKey.newInstance(), 1);
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                System.err.println("Could not dynamically initiate doBatch");
                throw new RuntimeException(e);
            }
        });
        t.start();
        return t;
    }
}
