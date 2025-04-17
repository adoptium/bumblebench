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

public class IntProvider implements Base.BenchmarkProvider<Base.RelativeOffsets> {
    public static final Unsafe UNSAFE = Base.UNSAFE;
    public static volatile int dump;
    public static volatile int incrementBy = 0;

    static private final Base.RelativeOffsets nativeOffsets;
    static {
        long baseOffset = UNSAFE.allocateMemory(5 * Integer.BYTES);
        int offset1 = (int) 0;
        int offset2 = (int) Integer.BYTES;
        int offset3 = (int) 2 * Integer.BYTES;
        int offset4 = (int) 3 * Integer.BYTES;
        int offset5 = (int) 4 * Integer.BYTES;

        UNSAFE.putInt(null, baseOffset + offset1, offset2);
        UNSAFE.putInt(null, baseOffset + offset2, offset3);
        UNSAFE.putInt(null, baseOffset + offset3, offset4);
        UNSAFE.putInt(null, baseOffset + offset4, offset5);
        UNSAFE.putInt(null, baseOffset + offset5, offset1);

        nativeOffsets = new Base.RelativeOffsets(null, baseOffset, offset1, offset2, offset3, offset4, offset5);
    }

    public Base.RelativeOffsets getNativeOffsets() {
        return nativeOffsets;
    }

    static private final Base.RelativeOffsets staticOffsets;
    static {
        class StaticField {
            public static int field1, field2, field3, field4, field5;
        }

        try {
            Field f1 = StaticField.class.getDeclaredField("field1");
            Field f2 = StaticField.class.getDeclaredField("field2");
            Field f3 = StaticField.class.getDeclaredField("field3");
            Field f4 = StaticField.class.getDeclaredField("field4");
            Field f5 = StaticField.class.getDeclaredField("field5");

            long baseOffset = UNSAFE.staticFieldOffset(f1);
            int field1Offset = 0;
            int field2Offset = (int) (UNSAFE.staticFieldOffset(f2) - baseOffset);
            int field3Offset = (int) (UNSAFE.staticFieldOffset(f3) - baseOffset);
            int field4Offset = (int) (UNSAFE.staticFieldOffset(f4) - baseOffset);
            int field5Offset = (int) (UNSAFE.staticFieldOffset(f5) - baseOffset);

            StaticField.field1 = field2Offset;
            StaticField.field2 = field3Offset;
            StaticField.field3 = field4Offset;
            StaticField.field4 = field5Offset;
            StaticField.field5 = field1Offset;

            staticOffsets = new Base.RelativeOffsets(
                UNSAFE.staticFieldBase(f1),
                baseOffset,
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

    public Base.RelativeOffsets getStaticOffsets() {
        return staticOffsets;
    }

    static private final Base.RelativeOffsets objectOffsets;
    static {
        class ObjectMember {
            public int field1, field2, field3, field4, field5;

            public ObjectMember(int f1, int f2, int f3, int f4, int f5) {
                field1 = f1;
                field2 = f2;
                field3 = f3;
                field4 = f4;
                field5 = f5;
            }
        }

        long baseOffset = UNSAFE.objectFieldOffset(ObjectMember.class, "field1");
        int field1Offset = 0;
        int field2Offset = (int) (UNSAFE.objectFieldOffset(ObjectMember.class, "field2") - baseOffset);
        int field3Offset = (int) (UNSAFE.objectFieldOffset(ObjectMember.class, "field3") - baseOffset);
        int field4Offset = (int) (UNSAFE.objectFieldOffset(ObjectMember.class, "field4") - baseOffset);
        int field5Offset = (int) (UNSAFE.objectFieldOffset(ObjectMember.class, "field5") - baseOffset);

        objectOffsets = new Base.RelativeOffsets(
            new ObjectMember(field2Offset, field3Offset, field4Offset, field5Offset, field1Offset),
            baseOffset, field1Offset, field2Offset, field3Offset, field4Offset, field5Offset
        );
    }

    public Base.RelativeOffsets getObjectOffsets() {
        return objectOffsets;
    }

    static private final Base.RelativeOffsets arrayOffsets;
    static {
        int baseOffset = UNSAFE.arrayBaseOffset(int[].class);
        int ascale = UNSAFE.arrayIndexScale(int[].class);
        int shift = 31 - Integer.numberOfLeadingZeros(ascale);

        int field1Offset = 0;
        int field2Offset = (int) (1 << shift);
        int field3Offset = (int) (2 << shift);
        int field4Offset = (int) (3 << shift);
        int field5Offset = (int) (4 << shift);

        int[] arr = {field2Offset, field3Offset, field4Offset, field5Offset, field1Offset};

        arrayOffsets = new Base.RelativeOffsets(arr, baseOffset, field1Offset, field2Offset, field3Offset, field4Offset, field5Offset);
    }

    public Base.RelativeOffsets getArrayOffsets() {
        return arrayOffsets;
    }

    public long stressGet(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        int o1 = (int) offsets.field1Offset;
        int o2 = (int) offsets.field2Offset;
        int o3 = (int) offsets.field3Offset;
        int o4 = (int) offsets.field4Offset;
        int o5 = (int) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getInt(base, baseOffset + o1);
            o2 = UNSAFE.getInt(base, baseOffset + o2);
            o3 = UNSAFE.getInt(base, baseOffset + o3);
            o4 = UNSAFE.getInt(base, baseOffset + o4);
            o5 = UNSAFE.getInt(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetOpaque(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        int o1 = (int) offsets.field1Offset;
        int o2 = (int) offsets.field2Offset;
        int o3 = (int) offsets.field3Offset;
        int o4 = (int) offsets.field4Offset;
        int o5 = (int) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getIntOpaque(base, baseOffset + o1);
            o2 = UNSAFE.getIntOpaque(base, baseOffset + o2);
            o3 = UNSAFE.getIntOpaque(base, baseOffset + o3);
            o4 = UNSAFE.getIntOpaque(base, baseOffset + o4);
            o5 = UNSAFE.getIntOpaque(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetAcquire(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        int o1 = (int) offsets.field1Offset;
        int o2 = (int) offsets.field2Offset;
        int o3 = (int) offsets.field3Offset;
        int o4 = (int) offsets.field4Offset;
        int o5 = (int) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getIntAcquire(base, baseOffset + o1);
            o2 = UNSAFE.getIntAcquire(base, baseOffset + o2);
            o3 = UNSAFE.getIntAcquire(base, baseOffset + o3);
            o4 = UNSAFE.getIntAcquire(base, baseOffset + o4);
            o5 = UNSAFE.getIntAcquire(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetVolatile(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        int o1 = (int) offsets.field1Offset;
        int o2 = (int) offsets.field2Offset;
        int o3 = (int) offsets.field3Offset;
        int o4 = (int) offsets.field4Offset;
        int o5 = (int) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getIntVolatile(base, baseOffset + o1);
            o2 = UNSAFE.getIntVolatile(base, baseOffset + o2);
            o3 = UNSAFE.getIntVolatile(base, baseOffset + o3);
            o4 = UNSAFE.getIntVolatile(base, baseOffset + o4);
            o5 = UNSAFE.getIntVolatile(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressPut(long numIterations, Base.RelativeOffsets offsets) {
        int inc = incrementBy;
        Object base = offsets.base;
        long o1 = offsets.baseOffset + offsets.field1Offset;
        long o2 = offsets.baseOffset + offsets.field2Offset;
        long o3 = offsets.baseOffset + offsets.field3Offset;
        long o4 = offsets.baseOffset + offsets.field4Offset;
        long o5 = offsets.baseOffset + offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putInt(base, o1, (int) i); o1 += inc;
            UNSAFE.putInt(base, o2, (int) i); o2 += inc;
            UNSAFE.putInt(base, o3, (int) i); o3 += inc;
            UNSAFE.putInt(base, o4, (int) i); o4 += inc;
            UNSAFE.putInt(base, o5, (int) i); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutOpaque(long numIterations, Base.RelativeOffsets offsets) {
        int inc = incrementBy;
        Object base = offsets.base;
        long o1 = offsets.baseOffset + offsets.field1Offset;
        long o2 = offsets.baseOffset + offsets.field2Offset;
        long o3 = offsets.baseOffset + offsets.field3Offset;
        long o4 = offsets.baseOffset + offsets.field4Offset;
        long o5 = offsets.baseOffset + offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putIntOpaque(base, o1, (int) i); o1 += inc;
            UNSAFE.putIntOpaque(base, o2, (int) i); o2 += inc;
            UNSAFE.putIntOpaque(base, o3, (int) i); o3 += inc;
            UNSAFE.putIntOpaque(base, o4, (int) i); o4 += inc;
            UNSAFE.putIntOpaque(base, o5, (int) i); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutRelease(long numIterations, Base.RelativeOffsets offsets) {
        int inc = incrementBy;
        Object base = offsets.base;
        long o1 = offsets.baseOffset + offsets.field1Offset;
        long o2 = offsets.baseOffset + offsets.field2Offset;
        long o3 = offsets.baseOffset + offsets.field3Offset;
        long o4 = offsets.baseOffset + offsets.field4Offset;
        long o5 = offsets.baseOffset + offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putIntRelease(base, o1, (int) i); o1 += inc;
            UNSAFE.putIntRelease(base, o2, (int) i); o2 += inc;
            UNSAFE.putIntRelease(base, o3, (int) i); o3 += inc;
            UNSAFE.putIntRelease(base, o4, (int) i); o4 += inc;
            UNSAFE.putIntRelease(base, o5, (int) i); o5 += inc;
        }

        return numIterations;
    }

    public long stressPutVolatile(long numIterations, Base.RelativeOffsets offsets) {
        int inc = incrementBy;
        Object base = offsets.base;
        long o1 = offsets.baseOffset + offsets.field1Offset;
        long o2 = offsets.baseOffset + offsets.field2Offset;
        long o3 = offsets.baseOffset + offsets.field3Offset;
        long o4 = offsets.baseOffset + offsets.field4Offset;
        long o5 = offsets.baseOffset + offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putIntVolatile(base, o1, (int) i); o1 += inc;
            UNSAFE.putIntVolatile(base, o2, (int) i); o2 += inc;
            UNSAFE.putIntVolatile(base, o3, (int) i); o3 += inc;
            UNSAFE.putIntVolatile(base, o4, (int) i); o4 += inc;
            UNSAFE.putIntVolatile(base, o5, (int) i); o5 += inc;
        }

        return numIterations;
    }
}
