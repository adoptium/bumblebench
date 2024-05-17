package net.adoptopenjdk.bumblebench.jitserver;

import net.adoptopenjdk.bumblebench.core.MicroBench;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public final class JITserver extends MicroBench {

    // Classes and corresponding invocation counts
    static final ArrayList<ArrayList<Object[]>> classesToInvocation;

    static {
        classesToInvocation = option("classesToInvoc", new ArrayList<>());
    }

    @Override
    protected long doBatch(long numIterations) throws InterruptedException {

        ArrayList<Thread> threads = new ArrayList<>();

        // JITServer doBatch iterations
        for (long i = 0; i < numIterations; i++) {

            // Create each thread
            for(ArrayList<Object[]> eachThread : classesToInvocation){

                Method[] methodReqArr = new Method[eachThread.size()];
                Class<? extends MicroBench>[] classKeyArr = new Class[eachThread.size()];
                int[] invocCountArr = new int[eachThread.size()];

               // Find Class, invocation count, and method for each kernel within thread
                int sequentialCalls = 0;
                for(Object[] classIntegerEntry : eachThread){

                    // Use Reflection to call doBatch for required number of invocations.
                    Class<? extends MicroBench> classKey = (Class<? extends MicroBench>) classIntegerEntry[0];
                    Integer invocationCountValue = (Integer) classIntegerEntry[1];
                    Method methodReq;
                    try {
                        methodReq = classKey.getDeclaredMethod("doBatch", long.class);
                    } catch (NoSuchMethodException e) {
                        System.err.println("doBatch not implemented");
                        throw new RuntimeException(e);
                    }
                    methodReq.setAccessible(true);
                    methodReqArr[sequentialCalls] = methodReq;
                    classKeyArr[sequentialCalls] = classKey;
                    invocCountArr[sequentialCalls] = invocationCountValue;

                    sequentialCalls++;
                }

                // Thread responsible for spawning a doBatch
                Thread t = getThread(methodReqArr, classKeyArr, invocCountArr);
                threads.add(t);
            }

            for(Thread thread : threads){
                thread.join();
            }
        }

        return numIterations;
    }

    private static Thread getThread(Method[] methodReqArr, Class<? extends MicroBench>[] classKeyArr, int[] invocationCountArr) {
        Thread t = new Thread(() -> {
            try {
                // Sequentially call the doBatch for each kernel with their corresponding invocation count.
                for(int i = 0; i < methodReqArr.length; i++) {
                    methodReqArr[i].invoke(classKeyArr[i].newInstance(), invocationCountArr[i]);
                }
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                System.err.println("Could not dynamically initiate doBatch");
                throw new RuntimeException(e);
            }
        });
        t.start();
        return t;
    }
}
