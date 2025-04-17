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

package net.adoptopenjdk.bumblebench.unsafe;

import jdk.internal.misc.Unsafe;
import java.lang.Runtime;
import java.lang.Thread;

public class Base {
    public static final Unsafe UNSAFE;
    public static volatile int dump;
    public static volatile int incrementBy = 0;

    static {
        try {
            UNSAFE = Unsafe.getUnsafe();
        } catch (IllegalAccessError e) {
            System.err.println("add \"--add-exports java.base/jdk.internal.misc=ALL-UNNAMED\" to your java command");
            throw new RuntimeException("Unable to get Unsafe instance.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get Unsafe instance.", e);
        }
    }

    public static abstract class Offsets {}

    public static class AbsoluteOffsets extends Offsets {
        public Object base;
        public long field1Offset, field2Offset, field3Offset, field4Offset, field5Offset;

        public AbsoluteOffsets(Object b, long f1, long f2, long f3, long f4, long f5) {
            base = b;
            field1Offset = f1;
            field2Offset = f2;
            field3Offset = f3;
            field4Offset = f4;
            field5Offset = f5;
        }
    }

    public static class RelativeOffsets extends AbsoluteOffsets {
        public long baseOffset;

        public RelativeOffsets(Object b, long bo, long f1, long f2, long f3, long f4, long f5) {
            super(b, f1, f2, f3, f4, f5);
            baseOffset = bo;
        }
    }

    public static class Objects extends Offsets {
        public Object base1, base2, base3, base4, base5;
        public long offset;

        public Objects(Object b1, Object b2, Object b3, Object b4, Object b5, long o) {
            base1 = b1;
            base2 = b2;
            base3 = b3;
            base4 = b4;
            base5 = b5;
            offset = o;
        }
    }

    public static interface BenchmarkProvider<T extends Offsets> {
        public T getNativeOffsets();
        public T getStaticOffsets();
        public T getObjectOffsets();
        public T getArrayOffsets();

        public long stressGet(long numIterations, T offsets);
        public long stressGetOpaque(long numIterations, T offsets);
        public long stressGetAcquire(long numIterations, T offsets);
        public long stressGetVolatile(long numIterations, T offsets);

        public long stressPut(long numIterations, T offsets);
        public long stressPutOpaque(long numIterations, T offsets);
        public long stressPutRelease(long numIterations, T offsets);
        public long stressPutVolatile(long numIterations, T offsets);
    }
}
