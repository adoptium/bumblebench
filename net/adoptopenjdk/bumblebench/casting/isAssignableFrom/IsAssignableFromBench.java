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

package net.adoptopenjdk.bumblebench.casting.isAssignableFrom;

import java.util.Random;

import net.adoptopenjdk.bumblebench.core.MicroBench;

public final class IsAssignableFromBench extends MicroBench {

    interface Barks {}
    interface Meows {}
    interface Sheds {}
    interface LaysEggs {}

    class Animal {}
    class Plant {}

    class Mamal extends Animal {}
    class Reptile extends Animal {}
    class Canine extends Mamal {}
    class Feline extends Mamal {}

    class Tree extends Plant {}

    class Carniferous extends Tree {}
    class Deciduous extends Tree {}

    class Maple extends Deciduous {}

    class Dog extends Canine implements Barks {}
    class Cat extends Feline {}
    class Fox extends Canine implements Sheds {}
    class Platypus extends Mamal implements LaysEggs {}

    class Lab extends Dog implements Sheds {}
    class Poodle extends Dog {}
    class Calico extends Cat implements Meows, Sheds {}

    static Class<?> classes[] = {Barks.class, Meows.class, Sheds.class, LaysEggs.class, Animal.class, Plant.class, Mamal.class, Reptile.class, Canine.class, Feline.class, Tree.class, Carniferous.class, Deciduous.class, Mamal.class, 
            Dog.class, Cat.class, Fox.class, Platypus.class, Lab.class, Poodle.class, Calico.class};

    protected long doBatch(long numIterations) throws InterruptedException {
        pauseTimer();
        

        Random r = new Random(1);
        int length = classes.length;
        boolean b = false;
        for (long i = 0; i < numIterations; i++)
        {
            int first = r.nextInt(length);
            int second = r.nextInt(length);
            startTimer();
            b = b ^ classes[first].isAssignableFrom(classes[second]);
            pauseTimer();
        }
        return numIterations;
    }
}

