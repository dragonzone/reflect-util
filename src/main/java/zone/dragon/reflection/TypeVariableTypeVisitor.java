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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static zone.dragon.reflection.Types.rawType;

/**
 *
 * @author Bryan Harclerode
 * @date 5/1/2021
 */
public class TypeVariableTypeVisitor implements TypeVisitor<Map<TypeVariable<? extends Class<?>>, Type>> {

    public static final TypeVariableTypeVisitor INSTANCE = new TypeVariableTypeVisitor();

    @Override
    public Map<TypeVariable<? extends Class<?>>, Type> visit(Class<?> clazz) {
        Map<TypeVariable<? extends Class<?>>, Type> bindings = new HashMap<>();
        if (clazz.getGenericSuperclass() != null) {
            bindings.putAll(TypeVisitor.visit(clazz.getGenericSuperclass(), this));
        }
        for (Type superInterface : clazz.getGenericInterfaces()) {
            bindings.putAll(TypeVisitor.visit(superInterface, this));
        }
        return bindings;
    }

    @Override
    public Map<TypeVariable<? extends Class<?>>, Type> visit(ParameterizedType parameterized) {
        Map<TypeVariable<? extends Class<?>>, Type> bindings = new HashMap<>(parameterized.getActualTypeArguments().length);
        Class<?> rawClass = rawType(parameterized.getRawType());
        TypeVariable<? extends Class<?>>[] typeParameters = rawClass.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            bindings.put(typeParameters[i], parameterized.getActualTypeArguments()[i]);
        }
        return bindings;
    }

    @Override
    public Map<TypeVariable<? extends Class<?>>, Type> visit(WildcardType wildcard) {
        return visitTypes(wildcard.getUpperBounds());
    }

    @Override
    public Map<TypeVariable<? extends Class<?>>, Type> visit(GenericArrayType genericArray) {
        return Collections.emptyMap();
    }

    @Override
    public Map<TypeVariable<? extends Class<?>>, Type> visit(TypeVariable<?> variable) {
        return visitTypes(variable.getBounds());
    }

    protected Map<TypeVariable<? extends Class<?>>, Type> visitTypes(Type[] types) {
        if (types.length == 0) {
            return Collections.emptyMap();
        }
        Map<TypeVariable<? extends Class<?>>, Type> bindings = TypeVisitor.visit(types[0], this);
        for (int i = 1; i < types.length; i++) {
            bindings.putAll(TypeVisitor.visit(types[i], this));
        }
        return bindings;
    }

    protected void simplify(Map<TypeVariable<? extends Class<?>>, Type> bindings) {

    }
}
