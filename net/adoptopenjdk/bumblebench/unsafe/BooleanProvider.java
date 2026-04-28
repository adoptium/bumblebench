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
import java.lang.reflect.Field;
import net.adoptopenjdk.bumblebench.unsafe.Base;

public class BooleanProvider implements Base.BenchmarkProvider<Base.AbsoluteOffsets> {
    public static final Unsafe UNSAFE = Base.UNSAFE;
    public static volatile int dump;
    public static volatile int incrementBy = 0;

    static private final Base.AbsoluteOffsets nativeOffsets;
    static {
        // Assume that one boolean fits in one byte
        long offset1 = UNSAFE.allocateMemory(5);
        long offset2 = offset1 + 1;
        long offset3 = offset1 + 2;
        long offset4 = offset1 + 3;
        long offset5 = offset1 + 4;

        UNSAFE.putBoolean(null, offset1, true);
        UNSAFE.putBoolean(null, offset2, false);
        UNSAFE.putBoolean(null, offset3, true);
        UNSAFE.putBoolean(null, offset4, false);
        UNSAFE.putBoolean(null, offset5, true);

        nativeOffsets = new Base.AbsoluteOffsets(null, offset1, offset2, offset3, offset4, offset5);
    }

    public Base.AbsoluteOffsets getNativeOffsets() {
        return nativeOffsets;
    }

    static private final Base.AbsoluteOffsets staticOffsets;
    static {
        class StaticField {
            public static boolean field1, field2, field3, field4, field5;
        }

        try {
            Field f1 = StaticField.class.getDeclaredField("field1");
            Field f2 = StaticField.class.getDeclaredField("field2");
            Field f3 = StaticField.class.getDeclaredField("field3");
            Field f4 = StaticField.class.getDeclaredField("field4");
            Field f5 = StaticField.class.getDeclaredField("field5");

            staticOffsets = new Base.AbsoluteOffsets(
                UNSAFE.staticFieldBase(f1),
                UNSAFE.staticFieldOffset(f1),
                UNSAFE.staticFieldOffset(f2),
                UNSAFE.staticFieldOffset(f3),
                UNSAFE.staticFieldOffset(f4),
                UNSAFE.staticFieldOffset(f5)
            );
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("This should be unreachable", e);
        }
    }

    public Base.AbsoluteOffsets getStaticOffsets() {
        return staticOffsets;
    }

    static private final Base.AbsoluteOffsets objectOffsets;
    static {
        class ObjectMember {
            public boolean field1, field2, field3, field4, field5;
        }

        objectOffsets = new Base.AbsoluteOffsets(
            new ObjectMember(),
            UNSAFE.objectFieldOffset(ObjectMember.class, "field1"),
            UNSAFE.objectFieldOffset(ObjectMember.class, "field2"),
            UNSAFE.objectFieldOffset(ObjectMember.class, "field3"),
            UNSAFE.objectFieldOffset(ObjectMember.class, "field4"),
            UNSAFE.objectFieldOffset(ObjectMember.class, "field5")
        );
    }

    public Base.AbsoluteOffsets getObjectOffsets() {
        return objectOffsets;
    }

    static private final Base.AbsoluteOffsets arrayOffsets;
    static {
        int offset = UNSAFE.arrayBaseOffset(boolean[].class);
        int ascale = UNSAFE.arrayIndexScale(boolean[].class);
        int shift = 31 - Integer.numberOfLeadingZeros(ascale);

        arrayOffsets = new Base.AbsoluteOffsets(
            new boolean[5],
            offset,
            offset + (1 << shift),
            offset + (2 << shift),
            offset + (3 << shift),
            offset + (4 << shift)
        );
    }

    public Base.AbsoluteOffsets getArrayOffsets() {
        return arrayOffsets;
    }

    public long stressGet(long numIterations, Base.AbsoluteOffsets offsets) {
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        int acc1 = 0, acc2 = 0, acc3 = 0, acc4 = 0, acc5 = 0;

        for (long i = 0; i < numIterations; i++) {
            acc1 += UNSAFE.getBoolean(base, o1) ? 1 : 0;
            acc2 += UNSAFE.getBoolean(base, o2) ? 1 : 0;
            acc3 += UNSAFE.getBoolean(base, o3) ? 1 : 0;
            acc4 += UNSAFE.getBoolean(base, o4) ? 1 : 0;
            acc5 += UNSAFE.getBoolean(base, o5) ? 1 : 0;
        }

        dump = acc1 + acc2 + acc3 + acc4 + acc5;
        return numIterations;
    }

    public long stressGetOpaque(long numIterations, Base.AbsoluteOffsets offsets) {
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        int acc1 = 0, acc2 = 0, acc3 = 0, acc4 = 0, acc5 = 0;

        for (long i = 0; i < numIterations; i++) {
            acc1 += UNSAFE.getBooleanOpaque(base, o1) ? 1 : 0;
            acc2 += UNSAFE.getBooleanOpaque(base, o2) ? 1 : 0;
            acc3 += UNSAFE.getBooleanOpaque(base, o3) ? 1 : 0;
            acc4 += UNSAFE.getBooleanOpaque(base, o4) ? 1 : 0;
            acc5 += UNSAFE.getBooleanOpaque(base, o5) ? 1 : 0;
        }

        dump = acc1 + acc2 + acc3 + acc4 + acc5;
        return numIterations;
    }

    public long stressGetAcquire(long numIterations, Base.AbsoluteOffsets offsets) {
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        int acc1 = 0, acc2 = 0, acc3 = 0, acc4 = 0, acc5 = 0;

        for (long i = 0; i < numIterations; i++) {
            acc1 += UNSAFE.getBooleanAcquire(base, o1) ? 1 : 0;
            acc2 += UNSAFE.getBooleanAcquire(base, o2) ? 1 : 0;
            acc3 += UNSAFE.getBooleanAcquire(base, o3) ? 1 : 0;
            acc4 += UNSAFE.getBooleanAcquire(base, o4) ? 1 : 0;
            acc5 += UNSAFE.getBooleanAcquire(base, o5) ? 1 : 0;
        }

        dump = acc1 + acc2 + acc3 + acc4 + acc5;
        return numIterations;
    }

    public long stressGetVolatile(long numIterations, Base.AbsoluteOffsets offsets) {
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        int acc1 = 0, acc2 = 0, acc3 = 0, acc4 = 0, acc5 = 0;

        for (long i = 0; i < numIterations; i++) {
            acc1 += UNSAFE.getBooleanVolatile(base, o1) ? 1 : 0;
            acc2 += UNSAFE.getBooleanVolatile(base, o2) ? 1 : 0;
            acc3 += UNSAFE.getBooleanVolatile(base, o3) ? 1 : 0;
            acc4 += UNSAFE.getBooleanVolatile(base, o4) ? 1 : 0;
            acc5 += UNSAFE.getBooleanVolatile(base, o5) ? 1 : 0;
        }

        dump = acc1 + acc2 + acc3 + acc4 + acc5;
        return numIterations;
    }

    public long stressPut(long numIterations, Base.AbsoluteOffsets offsets) {
        int inc = incrementBy;
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putBoolean(base, o1, (i & 1) == 0); o1 += inc;
            UNSAFE.putBoolean(base, o2, (i & 1) == 0); o2 += inc;
            UNSAFE.putBoolean(base, o3, (i & 1) == 0); o3 += inc;
            UNSAFE.putBoolean(base, o4, (i & 1) == 0); o4 += inc;
            UNSAFE.putBoolean(base, o5, (i & 1) == 0); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutOpaque(long numIterations, Base.AbsoluteOffsets offsets) {
        int inc = incrementBy;
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putBooleanOpaque(base, o1, (i & 1) == 0); o1 += inc;
            UNSAFE.putBooleanOpaque(base, o2, (i & 1) == 0); o2 += inc;
            UNSAFE.putBooleanOpaque(base, o3, (i & 1) == 0); o3 += inc;
            UNSAFE.putBooleanOpaque(base, o4, (i & 1) == 0); o4 += inc;
            UNSAFE.putBooleanOpaque(base, o5, (i & 1) == 0); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutRelease(long numIterations, Base.AbsoluteOffsets offsets) {
        int inc = incrementBy;
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putBooleanRelease(base, o1, (i & 1) == 0); o1 += inc;
            UNSAFE.putBooleanRelease(base, o2, (i & 1) == 0); o2 += inc;
            UNSAFE.putBooleanRelease(base, o3, (i & 1) == 0); o3 += inc;
            UNSAFE.putBooleanRelease(base, o4, (i & 1) == 0); o4 += inc;
            UNSAFE.putBooleanRelease(base, o5, (i & 1) == 0); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutVolatile(long numIterations, Base.AbsoluteOffsets offsets) {
        int inc = incrementBy;
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putBooleanVolatile(base, o1, (i & 1) == 0); o1 += inc;
            UNSAFE.putBooleanVolatile(base, o2, (i & 1) == 0); o2 += inc;
            UNSAFE.putBooleanVolatile(base, o3, (i & 1) == 0); o3 += inc;
            UNSAFE.putBooleanVolatile(base, o4, (i & 1) == 0); o4 += inc;
            UNSAFE.putBooleanVolatile(base, o5, (i & 1) == 0); o5 += inc;
        }

        return numIterations;
    }
}
