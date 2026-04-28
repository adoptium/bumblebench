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
import java.lang.Double;
import java.lang.reflect.Field;
import net.adoptopenjdk.bumblebench.unsafe.Base;

public class DoubleProvider implements Base.BenchmarkProvider<Base.AbsoluteOffsets> {
    public static final Unsafe UNSAFE = Base.UNSAFE;
    public static volatile double dump;
    public static volatile int incrementBy = 0;

    static private final Base.AbsoluteOffsets nativeOffsets;
    static {
        long offset1 = UNSAFE.allocateMemory(5);
        long offset2 = offset1 + Double.BYTES;
        long offset3 = offset2 + Double.BYTES;
        long offset4 = offset3 + Double.BYTES;
        long offset5 = offset4 + Double.BYTES;

        nativeOffsets = new Base.AbsoluteOffsets(null, offset1, offset2, offset3, offset4, offset5);
    }

    public Base.AbsoluteOffsets getNativeOffsets() {
        return nativeOffsets;
    }

    static private final Base.AbsoluteOffsets staticOffsets;
    static {
        class StaticField {
            public static double field1, field2, field3, field4, field5;
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
            public double field1, field2, field3, field4, field5;
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
        int offset = UNSAFE.arrayBaseOffset(double[].class);
        int ascale = UNSAFE.arrayIndexScale(double[].class);
        int shift = 31 - Integer.numberOfLeadingZeros(ascale);

        arrayOffsets = new Base.AbsoluteOffsets(
            new double[5],
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

        int inc = incrementBy;
        double acc1 = 0, acc2 = 0, acc3 = 0, acc4 = 0, acc5 = 0;

        for (long i = 0; i < numIterations; i++) {
            acc1 += UNSAFE.getDouble(base, o1); o1 += inc;
            acc2 += UNSAFE.getDouble(base, o2); o2 += inc;
            acc3 += UNSAFE.getDouble(base, o3); o3 += inc;
            acc4 += UNSAFE.getDouble(base, o4); o4 += inc;
            acc5 += UNSAFE.getDouble(base, o5); o5 += inc;
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

        int inc = incrementBy;
        double acc1 = 0, acc2 = 0, acc3 = 0, acc4 = 0, acc5 = 0;

        for (long i = 0; i < numIterations; i++) {
            acc1 += UNSAFE.getDoubleOpaque(base, o1); o1 += inc;
            acc2 += UNSAFE.getDoubleOpaque(base, o2); o2 += inc;
            acc3 += UNSAFE.getDoubleOpaque(base, o3); o3 += inc;
            acc4 += UNSAFE.getDoubleOpaque(base, o4); o4 += inc;
            acc5 += UNSAFE.getDoubleOpaque(base, o5); o5 += inc;
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

        int inc = incrementBy;
        double acc1 = 0, acc2 = 0, acc3 = 0, acc4 = 0, acc5 = 0;

        for (long i = 0; i < numIterations; i++) {
            acc1 += UNSAFE.getDoubleAcquire(base, o1); o1 += inc;
            acc2 += UNSAFE.getDoubleAcquire(base, o2); o2 += inc;
            acc3 += UNSAFE.getDoubleAcquire(base, o3); o3 += inc;
            acc4 += UNSAFE.getDoubleAcquire(base, o4); o4 += inc;
            acc5 += UNSAFE.getDoubleAcquire(base, o5); o5 += inc;
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

        int inc = incrementBy;
        double acc1 = 0, acc2 = 0, acc3 = 0, acc4 = 0, acc5 = 0;

        for (long i = 0; i < numIterations; i++) {
            acc1 += UNSAFE.getDoubleVolatile(base, o1); o1 += inc;
            acc2 += UNSAFE.getDoubleVolatile(base, o2); o2 += inc;
            acc3 += UNSAFE.getDoubleVolatile(base, o3); o3 += inc;
            acc4 += UNSAFE.getDoubleVolatile(base, o4); o4 += inc;
            acc5 += UNSAFE.getDoubleVolatile(base, o5); o5 += inc;
        }

        dump = acc1 + acc2 + acc3 + acc4 + acc5;
        return numIterations;
    }

    public long stressPut(long numIterations, Base.AbsoluteOffsets offsets) {
        int inc = incrementBy;
        double d = 0;

        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++, d++) {
            UNSAFE.putDouble(base, o1, d); o1 += inc;
            UNSAFE.putDouble(base, o2, d); o2 += inc;
            UNSAFE.putDouble(base, o3, d); o3 += inc;
            UNSAFE.putDouble(base, o4, d); o4 += inc;
            UNSAFE.putDouble(base, o5, d); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutOpaque(long numIterations, Base.AbsoluteOffsets offsets) {
        int inc = incrementBy;
        double d = 0;

        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++, d++) {
            UNSAFE.putDoubleOpaque(base, o1, d); o1 += inc;
            UNSAFE.putDoubleOpaque(base, o2, d); o2 += inc;
            UNSAFE.putDoubleOpaque(base, o3, d); o3 += inc;
            UNSAFE.putDoubleOpaque(base, o4, d); o4 += inc;
            UNSAFE.putDoubleOpaque(base, o5, d); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutRelease(long numIterations, Base.AbsoluteOffsets offsets) {
        int inc = incrementBy;
        double d = 0;

        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++, d++) {
            UNSAFE.putDoubleRelease(base, o1, d); o1 += inc;
            UNSAFE.putDoubleRelease(base, o2, d); o2 += inc;
            UNSAFE.putDoubleRelease(base, o3, d); o3 += inc;
            UNSAFE.putDoubleRelease(base, o4, d); o4 += inc;
            UNSAFE.putDoubleRelease(base, o5, d); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutVolatile(long numIterations, Base.AbsoluteOffsets offsets) {
        int inc = incrementBy;
        double d = 0;

        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++, d++) {
            UNSAFE.putDoubleVolatile(base, o1, d); o1 += inc;
            UNSAFE.putDoubleVolatile(base, o2, d); o2 += inc;
            UNSAFE.putDoubleVolatile(base, o3, d); o3 += inc;
            UNSAFE.putDoubleVolatile(base, o4, d); o4 += inc;
            UNSAFE.putDoubleVolatile(base, o5, d); o5 += inc;
        }

        return numIterations;
    }
}
