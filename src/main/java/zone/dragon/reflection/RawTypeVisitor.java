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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import static zone.dragon.reflection.Types.arrayOf;

/**
 *
 * @author Darth Android
 * @date 5/1/2021
 */
public class RawTypeVisitor implements TypeVisitor<Class<?>> {

    public static final RawTypeVisitor INSTANCE = new RawTypeVisitor();

    public static Class<?> rawType(Type type) {
        return TypeVisitor.visit(type, INSTANCE);
    }

    @Override
    public Class<?> visit(Class<?> clazz) {
        return clazz;
    }

    @Override
    public Class<?> visit(ParameterizedType parameterized) {
        return rawType(parameterized.getRawType());
    }

    @Override
    public Class<?> visit(WildcardType wildcard) {
        Type[] upperBounds = wildcard.getUpperBounds();
        if (upperBounds != null && upperBounds.length > 0) {
            return rawType(upperBounds[0]);
        }
        return Object.class;
    }

    @Override
    public Class<?> visit(GenericArrayType genericArray) {
        return arrayOf(rawType(genericArray.getGenericComponentType()));
    }

    @Override
    public Class<?> visit(TypeVariable<?> variable) {
        Type[] upperBounds = variable.getBounds();
        if (upperBounds != null && upperBounds.length > 0) {
            return rawType(upperBounds[0]);
        }
        return Object.class;
    }
}
