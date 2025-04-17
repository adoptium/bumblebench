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

public class ReferenceProvider implements Base.BenchmarkProvider<Base.Objects> {
    public static final Unsafe UNSAFE = Base.UNSAFE;
    public static volatile long dump;
    public static volatile int incrementBy = 0;

    public Base.Objects getNativeOffsets() {
        return null;
    }

    static private final Base.Objects staticOffsets;
    static {
        class C1 {
            static Object field;
        }

        class C2 {
            static Object field;
        }

        class C3 {
            static Object field;
        }

        class C4 {
            static Object field;
        }

        class C5 {
            static Object field;
        }

        try {
            Field f1 = C1.class.getDeclaredField("field");
            Field f2 = C2.class.getDeclaredField("field");
            Field f3 = C3.class.getDeclaredField("field");
            Field f4 = C4.class.getDeclaredField("field");
            Field f5 = C5.class.getDeclaredField("field");

            Object b1 = UNSAFE.staticFieldBase(f1);
            Object b2 = UNSAFE.staticFieldBase(f2);
            Object b3 = UNSAFE.staticFieldBase(f3);
            Object b4 = UNSAFE.staticFieldBase(f4);
            Object b5 = UNSAFE.staticFieldBase(f5);

            C1.field = b2;
            C2.field = b3;
            C3.field = b4;
            C4.field = b5;
            C5.field = b1;

            long offset = UNSAFE.staticFieldOffset(f1);

            if (offset != UNSAFE.staticFieldOffset(f2)
                || offset != UNSAFE.staticFieldOffset(f3)
                || offset != UNSAFE.staticFieldOffset(f4)
                || offset != UNSAFE.staticFieldOffset(f5)) {
                throw new RuntimeException("Incorrect assumption that same shaped classes will have same static field offsets");
            }

            staticOffsets = new Base.Objects(b1, b2, b3, b4, b5, offset);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("This should be unreachable", e);
        }
    }

    public Base.Objects getStaticOffsets() {
        return staticOffsets;
    }

    static private final Base.Objects objectOffsets;
    static {
        class ObjectMember {
            Object field;

            public ObjectMember(Object f) {
                field = f;
            }
        }

        ObjectMember b1 = new ObjectMember(null);
        ObjectMember b2 = new ObjectMember(b1);
        ObjectMember b3 = new ObjectMember(b2);
        ObjectMember b4 = new ObjectMember(b3);
        ObjectMember b5 = new ObjectMember(b4);

        b1.field = b5;

        objectOffsets = new Base.Objects(
            b1, b2, b3, b4, b5,
            UNSAFE.objectFieldOffset(ObjectMember.class, "field")
        );
    }

    public Base.Objects getObjectOffsets() {
        return objectOffsets;
    }

    static private final Base.Objects arrayOffsets;
    static {
        int offset = UNSAFE.arrayBaseOffset(Object[].class);

        Object[] b1 = new Object[1];
        Object[] b2 = {b1};
        Object[] b3 = {b2};
        Object[] b4 = {b3};
        Object[] b5 = {b4};
        b1[0] = b5;

        arrayOffsets = new Base.Objects(b1, b2, b3, b4, b5, offset);
    }

    public Base.Objects getArrayOffsets() {
        return arrayOffsets;
    }

    public long stressGet(long numIterations, Base.Objects objects) {
        long offset = objects.offset;
        Object b1 = objects.base1;
        Object b2 = objects.base2;
        Object b3 = objects.base3;
        Object b4 = objects.base4;
        Object b5 = objects.base5;

        for (long i = 0; i < numIterations; i++) {
            b1 = UNSAFE.getReference(b1, offset);
            b2 = UNSAFE.getReference(b2, offset);
            b3 = UNSAFE.getReference(b3, offset);
            b4 = UNSAFE.getReference(b4, offset);
            b5 = UNSAFE.getReference(b5, offset);
        }

        dump = System.identityHashCode(b1)
             + System.identityHashCode(b2)
             + System.identityHashCode(b3)
             + System.identityHashCode(b4)
             + System.identityHashCode(b5);

        return numIterations;
    }

    public long stressGetOpaque(long numIterations, Base.Objects objects) {
        long offset = objects.offset;
        Object b1 = objects.base1;
        Object b2 = objects.base2;
        Object b3 = objects.base3;
        Object b4 = objects.base4;
        Object b5 = objects.base5;

        for (long i = 0; i < numIterations; i++) {
            b1 = UNSAFE.getReferenceOpaque(b1, offset);
            b2 = UNSAFE.getReferenceOpaque(b2, offset);
            b3 = UNSAFE.getReferenceOpaque(b3, offset);
            b4 = UNSAFE.getReferenceOpaque(b4, offset);
            b5 = UNSAFE.getReferenceOpaque(b5, offset);
        }

        dump = System.identityHashCode(b1)
             + System.identityHashCode(b2)
             + System.identityHashCode(b3)
             + System.identityHashCode(b4)
             + System.identityHashCode(b5);

        return numIterations;
    }

    public long stressGetAcquire(long numIterations, Base.Objects objects) {
        long offset = objects.offset;
        Object b1 = objects.base1;
        Object b2 = objects.base2;
        Object b3 = objects.base3;
        Object b4 = objects.base4;
        Object b5 = objects.base5;

        for (long i = 0; i < numIterations; i++) {
            b1 = UNSAFE.getReferenceAcquire(b1, offset);
            b2 = UNSAFE.getReferenceAcquire(b2, offset);
            b3 = UNSAFE.getReferenceAcquire(b3, offset);
            b4 = UNSAFE.getReferenceAcquire(b4, offset);
            b5 = UNSAFE.getReferenceAcquire(b5, offset);
        }

        dump = System.identityHashCode(b1)
             + System.identityHashCode(b2)
             + System.identityHashCode(b3)
             + System.identityHashCode(b4)
             + System.identityHashCode(b5);

        return numIterations;
    }

    public long stressGetVolatile(long numIterations, Base.Objects objects) {
        long offset = objects.offset;
        Object b1 = objects.base1;
        Object b2 = objects.base2;
        Object b3 = objects.base3;
        Object b4 = objects.base4;
        Object b5 = objects.base5;

        for (long i = 0; i < numIterations; i++) {
            b1 = UNSAFE.getReferenceVolatile(b1, offset);
            b2 = UNSAFE.getReferenceVolatile(b2, offset);
            b3 = UNSAFE.getReferenceVolatile(b3, offset);
            b4 = UNSAFE.getReferenceVolatile(b4, offset);
            b5 = UNSAFE.getReferenceVolatile(b5, offset);
        }

        dump = System.identityHashCode(b1)
             + System.identityHashCode(b2)
             + System.identityHashCode(b3)
             + System.identityHashCode(b4)
             + System.identityHashCode(b5);

        return numIterations;
    }

    public long stressPut(long numIterations, Base.Objects objects) {
        int inc = incrementBy;
        long offset = objects.offset;
        Object b1 = objects.base1;
        Object b2 = objects.base2;
        Object b3 = objects.base3;
        Object b4 = objects.base4;
        Object b5 = objects.base5;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putReference(b1, offset, b5);
            UNSAFE.putReference(b2, offset, b1);
            UNSAFE.putReference(b3, offset, b2);
            UNSAFE.putReference(b4, offset, b3);
            UNSAFE.putReference(b5, offset, b4);
            offset += inc;
        }

        return numIterations;
    }

    public long stressPutOpaque(long numIterations, Base.Objects objects) {
        int inc = incrementBy;
        long offset = objects.offset;
        Object b1 = objects.base1;
        Object b2 = objects.base2;
        Object b3 = objects.base3;
        Object b4 = objects.base4;
        Object b5 = objects.base5;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putReferenceOpaque(b1, offset, b5);
            UNSAFE.putReferenceOpaque(b2, offset, b1);
            UNSAFE.putReferenceOpaque(b3, offset, b2);
            UNSAFE.putReferenceOpaque(b4, offset, b3);
            UNSAFE.putReferenceOpaque(b5, offset, b4);
            offset += inc;
        }

        return numIterations;
    }

    public long stressPutRelease(long numIterations, Base.Objects objects) {
        int inc = incrementBy;
        long offset = objects.offset;
        Object b1 = objects.base1;
        Object b2 = objects.base2;
        Object b3 = objects.base3;
        Object b4 = objects.base4;
        Object b5 = objects.base5;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putReferenceRelease(b1, offset, b5);
            UNSAFE.putReferenceRelease(b2, offset, b1);
            UNSAFE.putReferenceRelease(b3, offset, b2);
            UNSAFE.putReferenceRelease(b4, offset, b3);
            UNSAFE.putReferenceRelease(b5, offset, b4);
            offset += inc;
        }

        return numIterations;
    }

    public long stressPutVolatile(long numIterations, Base.Objects objects) {
        int inc = incrementBy;
        long offset = objects.offset;
        Object b1 = objects.base1;
        Object b2 = objects.base2;
        Object b3 = objects.base3;
        Object b4 = objects.base4;
        Object b5 = objects.base5;

        for (long i = 0; i < numIterations; i++) {
            UNSAFE.putReferenceVolatile(b1, offset, b5);
            UNSAFE.putReferenceVolatile(b2, offset, b1);
            UNSAFE.putReferenceVolatile(b3, offset, b2);
            UNSAFE.putReferenceVolatile(b4, offset, b3);
            UNSAFE.putReferenceVolatile(b5, offset, b4);
            offset += inc;
        }

        return numIterations;
    }
}
