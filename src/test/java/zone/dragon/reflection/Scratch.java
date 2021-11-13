/*
 * Copyright 2021 Bryan Harclerode
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zone.dragon.reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * @author Darth Android
 * @date 8/21/2021
 */

public class Scratch {

    @Test
    void scratch1() {
        printTypeTree(ArrayList.class, "", true, new HashSet<>());
        System.out.println();
        printTypeTree(LinkedList.class, "", true, new HashSet<>());

    }

    void printTypeTree(Class<?> clazz, String indent, boolean isLast, Set<Class<?>> seen) {
        System.out.print(indent);
        if (!isLast) {
            System.out.print("├╴");
        } else {
            System.out.print("└╴");
        }
        System.out.println(clazz.toString());
        seen.add(clazz);
        List<Class<?>> classes = new LinkedList<>(Arrays.asList(clazz.getInterfaces()));
        if (clazz.getSuperclass() != null) {
            classes.add(0, clazz.getSuperclass());
        }
        classes.removeAll(seen);
        seen.addAll(classes);
        String subIndent = indent + (isLast ? "  " : "│ ");
        for (int i = 0; i < classes.size(); i++) {
            Class<?> nestedClass = classes.get(i);
            if (i < classes.size() - 1) {
                printTypeTree(nestedClass, subIndent, false, seen);
            } else {
                printTypeTree(nestedClass, subIndent, true, seen);
            }
        }
    }
}
