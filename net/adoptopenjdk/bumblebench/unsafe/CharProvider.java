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
import java.lang.Character;
import java.lang.reflect.Field;
import net.adoptopenjdk.bumblebench.unsafe.Base;

public class CharProvider implements Base.BenchmarkProvider<Base.RelativeOffsets> {
    public static final Unsafe UNSAFE = Base.UNSAFE;
    public static volatile int dump;
    public static volatile int incrementBy = 0;

    static private final Base.RelativeOffsets nativeOffsets;
    static {
        long baseOffset = UNSAFE.allocateMemory(5 * Character.BYTES);
        char offset1 = (char) 0;
        char offset2 = (char) Character.BYTES;
        char offset3 = (char) 2 * Character.BYTES;
        char offset4 = (char) 3 * Character.BYTES;
        char offset5 = (char) 4 * Character.BYTES;

        UNSAFE.putChar(null, baseOffset + offset1, offset2);
        UNSAFE.putChar(null, baseOffset + offset2, offset3);
        UNSAFE.putChar(null, baseOffset + offset3, offset4);
        UNSAFE.putChar(null, baseOffset + offset4, offset5);
        UNSAFE.putChar(null, baseOffset + offset5, offset1);

        nativeOffsets = new Base.RelativeOffsets(null, baseOffset, offset1, offset2, offset3, offset4, offset5);
    }

    public Base.RelativeOffsets getNativeOffsets() {
        return nativeOffsets;
    }

    static private final Base.RelativeOffsets staticOffsets;
    static {
        class StaticField {
            public static char field1, field2, field3, field4, field5;
        }

        try {
            Field f1 = StaticField.class.getDeclaredField("field1");
            Field f2 = StaticField.class.getDeclaredField("field2");
            Field f3 = StaticField.class.getDeclaredField("field3");
            Field f4 = StaticField.class.getDeclaredField("field4");
            Field f5 = StaticField.class.getDeclaredField("field5");

            long baseOffset = UNSAFE.staticFieldOffset(f1);
            char field1Offset = 0;
            char field2Offset = (char) (UNSAFE.staticFieldOffset(f2) - baseOffset);
            char field3Offset = (char) (UNSAFE.staticFieldOffset(f3) - baseOffset);
            char field4Offset = (char) (UNSAFE.staticFieldOffset(f4) - baseOffset);
            char field5Offset = (char) (UNSAFE.staticFieldOffset(f5) - baseOffset);

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
            public char field1, field2, field3, field4, field5;

            public ObjectMember(char f1, char f2, char f3, char f4, char f5) {
                field1 = f1;
                field2 = f2;
                field3 = f3;
                field4 = f4;
                field5 = f5;
            }
        }

        long baseOffset = UNSAFE.objectFieldOffset(ObjectMember.class, "field1");
        char field1Offset = 0;
        char field2Offset = (char) (UNSAFE.objectFieldOffset(ObjectMember.class, "field2") - baseOffset);
        char field3Offset = (char) (UNSAFE.objectFieldOffset(ObjectMember.class, "field3") - baseOffset);
        char field4Offset = (char) (UNSAFE.objectFieldOffset(ObjectMember.class, "field4") - baseOffset);
        char field5Offset = (char) (UNSAFE.objectFieldOffset(ObjectMember.class, "field5") - baseOffset);

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
        int baseOffset = UNSAFE.arrayBaseOffset(char[].class);
        int ascale = UNSAFE.arrayIndexScale(char[].class);
        int shift = 31 - Integer.numberOfLeadingZeros(ascale);

        char field1Offset = 0;
        char field2Offset = (char) (1 << shift);
        char field3Offset = (char) (2 << shift);
        char field4Offset = (char) (3 << shift);
        char field5Offset = (char) (4 << shift);

        char[] arr = {field2Offset, field3Offset, field4Offset, field5Offset, field1Offset};

        arrayOffsets = new Base.RelativeOffsets(arr, baseOffset, field1Offset, field2Offset, field3Offset, field4Offset, field5Offset);
    }

    public Base.RelativeOffsets getArrayOffsets() {
        return arrayOffsets;
    }

    public long stressGet(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        char o1 = (char) offsets.field1Offset;
        char o2 = (char) offsets.field2Offset;
        char o3 = (char) offsets.field3Offset;
        char o4 = (char) offsets.field4Offset;
        char o5 = (char) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getChar(base, baseOffset + o1);
            o2 = UNSAFE.getChar(base, baseOffset + o2);
            o3 = UNSAFE.getChar(base, baseOffset + o3);
            o4 = UNSAFE.getChar(base, baseOffset + o4);
            o5 = UNSAFE.getChar(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetOpaque(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        char o1 = (char) offsets.field1Offset;
        char o2 = (char) offsets.field2Offset;
        char o3 = (char) offsets.field3Offset;
        char o4 = (char) offsets.field4Offset;
        char o5 = (char) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getCharOpaque(base, baseOffset + o1);
            o2 = UNSAFE.getCharOpaque(base, baseOffset + o2);
            o3 = UNSAFE.getCharOpaque(base, baseOffset + o3);
            o4 = UNSAFE.getCharOpaque(base, baseOffset + o4);
            o5 = UNSAFE.getCharOpaque(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetAcquire(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        char o1 = (char) offsets.field1Offset;
        char o2 = (char) offsets.field2Offset;
        char o3 = (char) offsets.field3Offset;
        char o4 = (char) offsets.field4Offset;
        char o5 = (char) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getCharAcquire(base, baseOffset + o1);
            o2 = UNSAFE.getCharAcquire(base, baseOffset + o2);
            o3 = UNSAFE.getCharAcquire(base, baseOffset + o3);
            o4 = UNSAFE.getCharAcquire(base, baseOffset + o4);
            o5 = UNSAFE.getCharAcquire(base, baseOffset + o5);
        }

        dump = o1 + o2 + o3 + o4 + o5;
        return numIterations;
    }

    public long stressGetVolatile(long numIterations, Base.RelativeOffsets offsets) {
        Object base = offsets.base;
        long baseOffset = offsets.baseOffset;
        char o1 = (char) offsets.field1Offset;
        char o2 = (char) offsets.field2Offset;
        char o3 = (char) offsets.field3Offset;
        char o4 = (char) offsets.field4Offset;
        char o5 = (char) offsets.field5Offset;

        for (long i = 0; i < numIterations; i++) {
            o1 = UNSAFE.getCharVolatile(base, baseOffset + o1);
            o2 = UNSAFE.getCharVolatile(base, baseOffset + o2);
            o3 = UNSAFE.getCharVolatile(base, baseOffset + o3);
            o4 = UNSAFE.getCharVolatile(base, baseOffset + o4);
            o5 = UNSAFE.getCharVolatile(base, baseOffset + o5);
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
            UNSAFE.putChar(base, o1, (char) i); o1 += inc;
            UNSAFE.putChar(base, o2, (char) i); o2 += inc;
            UNSAFE.putChar(base, o3, (char) i); o3 += inc;
            UNSAFE.putChar(base, o4, (char) i); o4 += inc;
            UNSAFE.putChar(base, o5, (char) i); o5 += inc;
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
            UNSAFE.putCharOpaque(base, o1, (char) i); o1 += inc;
            UNSAFE.putCharOpaque(base, o2, (char) i); o2 += inc;
            UNSAFE.putCharOpaque(base, o3, (char) i); o3 += inc;
            UNSAFE.putCharOpaque(base, o4, (char) i); o4 += inc;
            UNSAFE.putCharOpaque(base, o5, (char) i); o5 += inc;
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
            UNSAFE.putCharRelease(base, o1, (char) i); o1 += inc;
            UNSAFE.putCharRelease(base, o2, (char) i); o2 += inc;
            UNSAFE.putCharRelease(base, o3, (char) i); o3 += inc;
            UNSAFE.putCharRelease(base, o4, (char) i); o4 += inc;
            UNSAFE.putCharRelease(base, o5, (char) i); o5 += inc;
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
            UNSAFE.putCharVolatile(base, o1, (char) i); o1 += inc;
            UNSAFE.putCharVolatile(base, o2, (char) i); o2 += inc;
            UNSAFE.putCharVolatile(base, o3, (char) i); o3 += inc;
            UNSAFE.putCharVolatile(base, o4, (char) i); o4 += inc;
            UNSAFE.putCharVolatile(base, o5, (char) i); o5 += inc;
        }

        return numIterations;
    }
}
