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
import java.lang.Short;
import java.lang.reflect.Field;
import net.adoptopenjdk.bumblebench.unsafe.Base;

public class ShortProvider implements Base.BenchmarkProvider<Base.RelativeOffsets> {
    public static final Unsafe UNSAFE = Base.UNSAFE;
    public static volatile int dump;
    public static volatile int incrementBy = 0;

    static private final Base.RelativeOffsets nativeOffsets;
    static {
        long baseOffset = UNSAFE.allocateMemory(5 * Short.BYTES);
        short offset1 = (short) 0;
        short offset2 = (short) Short.BYTES;
        short offset3 = (short) 2 * Short.BYTES;
        short offset4 = (short) 3 * Short.BYTES;
        short offset5 = (short) 4 * Short.BYTES;

        UNSAFE.putShort(null, baseOffset + offset1, offset2);
        UNSAFE.putShort(null, baseOffset + offset2, offset3);
        UNSAFE.putShort(null, baseOffset + offset3, offset4);
        UNSAFE.putShort(null, baseOffset + offset4, offset5);
        UNSAFE.putShort(null, baseOffset + offset5, offset1);

        nativeOffsets = new Base.RelativeOffsets(null, baseOffset, offset1, offset2, offset3, offset4, offset5);
    }

    public Base.RelativeOffsets getNativeOffsets() {
        return nativeOffsets;
    }

    static private final Base.RelativeOffsets staticOffsets;
    static {
        class StaticField {
            public static short field1, field2, field3, field4, field5;
        }

        try {
            Field f1 = StaticField.class.getDeclaredField("field1");
            Field f2 = StaticField.class.getDeclaredField("field2");
            Field f3 = StaticField.class.getDeclaredField("field3");
            Field f4 = StaticField.class.getDeclaredField("field4");
            Field f5 = StaticField.class.getDeclaredField("field5");

            long baseOffset = UNSAFE.staticFieldOffset(f1);
            short field1Offset = 0;
            short field2Offset = (short) (UNSAFE.staticFieldOffset(f2) - baseOffset);
            short field3Offset = (short) (UNSAFE.staticFieldOffset(f3) - baseOffset);
            short field4Offset = (short) (UNSAFE.staticFieldOffset(f4) - baseOffset);
            short field5Offset = (short) (UNSAFE.staticFieldOffset(f5) - baseOffset);

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
            public short field1, field2, field3, field4, field5;

            public ObjectMember(short f1, short f2, short f3, short f4, short f5) {
                field1 = f1;
                field2 = f2;
                field3 = f3;
                field4 = f4;
                field5 = f5;
            }
        }

        long baseOffset = UNSAFE.objectFieldOffset(ObjectMember.class, "field1");
        short field1Offset = 0;
        short field2Offset = (short) (UNSAFE.objectFieldOffset(ObjectMember.class, "field2") - baseOffset);
        short field3Offset = (short) (UNSAFE.objectFieldOffset(ObjectMember.class, "field3") - baseOffset);
        short field4Offset = (short) (UNSAFE.objectFieldOffset(ObjectMember.class, "field4") - baseOffset);
        short field5Offset = (short) (UNSAFE.objectFieldOffset(ObjectMember.class, "field5") - baseOffset);

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
        int baseOffset = UNSAFE.arrayBaseOffset(short[].class);
        int ascale = UNSAFE.arrayIndexScale(short[].class);
        int shift = 31 - Integer.numberOfLeadingZeros(ascale);

        short field1Offset = 0;
        short field2Offset = (short) (1 << shift);
        short field3Offset = (short) (2 << shift);
        short field4Offset = (short) (3 << shift);
        short field5Offset = (short) (4 << shift);

        short[] arr = {field2Offset, field3Offset, field4Offset, field5Offset, field1Offset};

        arrayOffsets = new Base.RelativeOffsets(arr, baseOffset, field1Offset, field2Offset, field3Offset, field4Offset, field5Offset);
    }

    public Base.RelativeOffsets getArrayOffsets() {
        return arrayOffsets;
    }

    public long stressGet(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        short o1 = (short) offsets.field1Offset;
        short o2 = (short) offsets.field2Offset;
        short o3 = (short) offsets.field3Offset;
        short o4 = (short) offsets.field4Offset;
        short o5 = (short) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getShort(base, baseOffset + o1);
            o2 = UNSAFE.getShort(base, baseOffset + o2);
            o3 = UNSAFE.getShort(base, baseOffset + o3);
            o4 = UNSAFE.getShort(base, baseOffset + o4);
            o5 = UNSAFE.getShort(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetOpaque(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        short o1 = (short) offsets.field1Offset;
        short o2 = (short) offsets.field2Offset;
        short o3 = (short) offsets.field3Offset;
        short o4 = (short) offsets.field4Offset;
        short o5 = (short) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getShortOpaque(base, baseOffset + o1);
            o2 = UNSAFE.getShortOpaque(base, baseOffset + o2);
            o3 = UNSAFE.getShortOpaque(base, baseOffset + o3);
            o4 = UNSAFE.getShortOpaque(base, baseOffset + o4);
            o5 = UNSAFE.getShortOpaque(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetAcquire(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        short o1 = (short) offsets.field1Offset;
        short o2 = (short) offsets.field2Offset;
        short o3 = (short) offsets.field3Offset;
        short o4 = (short) offsets.field4Offset;
        short o5 = (short) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getShortAcquire(base, baseOffset + o1);
            o2 = UNSAFE.getShortAcquire(base, baseOffset + o2);
            o3 = UNSAFE.getShortAcquire(base, baseOffset + o3);
            o4 = UNSAFE.getShortAcquire(base, baseOffset + o4);
            o5 = UNSAFE.getShortAcquire(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetVolatile(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        short o1 = (short) offsets.field1Offset;
        short o2 = (short) offsets.field2Offset;
        short o3 = (short) offsets.field3Offset;
        short o4 = (short) offsets.field4Offset;
        short o5 = (short) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getShortVolatile(base, baseOffset + o1);
            o2 = UNSAFE.getShortVolatile(base, baseOffset + o2);
            o3 = UNSAFE.getShortVolatile(base, baseOffset + o3);
            o4 = UNSAFE.getShortVolatile(base, baseOffset + o4);
            o5 = UNSAFE.getShortVolatile(base, baseOffset + o5);
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
            UNSAFE.putShort(base, o1, (short) i); o1 += inc;
            UNSAFE.putShort(base, o2, (short) i); o2 += inc;
            UNSAFE.putShort(base, o3, (short) i); o3 += inc;
            UNSAFE.putShort(base, o4, (short) i); o4 += inc;
            UNSAFE.putShort(base, o5, (short) i); o5 += inc;
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
            UNSAFE.putShortOpaque(base, o1, (short) i); o1 += inc;
            UNSAFE.putShortOpaque(base, o2, (short) i); o2 += inc;
            UNSAFE.putShortOpaque(base, o3, (short) i); o3 += inc;
            UNSAFE.putShortOpaque(base, o4, (short) i); o4 += inc;
            UNSAFE.putShortOpaque(base, o5, (short) i); o5 += inc;
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
            UNSAFE.putShortRelease(base, o1, (short) i); o1 += inc;
            UNSAFE.putShortRelease(base, o2, (short) i); o2 += inc;
            UNSAFE.putShortRelease(base, o3, (short) i); o3 += inc;
            UNSAFE.putShortRelease(base, o4, (short) i); o4 += inc;
            UNSAFE.putShortRelease(base, o5, (short) i); o5 += inc;
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
            UNSAFE.putShortVolatile(base, o1, (short) i); o1 += inc;
            UNSAFE.putShortVolatile(base, o2, (short) i); o2 += inc;
            UNSAFE.putShortVolatile(base, o3, (short) i); o3 += inc;
            UNSAFE.putShortVolatile(base, o4, (short) i); o4 += inc;
            UNSAFE.putShortVolatile(base, o5, (short) i); o5 += inc;
        }

        return numIterations;
    }
}
