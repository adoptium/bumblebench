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
import java.lang.Byte;
import java.lang.reflect.Field;
import net.adoptopenjdk.bumblebench.unsafe.Base;

public class ByteProvider implements Base.BenchmarkProvider<Base.RelativeOffsets> {
    public static final Unsafe UNSAFE = Base.UNSAFE;
    public static volatile int dump;
    public static volatile int incrementBy = 0;

    static private final Base.RelativeOffsets nativeOffsets;
    static {
        long baseOffset = UNSAFE.allocateMemory(5 * Byte.BYTES);
        byte offset1 = (byte) 0;
        byte offset2 = (byte) Byte.BYTES;
        byte offset3 = (byte) 2 * Byte.BYTES;
        byte offset4 = (byte) 3 * Byte.BYTES;
        byte offset5 = (byte) 4 * Byte.BYTES;

        UNSAFE.putByte(null, baseOffset + offset1, offset2);
        UNSAFE.putByte(null, baseOffset + offset2, offset3);
        UNSAFE.putByte(null, baseOffset + offset3, offset4);
        UNSAFE.putByte(null, baseOffset + offset4, offset5);
        UNSAFE.putByte(null, baseOffset + offset5, offset1);

        nativeOffsets = new Base.RelativeOffsets(null, baseOffset, offset1, offset2, offset3, offset4, offset5);
    }

    public Base.RelativeOffsets getNativeOffsets() {
        return nativeOffsets;
    }

    static private final Base.RelativeOffsets staticOffsets;
    static {
        class StaticField {
            public static byte field1, field2, field3, field4, field5;
        }

        try {
            Field f1 = StaticField.class.getDeclaredField("field1");
            Field f2 = StaticField.class.getDeclaredField("field2");
            Field f3 = StaticField.class.getDeclaredField("field3");
            Field f4 = StaticField.class.getDeclaredField("field4");
            Field f5 = StaticField.class.getDeclaredField("field5");

            long baseOffset = UNSAFE.staticFieldOffset(f1);
            byte field1Offset = 0;
            byte field2Offset = (byte) (UNSAFE.staticFieldOffset(f2) - baseOffset);
            byte field3Offset = (byte) (UNSAFE.staticFieldOffset(f3) - baseOffset);
            byte field4Offset = (byte) (UNSAFE.staticFieldOffset(f4) - baseOffset);
            byte field5Offset = (byte) (UNSAFE.staticFieldOffset(f5) - baseOffset);

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
            public byte field1, field2, field3, field4, field5;

            public ObjectMember(byte f1, byte f2, byte f3, byte f4, byte f5) {
                field1 = f1;
                field2 = f2;
                field3 = f3;
                field4 = f4;
                field5 = f5;
            }
        }

        long baseOffset = UNSAFE.objectFieldOffset(ObjectMember.class, "field1");
        byte field1Offset = 0;
        byte field2Offset = (byte) (UNSAFE.objectFieldOffset(ObjectMember.class, "field2") - baseOffset);
        byte field3Offset = (byte) (UNSAFE.objectFieldOffset(ObjectMember.class, "field3") - baseOffset);
        byte field4Offset = (byte) (UNSAFE.objectFieldOffset(ObjectMember.class, "field4") - baseOffset);
        byte field5Offset = (byte) (UNSAFE.objectFieldOffset(ObjectMember.class, "field5") - baseOffset);

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
        int baseOffset = UNSAFE.arrayBaseOffset(byte[].class);
        int ascale = UNSAFE.arrayIndexScale(byte[].class);
        int shift = 31 - Integer.numberOfLeadingZeros(ascale);

        byte field1Offset = 0;
        byte field2Offset = (byte) (1 << shift);
        byte field3Offset = (byte) (2 << shift);
        byte field4Offset = (byte) (3 << shift);
        byte field5Offset = (byte) (4 << shift);

        byte[] arr = {field2Offset, field3Offset, field4Offset, field5Offset, field1Offset};

        arrayOffsets = new Base.RelativeOffsets(arr, baseOffset, field1Offset, field2Offset, field3Offset, field4Offset, field5Offset);
    }

    public Base.RelativeOffsets getArrayOffsets() {
        return arrayOffsets;
    }

    public long stressGet(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        byte o1 = (byte) offsets.field1Offset;
        byte o2 = (byte) offsets.field2Offset;
        byte o3 = (byte) offsets.field3Offset;
        byte o4 = (byte) offsets.field4Offset;
        byte o5 = (byte) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getByte(base, baseOffset + o1);
            o2 = UNSAFE.getByte(base, baseOffset + o2);
            o3 = UNSAFE.getByte(base, baseOffset + o3);
            o4 = UNSAFE.getByte(base, baseOffset + o4);
            o5 = UNSAFE.getByte(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetOpaque(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        byte o1 = (byte) offsets.field1Offset;
        byte o2 = (byte) offsets.field2Offset;
        byte o3 = (byte) offsets.field3Offset;
        byte o4 = (byte) offsets.field4Offset;
        byte o5 = (byte) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getByteOpaque(base, baseOffset + o1);
            o2 = UNSAFE.getByteOpaque(base, baseOffset + o2);
            o3 = UNSAFE.getByteOpaque(base, baseOffset + o3);
            o4 = UNSAFE.getByteOpaque(base, baseOffset + o4);
            o5 = UNSAFE.getByteOpaque(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetAcquire(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        byte o1 = (byte) offsets.field1Offset;
        byte o2 = (byte) offsets.field2Offset;
        byte o3 = (byte) offsets.field3Offset;
        byte o4 = (byte) offsets.field4Offset;
        byte o5 = (byte) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getByteAcquire(base, baseOffset + o1);
            o2 = UNSAFE.getByteAcquire(base, baseOffset + o2);
            o3 = UNSAFE.getByteAcquire(base, baseOffset + o3);
            o4 = UNSAFE.getByteAcquire(base, baseOffset + o4);
            o5 = UNSAFE.getByteAcquire(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetVolatile(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        byte o1 = (byte) offsets.field1Offset;
        byte o2 = (byte) offsets.field2Offset;
        byte o3 = (byte) offsets.field3Offset;
        byte o4 = (byte) offsets.field4Offset;
        byte o5 = (byte) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getByteVolatile(base, baseOffset + o1);
            o2 = UNSAFE.getByteVolatile(base, baseOffset + o2);
            o3 = UNSAFE.getByteVolatile(base, baseOffset + o3);
            o4 = UNSAFE.getByteVolatile(base, baseOffset + o4);
            o5 = UNSAFE.getByteVolatile(base, baseOffset + o5);
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
                UNSAFE.putByte(base, o1, (byte) i); o1 += inc;
                UNSAFE.putByte(base, o2, (byte) i); o2 += inc;
                UNSAFE.putByte(base, o3, (byte) i); o3 += inc;
                UNSAFE.putByte(base, o4, (byte) i); o4 += inc;
                UNSAFE.putByte(base, o5, (byte) i); o5 += inc;
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
                UNSAFE.putByteOpaque(base, o1, (byte) i); o1 += inc;
                UNSAFE.putByteOpaque(base, o2, (byte) i); o2 += inc;
                UNSAFE.putByteOpaque(base, o3, (byte) i); o3 += inc;
                UNSAFE.putByteOpaque(base, o4, (byte) i); o4 += inc;
                UNSAFE.putByteOpaque(base, o5, (byte) i); o5 += inc;
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
                UNSAFE.putByteRelease(base, o1, (byte) i); o1 += inc;
                UNSAFE.putByteRelease(base, o2, (byte) i); o2 += inc;
                UNSAFE.putByteRelease(base, o3, (byte) i); o3 += inc;
                UNSAFE.putByteRelease(base, o4, (byte) i); o4 += inc;
                UNSAFE.putByteRelease(base, o5, (byte) i); o5 += inc;
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
                UNSAFE.putByteVolatile(base, o1, (byte) i); o1 += inc;
                UNSAFE.putByteVolatile(base, o2, (byte) i); o2 += inc;
                UNSAFE.putByteVolatile(base, o3, (byte) i); o3 += inc;
                UNSAFE.putByteVolatile(base, o4, (byte) i); o4 += inc;
                UNSAFE.putByteVolatile(base, o5, (byte) i); o5 += inc;
            }

        return numIterations;
    }
}
