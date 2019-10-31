/*
 * Copyright 2019 Bryan Harclerode
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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;
import lombok.Value;

/**
 * Utility functions for interacting with the various implementations of {@link Type}, such as {@link ParameterizedType}, {@link
 * GenericArrayType}, {@link WildcardType}, {@link TypeVariable}, and {@link Class}
 *
 * @author Bryan Harclerode
 */
public final class Types {

    /**
     * Checks if a given subtype can be assigned to a field or variable of the given class; This is similar to {@link
     * Class#isAssignableFrom(Class)} except that it can handle generic types and wildcards as the subtype.
     *
     * @param superType
     *     The assignable type
     * @param subType
     *     The type to be assigned
     *
     * @return {@code true} if the {@code subType} can be assigned to a field or variable of type {@code superType}
     *
     * @throws NullPointerException
     *     If {@code superType} or {@code subType} is {@code null}
     */
    public static boolean isAssignableFrom(@NonNull Class<?> superType, @NonNull Type subType) {
        return superType.isAssignableFrom(rawType(subType));
    }

    /**
     * Creates a parameterized type binding for a generic type
     *
     * @param owner
     *     {@code Type} object representing the type that this type is a member of; See {@link ParameterizedType#getOwnerType()}
     * @param rawType
     *     The raw type represented by this parameterized type
     * @param typeArguments
     *     The type bindings for this parameterized type
     *
     * @return A bound representation of a raw type
     *
     * @throws NullPointerException
     *     If {@code rawType} or {@code typeArguments} are {@code null}
     */
    public static ParameterizedType parameterized(Type owner, @NonNull Type rawType, @NonNull Type... typeArguments) {
        for (int i = 0; i < typeArguments.length; i++) {
            if (typeArguments[i] == null) {
                throw new NullPointerException(String.format("typeArguments[%d]", i));
            }
        }
        return new ParameterizedTypeImpl(owner, rawType, typeArguments);
    }

    /**
     * Creates a wildcard supertype, such as {@code ? super Type & Type2 & Type3}
     *
     * @param subTypes
     *     The lower bounds of the wildcard
     *
     * @return A wildcard type with the specified lower bounds
     *
     * @throws NullPointerException
     *     If {@code subTypes} is {@code null}
     */
    public static WildcardType anySuper(@NonNull Type... subTypes) {
        for (int i = 0; i < subTypes.length; i++) {
            if (subTypes[i] == null) {
                throw new NullPointerException(String.format("subTypes[%d]", i));
            }
        }
        return new WildcardTypeImpl(new Type[0], subTypes);
    }


    /**
     * Creates a wildcard subtype, such as {@code ? extends Type & Type2 & Type3}
     *
     * @param superTypes
     *     The upper bounds of the wildcard
     *
     * @return A wildcard type with the specified upper bounds
     *
     * @throws NullPointerException
     *     If {@code superTypes} is {@code null}
     */
    public static WildcardType anyExtends(@NonNull Type... superTypes) {
        for (int i = 0; i < superTypes.length; i++) {
            if (superTypes[i] == null) {
                throw new NullPointerException(String.format("superTypes[%d]", i));
            }
        }
        return new WildcardTypeImpl(superTypes, new Type[0]);
    }

    /**
     * Given a type, returns the corresponding array type with that type as the component
     *
     * @param componentType
     *     Component type of the array
     *
     * @return An array type
     *
     * @throws NullPointerException
     *     If {@code componentType} is {@code null}
     */
    public static Type arrayOf(@NonNull Type componentType) {
        if (componentType == int.class) {
            return int[].class;
        } else if (componentType == byte.class) {
            return byte[].class;
        } else if (componentType == short.class) {
            return short[].class;
        } else if (componentType == long.class) {
            return long[].class;
        } else if (componentType == char.class) {
            return char[].class;
        } else if (componentType == double.class) {
            return double[].class;
        } else if (componentType == float.class) {
            return float[].class;
        } else if (componentType == boolean.class) {
            return boolean[].class;
        } else if (componentType == void.class) {
            throw new IllegalArgumentException("Can't create a void[] array");
        } else if (componentType == Void.class) {
            throw new IllegalArgumentException("Can't create a Void[] array");
        } else if (componentType instanceof Class) {
            return Array.newInstance((Class) componentType, 0).getClass();
        } else {
            return new GenericArrayTypeImpl(componentType);
        }
    }

    /**
     * Given a type, reduces it down to its raw type; This removes all generic information, and reduces variable and wildcard types to their
     * upper bounds.
     *
     * @param type
     *     The type to process
     *
     * @return The raw type for {@code type}
     */
    public static Class<?> rawType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return rawType(((ParameterizedType) type).getRawType());
        } else if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (upperBounds == null || upperBounds.length == 0) {
                return Object.class;
            } else {
                return rawType(upperBounds[0]);
            }
        } else if (type instanceof GenericArrayType) {
            return (Class) arrayOf(rawType(((GenericArrayType) type).getGenericComponentType()));
        } else if (type instanceof TypeVariable) {
            Type[] upperBounds = ((TypeVariable) type).getBounds();
            if (upperBounds == null || upperBounds.length == 0) {
                return Object.class;
            } else {
                return rawType(upperBounds[0]);
            }
        } else {
            return Object.class;
        }
    }

    /**
     * @param contextType
     *     Contextual type in which {@code boundType} is encountered; This will be used to reify {@code boundType} before {@code
     *     targetClass} is resolved.
     * @param boundType
     *     Type that should be reified and resolved
     * @param targetClass
     *     Class to resolve from {@code boundType}
     * @param targetTypeVariableIndex
     *     Index of the type variable from {@code targetClass} to resolve
     *
     * @return A fully resolved type
     *
     * @throws NullPointerException
     *     If {@code targetClass} is {@code null}
     * @throws IllegalArgumentException
     *     If {@code targetClass} is not generic
     * @throws IndexOutOfBoundsException
     *     If {@code targetTypeVariableIndex} is less than 0 or more than the number of type variables in {@code targetClass}
     */
    public static Type resolveReifiedType(Type contextType, Type boundType, @NonNull Class<?> targetClass, int targetTypeVariableIndex) {
        Map<TypeVariable<? extends Class<?>>, Type> typeVariableTypeMap = resolveTypeVariables(contextType);
        Type reifiedType = reifyType(boundType, typeVariableTypeMap);
        return resolveTypeVariable(reifiedType, targetClass, targetTypeVariableIndex);
    }

    /**
     * Reifies all type variables in a given type; Type variables are first looked up in {@code typeVariables}, or reduced to their first
     * upper bound if there is no variable binding in the map. If {@code type} is {@code null}, then {@code null} is returned.
     *
     * @param type
     *     Type to reify
     * @param typeVariables
     *     Map of type variables to actual types
     *
     * @return {@code type}, but with all type variables replaced with actual types; If {@code type} is {@code null}, then {@code null} is
     * returned
     *
     * @throws NullPointerException
     *     If {@code typeVariables} is {@code null}
     */
    public static Type reifyType(Type type, @NonNull Map<? extends TypeVariable<?>, Type> typeVariables) {
        if (type instanceof Class) {
            return type;
        } else if (type instanceof ParameterizedType) {
            Type[] boundTypes = ((ParameterizedType) type).getActualTypeArguments();
            Type[] newBoundTypes = new Type[boundTypes.length];
            for (int i = 0; i < boundTypes.length; i++) {
                newBoundTypes[i] = reifyType(boundTypes[i], typeVariables);
            }
            return parameterized(
                reifyType(((ParameterizedType) type).getOwnerType(), typeVariables),
                reifyType(((ParameterizedType) type).getRawType(), typeVariables),
                newBoundTypes
            );
        } else if (type instanceof TypeVariable) {
            Type resolvedType = typeVariables.get(type);
            if (resolvedType != null) {
                return reifyType(resolvedType, typeVariables);
            } else {
                return reifyType(((TypeVariable) type).getBounds()[0], typeVariables);
            }
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Type newComponentType = reifyType(componentType, typeVariables);
            return arrayOf(newComponentType);
        } else if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (upperBounds != null && upperBounds.length > 0) {
                return reifyType(upperBounds[0], typeVariables);
            } else {
                return Object.class;
            }
        }
        return type;
    }

    /**
     * Generates a map of all bound type variables for {@code type}, its supertypes, and implemented interfaces
     *
     * @param type
     *     The type for which a type variable map should be built
     *
     * @return A map of all discovered type variables that have bindings, or the empty map if no type bindings were found
     */
    public static Map<TypeVariable<? extends Class<?>>, Type> resolveTypeVariables(Type type) {
        Map<TypeVariable<? extends Class<?>>, Type> resolvedTypeVariables = new HashMap<>();
        resolveTypeVariables(type, resolvedTypeVariables);
        return resolvedTypeVariables;
    }

    private static void resolveTypeVariables(Type rootType, @NonNull Map<TypeVariable<? extends Class<?>>, Type> resolvedTypeVariables) {
        List<Type> remainingTypes = new ArrayList<>();
        remainingTypes.add(rootType);
        while (!remainingTypes.isEmpty()) {
            Type type = remainingTypes.remove(0);
            if (type instanceof ParameterizedType) {
                Class<?> rawClass = rawType(type);
                TypeVariable<? extends Class<?>>[] boundVariables = rawClass.getTypeParameters();
                for (int i = 0; i < boundVariables.length; i++) {
                    resolvedTypeVariables.putIfAbsent(boundVariables[i], ((ParameterizedType) type).getActualTypeArguments()[i]);
                }
            } else if (type instanceof Class) {
                remainingTypes.addAll(Arrays.asList(((Class) type).getGenericInterfaces()));
                remainingTypes.add(((Class) type).getGenericSuperclass());
            }
        }
    }

    /**
     * Resolves the bound type variable of a supertype by index; {@code boundType}'s superclasses and implemented interfaces will be
     * searched for {@code targetClass}, and then the {@code targetTypeVariableIndex}-th generic type variable will be resolved to its bound
     * type.
     *
     * @param boundType
     *     Fully bound type
     * @param targetClass
     *     Generic class whose type bindings should be resolved
     * @param targetTypeVariableIndex
     *     Index indicating which of {@code targetClass}'s type variables should be resolved
     *
     * @return The type bound to the {@code targetTypeVariableIndex}-th type variable of {@code targetType} in the context of {@code
     * boundType}; If {@code targetType} is not a supertype of {@code boundType}, then {@code null} is returned.
     *
     * @throws NullPointerException
     *     If {@code targetClass} is {@code null}
     * @throws IllegalArgumentException
     *     If {@code targetClass} is not generic
     * @throws IndexOutOfBoundsException
     *     If {@code targetTypeVariableIndex} is less than 0 or more than the number of type variables in {@code targetClass}
     */
    public static Type resolveTypeVariable(Type boundType, @NonNull Class<?> targetClass, int targetTypeVariableIndex) {
        TypeVariable<? extends Class<?>>[] typeParameters = targetClass.getTypeParameters();
        if (typeParameters == null || typeParameters.length == 0) {
            throw new IllegalArgumentException(targetClass.getName() + " is not a generic class.");
        } else if (targetTypeVariableIndex >= typeParameters.length) {
            throw new IndexOutOfBoundsException("Generic parameter index " + targetTypeVariableIndex + " is invalid for class " + targetClass
                .getName());
        }
        return resolveTypeVariables(boundType).get(typeParameters[targetTypeVariableIndex]);
    }

    @Value
    private static class WildcardTypeImpl implements WildcardType {

        @NonNull
        Type[] upperBounds;

        @NonNull
        Type[] lowerBounds;

        @Override
        public String toString() {
            if (upperBounds.length == 0 && lowerBounds.length == 0) {
                return "?";
            }
            StringBuilder buf = new StringBuilder("?");
            if (upperBounds.length > 0) {
                buf.append(" extends ");
                for (int i = 0; i < upperBounds.length; i++) {
                    buf.append(upperBounds[i].toString());
                    if (i < upperBounds.length - 1) {
                        buf.append(" & ");
                    }
                }
            }
            if (lowerBounds.length > 0) {
                buf.append(" super ");
                for (int i = 0; i < lowerBounds.length; i++) {
                    buf.append(lowerBounds[i].toString());
                    if (i < lowerBounds.length - 1) {
                        buf.append(" & ");
                    }
                }
            }
            return buf.toString();
        }
    }


    @Value
    private static class ParameterizedTypeImpl implements ParameterizedType {

        Type ownerType;

        @NonNull
        Type rawType;

        @NonNull
        Type[] actualTypeArguments;

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            if (ownerType != null) {
                buf.append(ownerType.toString()).append('.');
            }
            buf.append(rawType);
            if (actualTypeArguments.length > 0) {
                buf.append('<');
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    buf.append(actualTypeArguments[i].toString());
                    if (i < actualTypeArguments.length - 1) {
                        buf.append(',');
                    }
                }
                buf.append('>');
            }
            return buf.toString();
        }
    }

    @Value
    private static class GenericArrayTypeImpl implements GenericArrayType {

        @NonNull
        Type genericComponentType;

        @Override
        public String toString() {
            return getGenericComponentType().toString() + "[]";
        }
    }

    // No Instances
    private Types() {}

}
