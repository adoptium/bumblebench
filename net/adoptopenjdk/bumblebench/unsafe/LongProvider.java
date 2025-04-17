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
import java.lang.Long;
import java.lang.reflect.Field;
import net.adoptopenjdk.bumblebench.unsafe.Base;

public class LongProvider implements Base.BenchmarkProvider<Base.AbsoluteOffsets> {
    public static final Unsafe UNSAFE = Base.UNSAFE;
    public static volatile long dump;
    public static volatile int incrementBy = 0;

    static private final Base.AbsoluteOffsets nativeOffsets;
    static {
        long offset1 = UNSAFE.allocateMemory(5 * Long.BYTES);
        long offset2 = offset1 + Long.BYTES;
        long offset3 = offset2 + Long.BYTES;
        long offset4 = offset3 + Long.BYTES;
        long offset5 = offset4 + Long.BYTES;

        UNSAFE.putLong(null, offset1, offset2);
        UNSAFE.putLong(null, offset2, offset3);
        UNSAFE.putLong(null, offset3, offset4);
        UNSAFE.putLong(null, offset4, offset5);
        UNSAFE.putLong(null, offset5, offset1);

        nativeOffsets = new Base.AbsoluteOffsets(null, offset1, offset2, offset3, offset4, offset5);
    }

    public Base.AbsoluteOffsets getNativeOffsets() {
        return nativeOffsets;
    }

    static private final Base.AbsoluteOffsets staticOffsets;
    static {
        class StaticField {
            public static long field1, field2, field3, field4, field5;
        }

        try {
            Field f1 = StaticField.class.getDeclaredField("field1");
            Field f2 = StaticField.class.getDeclaredField("field2");
            Field f3 = StaticField.class.getDeclaredField("field3");
            Field f4 = StaticField.class.getDeclaredField("field4");
            Field f5 = StaticField.class.getDeclaredField("field5");

            long field1Offset = UNSAFE.staticFieldOffset(f1);
            long field2Offset = UNSAFE.staticFieldOffset(f2);
            long field3Offset = UNSAFE.staticFieldOffset(f3);
            long field4Offset = UNSAFE.staticFieldOffset(f4);
            long field5Offset = UNSAFE.staticFieldOffset(f5);

            StaticField.field1 = field2Offset;
            StaticField.field2 = field3Offset;
            StaticField.field3 = field4Offset;
            StaticField.field4 = field5Offset;
            StaticField.field5 = field1Offset;

            staticOffsets = new Base.AbsoluteOffsets(
                UNSAFE.staticFieldBase(f1),
                field1Offset,
                field2Offset,
                field3Offset,
                field4Offset,
                field5Offset
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
            public long field1, field2, field3, field4, field5;

            public ObjectMember(long f1, long f2, long f3, long f4, long f5) {
                field1 = f1;
                field2 = f2;
                field3 = f3;
                field4 = f4;
                field5 = f5;
            }
        }

        long field1Offset = UNSAFE.objectFieldOffset(ObjectMember.class, "field1");
        long field2Offset = UNSAFE.objectFieldOffset(ObjectMember.class, "field2");
        long field3Offset = UNSAFE.objectFieldOffset(ObjectMember.class, "field3");
        long field4Offset = UNSAFE.objectFieldOffset(ObjectMember.class, "field4");
        long field5Offset = UNSAFE.objectFieldOffset(ObjectMember.class, "field5");

        objectOffsets = new Base.AbsoluteOffsets(
            new ObjectMember(field2Offset, field3Offset, field4Offset, field5Offset, field1Offset),
            field1Offset, field2Offset, field3Offset, field4Offset, field5Offset
        );
    }

    public Base.AbsoluteOffsets getObjectOffsets() {
        return objectOffsets;
    }

    static private final Base.AbsoluteOffsets arrayOffsets;
    static {
        int offset = UNSAFE.arrayBaseOffset(long[].class);
        int ascale = UNSAFE.arrayIndexScale(long[].class);
        int shift = 31 - Integer.numberOfLeadingZeros(ascale);

        long field1Offset = offset;
        long field2Offset = offset + (1 << shift);
        long field3Offset = offset + (2 << shift);
        long field4Offset = offset + (3 << shift);
        long field5Offset = offset + (4 << shift);

        long[] arr = {field2Offset, field3Offset, field4Offset, field5Offset, field1Offset};

        arrayOffsets = new Base.AbsoluteOffsets(arr, field1Offset, field2Offset, field3Offset, field4Offset, field5Offset);
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

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getLong(base, o1);
            o2 = UNSAFE.getLong(base, o2);
            o3 = UNSAFE.getLong(base, o3);
            o4 = UNSAFE.getLong(base, o4);
            o5 = UNSAFE.getLong(base, o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetOpaque(long numIterations, Base.AbsoluteOffsets offsets) {
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getLongOpaque(base, o1);
            o2 = UNSAFE.getLongOpaque(base, o2);
            o3 = UNSAFE.getLongOpaque(base, o3);
            o4 = UNSAFE.getLongOpaque(base, o4);
            o5 = UNSAFE.getLongOpaque(base, o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetAcquire(long numIterations, Base.AbsoluteOffsets offsets) {
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getLongAcquire(base, o1);
            o2 = UNSAFE.getLongAcquire(base, o2);
            o3 = UNSAFE.getLongAcquire(base, o3);
            o4 = UNSAFE.getLongAcquire(base, o4);
            o5 = UNSAFE.getLongAcquire(base, o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetVolatile(long numIterations, Base.AbsoluteOffsets offsets) {
        Object base = offsets.base;
        long o1 = offsets.field1Offset;
        long o2 = offsets.field2Offset;
        long o3 = offsets.field3Offset;
        long o4 = offsets.field4Offset;
        long o5 = offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getLongAcquire(base, o1);
            o2 = UNSAFE.getLongAcquire(base, o2);
            o3 = UNSAFE.getLongAcquire(base, o3);
            o4 = UNSAFE.getLongAcquire(base, o4);
            o5 = UNSAFE.getLongAcquire(base, o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
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
            UNSAFE.putLong(base, o1, i); o1 += inc;
            UNSAFE.putLong(base, o2, i); o2 += inc;
            UNSAFE.putLong(base, o3, i); o3 += inc;
            UNSAFE.putLong(base, o4, i); o4 += inc;
            UNSAFE.putLong(base, o5, i); o5 += inc;
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
            UNSAFE.putLongOpaque(base, o1, i); o1 += inc;
            UNSAFE.putLongOpaque(base, o2, i); o2 += inc;
            UNSAFE.putLongOpaque(base, o3, i); o3 += inc;
            UNSAFE.putLongOpaque(base, o4, i); o4 += inc;
            UNSAFE.putLongOpaque(base, o5, i); o5 += inc;
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
            UNSAFE.putLongRelease(base, o1, i); o1 += inc;
            UNSAFE.putLongRelease(base, o2, i); o2 += inc;
            UNSAFE.putLongRelease(base, o3, i); o3 += inc;
            UNSAFE.putLongRelease(base, o4, i); o4 += inc;
            UNSAFE.putLongRelease(base, o5, i); o5 += inc;
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
            UNSAFE.putLongVolatile(base, o1, i); o1 += inc;
            UNSAFE.putLongVolatile(base, o2, i); o2 += inc;
            UNSAFE.putLongVolatile(base, o3, i); o3 += inc;
            UNSAFE.putLongVolatile(base, o4, i); o4 += inc;
            UNSAFE.putLongVolatile(base, o5, i); o5 += inc;
        }

        return numIterations;
    }
}
