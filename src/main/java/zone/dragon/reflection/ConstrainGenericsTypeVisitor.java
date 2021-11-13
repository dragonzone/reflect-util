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
import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;

/**
 * @author Darth Android
 * @date 5/7/2021
 */
public class ConstrainGenericsTypeVisitor implements TypeVisitor<Type> {

    protected final Class<?> runtimeType;

    protected final Map<TypeVariable<? extends Class<?>>, Type> typeVariables;

    protected final Map<TypeVariable<? extends Class<?>>, Type> constraints;

    public ConstrainGenericsTypeVisitor(Class<?> runtimeType) {
        this.runtimeType = runtimeType;
        typeVariables = Types.resolveTypeVariables(runtimeType);
        constraints = new HashMap<>();
        for (TypeVariable<? extends Class<?>> typeParameter : runtimeType.getTypeParameters()) {
            constraints.put(typeParameter, typeParameter);
        }
    }

    protected Type rebuildType() {
        TypeVariable<? extends Class<?>>[] typeParameters = runtimeType.getTypeParameters();
        Type[] newTypeParameters = new Type[typeParameters.length];
        for (int i = 0; i < typeParameters.length; i++) {
            newTypeParameters[i] = constraints.get(typeParameters[i]);
        }
        return Types.parameterized(null, runtimeType, newTypeParameters);
    }

    @Override
    public Type visit(@NonNull Class<?> clazz) {
        if (!clazz.isAssignableFrom(runtimeType)) {
            // Runtime type by definition must be the lowest bound on the raw type
            throw new IllegalArgumentException(runtimeType + " is not a subclass of " + clazz);
        }
        return rebuildType();
    }

    @Override
    public Type visit(ParameterizedType parameterized) {
        return null;  //TODO Implement
    }

    @Override
    public Type visit(WildcardType wildcard) {
        return null;  //TODO Implement
    }

    @Override
    public Type visit(GenericArrayType genericArray) {
        return null;  //TODO Implement
    }

    @Override
    public Type visit(TypeVariable<?> variable) {
        return null;  //TODO Implement
    }
}
