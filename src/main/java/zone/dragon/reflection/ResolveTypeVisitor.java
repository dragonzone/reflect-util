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

import static zone.dragon.reflection.Types.arrayOf;
import static zone.dragon.reflection.Types.rawType;
import static zone.dragon.reflection.Types.wildcard;

/**
 * @author Bryan Harclerode
 * @date 5/1/2021
 */
public class ResolveTypeVisitor implements TypeVisitor<Type> {

    public static Type resolveType(Type originalType, Map<TypeVariable<?>, Type> typeBindings) {
        return TypeVisitor.visit(originalType, new ResolveTypeVisitor(typeBindings));
    }

    protected final Map<? extends TypeVariable<?>, Type> typeVariables;

    public ResolveTypeVisitor(@NonNull Map<? extends TypeVariable<?>, Type> typeVariables) {
        this.typeVariables = new HashMap<>(typeVariables);
    }

    @Override
    public Type visit(Class<?> clazz) {
        if (clazz.getTypeParameters().length == 0) {
            return clazz;
        }
        return visit(Types.parameterized(null, clazz, clazz.getTypeParameters()));
    }

    @Override
    public Type visit(ParameterizedType parameterized) {
        Type[] originalTypeArguments = parameterized.getActualTypeArguments();
        Type[] newTypeArguments = new Type[originalTypeArguments.length];
        for (int i = 0; i < newTypeArguments.length; i++) {
            newTypeArguments[i] = TypeVisitor.visit(originalTypeArguments[i], this);
        }
        return Types.parameterized(parameterized.getOwnerType(), rawType(parameterized.getRawType()), newTypeArguments);
    }

    @Override
    public Type visit(WildcardType wildcard) {
        return wildcard(visitTypes(wildcard.getUpperBounds()), visitTypes(wildcard.getLowerBounds()));
    }

    @Override
    public Type visit(GenericArrayType genericArray) {
        return arrayOf(TypeVisitor.visit(genericArray.getGenericComponentType(), this));
    }

    @Override
    public Type visit(TypeVariable<?> variable) {
        if (typeVariables.containsKey(variable)) {
            // Resolved a variable
            return TypeVisitor.visit(typeVariables.get(variable), this);
        }
        // Couldn't resolve it
        return variable;
    }

    protected Type[] visitTypes(Type[] types) {
        Type[] newTypes = new Type[types.length];
        for (int i = 0; i < newTypes.length; i++) {
            newTypes[i] = TypeVisitor.visit(types[i], this);
        }
        return newTypes;
    }
}
