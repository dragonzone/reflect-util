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

import lombok.NonNull;

/**
 *
 * @author Darth Android
 * @date 5/1/2021
 */
public interface TypeVisitor<T> {

    static <T> T visit(@NonNull Type type, @NonNull TypeVisitor<T> visitor) {
        if (type instanceof Class) {
            return visitor.visit((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return visitor.visit((ParameterizedType) type);
        } else if (type instanceof WildcardType) {
            return visitor.visit((WildcardType) type);
        } else if (type instanceof GenericArrayType) {
            return visitor.visit((GenericArrayType) type);
        } else if (type instanceof TypeVariable) {
            return visitor.visit((TypeVariable<?>) type);
        } else {
            throw new IllegalArgumentException("Don't know how to visit type of " + type.getClass().getName());
        }
    }

    T visit(Class<?> clazz);

    T visit(ParameterizedType parameterized);

    T visit(WildcardType wildcard);

    T visit(GenericArrayType genericArray);

    T visit(TypeVariable<?> variable);

}
