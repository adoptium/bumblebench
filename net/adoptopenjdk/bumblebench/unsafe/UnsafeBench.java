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

import net.adoptopenjdk.bumblebench.unsafe.*;
import net.adoptopenjdk.bumblebench.core.MicroBench;

public class UnsafeBench {
    public static class Get {
        public static class Byte {
            static Base.BenchmarkProvider provider = new ByteProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Char {
            static Base.BenchmarkProvider provider = new CharProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Short {
            static Base.BenchmarkProvider provider = new ShortProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Int {
            static Base.BenchmarkProvider provider = new IntProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Boolean {
            static Base.BenchmarkProvider provider = new BooleanProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Double {
            static Base.BenchmarkProvider provider = new DoubleProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Float {
            static Base.BenchmarkProvider provider = new FloatProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Long {
            static Base.BenchmarkProvider provider = new LongProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Reference {
            static Base.BenchmarkProvider provider = new ReferenceProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGet(numIterations, provider.getArrayOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetOpaque(numIterations, provider.getArrayOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetAcquire(numIterations, provider.getArrayOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressGetVolatile(numIterations, provider.getArrayOffsets());
                    }
                }
            }
        }
    }

    public static class Put {
        public static class Byte {
            static Base.BenchmarkProvider provider = new ByteProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Char {
            static Base.BenchmarkProvider provider = new CharProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Short {
            static Base.BenchmarkProvider provider = new ShortProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Int {
            static Base.BenchmarkProvider provider = new IntProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Boolean {
            static Base.BenchmarkProvider provider = new BooleanProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Double {
            static Base.BenchmarkProvider provider = new DoubleProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Float {
            static Base.BenchmarkProvider provider = new FloatProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Long {
            static Base.BenchmarkProvider provider = new LongProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getNativeOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }

                public static class Native extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getNativeOffsets());
                    }
                }
            }
        }

        public static class Reference {
            static Base.BenchmarkProvider provider = new ReferenceProvider();
            public static class Plain {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPut(numIterations, provider.getArrayOffsets());
                    }
                }
            }

            public static class Opaque {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutOpaque(numIterations, provider.getArrayOffsets());
                    }
                }
            }

            public static class Acquire {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutRelease(numIterations, provider.getArrayOffsets());
                    }
                }
            }

            public static class Volatile {
                public static class ObjectMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getObjectOffsets());
                    }
                }

                public static class StaticMember extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getStaticOffsets());
                    }
                }

                public static class Array extends MicroBench {
                    protected long doBatch(long numIterations) {
                       return provider.stressPutVolatile(numIterations, provider.getArrayOffsets());
                    }
                }
            }
        }
    }
}
