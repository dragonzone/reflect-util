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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Bryan Harclerde
 */
@DisplayName("Types")
public class TypesTest {

    private static class RawTypeFixture<T extends Number> {

        public static final Type PRIMITIVE_TYPE;

        public static final Type CLASS_TYPE;

        public static final Type PARAMETERIZED_TYPE;

        public static final Type GENERIC_ARRAY_TYPE;

        public static final Type TYPE_VARIABLE;

        public static final Type UPPER_BOUND_WILDCARD_TYPE;

        public static final Type LOWER_BOUND_WILDCARD_TYPE;

        static {
            try {
                PRIMITIVE_TYPE = RawTypeFixture.class.getDeclaredField("primitive").getGenericType();
                CLASS_TYPE = RawTypeFixture.class.getDeclaredField("clazz").getGenericType();
                PARAMETERIZED_TYPE = RawTypeFixture.class.getDeclaredField("parameterizedType").getGenericType();
                GENERIC_ARRAY_TYPE = RawTypeFixture.class.getDeclaredField("genericArrayType").getGenericType();
                TYPE_VARIABLE = RawTypeFixture.class.getDeclaredField("typeVariable").getGenericType();
                ParameterizedType wildcardTypes = (ParameterizedType) RawTypeFixture.class
                    .getDeclaredField("wildcardType")
                    .getGenericType();
                UPPER_BOUND_WILDCARD_TYPE = wildcardTypes.getActualTypeArguments()[0];
                LOWER_BOUND_WILDCARD_TYPE = wildcardTypes.getActualTypeArguments()[1];
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Failed to init test fixture", e);
            }
        }

        public T typeVariable;

        int primitive;

        Integer clazz;

        List<Integer> parameterizedType;

        List<Integer>[] genericArrayType;

        Map<? extends String, ? super Integer> wildcardType;

    }

    private static class BoundTypeFixture extends GenericTypeFixture<Long> {


        public static final Type INHERITED_TYPE;

        public static final Type SIMPLE_BOUND_TYPE;

        public static final Type NESTED_BOUND_TYPE;

        private static final Type INHERITED_NESTED_TYPE;

        static {
            try {
                INHERITED_TYPE = BoundTypeFixture.class.getField("inheritedBoundType").getGenericType();
                INHERITED_NESTED_TYPE = BoundTypeFixture.class.getField("inheritedNestedBoundType").getGenericType();
                SIMPLE_BOUND_TYPE = BoundTypeFixture.class.getDeclaredField("simpleBoundType").getGenericType();
                NESTED_BOUND_TYPE = BoundTypeFixture.class.getDeclaredField("nestedBoundType").getGenericType();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Failed to init test fixture", e);
            }
        }

        GenericTypeFixture<Double> simpleBoundType;

        GenericTypeFixture<GenericTypeFixture<Float>> nestedBoundType;


    }

    private static class GenericTypeFixture<T> {
        public T inheritedBoundType;

        public GenericTypeFixture<GenericTypeFixture<T>> inheritedNestedBoundType;
    }

    @Nested
    @DisplayName("resolveType(Type,Class,int)")
    class ResolveReifiedType {


        @Test
        @DisplayName("with bound type")
        void withBoundType() {
            Type result = Types.resolveReifiedType(BoundTypeFixture.class, BoundTypeFixture.SIMPLE_BOUND_TYPE, GenericTypeFixture.class, 0);
            assertThat(result).isEqualTo(Double.class);
        }

        @Test
        @DisplayName("with nested bound type")
        void withNestedBoundType() {
            Type result = Types.resolveReifiedType(BoundTypeFixture.class, BoundTypeFixture.NESTED_BOUND_TYPE, GenericTypeFixture.class, 0);
            assertThat(result).isEqualTo(Types.parameterized(TypesTest.class, GenericTypeFixture.class, Float.class));
        }


        @Test
        void withNonGenericType() {
            Type result = Types.resolveReifiedType(BoundTypeFixture.class, BoundTypeFixture.INHERITED_TYPE, GenericTypeFixture.class, 0);
            assertThat(result).isEqualTo(null);
        }

        @Test
        void withNestedInheritedType() {
            Type result = Types.resolveReifiedType(
                BoundTypeFixture.class,
                BoundTypeFixture.INHERITED_NESTED_TYPE,
                GenericTypeFixture.class,
                0
            );
            assertThat(result).isEqualTo(Types.parameterized(TypesTest.class, GenericTypeFixture.class, Long.class));
        }

    }

    @Nested
    @DisplayName("resolveTypeVariable(Type,Class,int)")
    class ResolveTypeVariable {
        @Test
        @DisplayName("with null boundType")
        void withNullBoundType() {
            Type result = Types.resolveTypeVariable(null, List.class, 0);
            //
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("with null targetType")
        void withNullTargetType() {
            assertThatThrownBy(() -> Types.resolveTypeVariable(String.class, null, 0)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with non-generic targetType")
        void withNonGenericTargetType() {
            assertThatThrownBy(() -> Types.resolveTypeVariable(String.class, String.class, 0)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("with a targetTypeVariableIndex less than 0")
        void withIndexLessThanZero() {
            assertThatThrownBy(() -> Types.resolveTypeVariable(String.class, List.class, -1)).isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("with a targetTypeVariableIndex too large")
        void withIndexOutOfRange() {
            assertThatThrownBy(() -> Types.resolveTypeVariable(String.class, List.class, 1)).isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("with a parameterized type")
        void withParameterizedType() {
            Type result = Types.resolveTypeVariable(Types.parameterized(null, List.class, String.class), List.class, 0);
            //
            assertThat(result).isEqualTo(String.class);
        }

        @Test
        @DisplayName("with a bound subclass")
        void withBoundSubclass() {
            Type result = Types.resolveTypeVariable(BoundTypeFixture.class, GenericTypeFixture.class, 0);
            //
            assertThat(result).isEqualTo(Long.class);
        }
    }

    @Nested
    @DisplayName("resolveTypeVariables(Type)")
    class ResolveTypeVariables {
        @Test
        @DisplayName("with non-generic Type")
        void withNonGenericType() {
            Map<TypeVariable<? extends Class<?>>, Type> typeVariableTypeMap = Types.resolveTypeVariables(Random.class);
            //
            assertThat(typeVariableTypeMap).isEmpty();
        }

        @Test
        @DisplayName("with primitive Type")
        void withPrimitiveType() {
            Map<TypeVariable<? extends Class<?>>, Type> typeVariableTypeMap = Types.resolveTypeVariables(int.class);
            //
            assertThat(typeVariableTypeMap).isEmpty();
        }

        @Test
        @DisplayName("with null")
        void withNull() {
            Map<TypeVariable<? extends Class<?>>, Type> typeVariableTypeMap = Types.resolveTypeVariables(null);
            //
            assertThat(typeVariableTypeMap).isEmpty();
        }

        @Test
        @DisplayName("with ParameterizedType")
        void withParameterizedType() {
            Type type = Types.parameterized(null, RawTypeFixture.class, Integer.class);
            Map<TypeVariable<? extends Class<?>>, Type> typeVariableTypeMap = Types.resolveTypeVariables(type);
            //
            TypeVariable<Class<RawTypeFixture>> typeParameter = RawTypeFixture.class.getTypeParameters()[0];
            assertThat(typeVariableTypeMap).containsExactly(entry(typeParameter, Integer.class));
        }

        @Test
        @DisplayName("with bound supertype")
        void withBoundSupertype() {
            Map<TypeVariable<? extends Class<?>>, Type> typeVariableTypeMap = Types.resolveTypeVariables(BoundTypeFixture.class);
            //
            TypeVariable<Class<GenericTypeFixture>> typeParameter = GenericTypeFixture.class.getTypeParameters()[0];
            assertThat(typeVariableTypeMap).containsExactly(entry(typeParameter, Long.class));
        }
    }

    @Nested
    @DisplayName("parameterized(Type,Type,Type...)")
    class Parameterized {

        @Test
        @DisplayName("with ownerType, type, and type parameters")
        void withEverything() {
            Type ownerType = mock(Type.class);
            Type rawType = mock(Type.class);
            Type[] typeParameters = new Type[0];
            ParameterizedType type = Types.parameterized(ownerType, rawType, typeParameters);
            assertThat(type).isNotNull();
            assertThat(type).hasFieldOrPropertyWithValue("ownerType", ownerType)
                            .hasFieldOrPropertyWithValue("rawType", rawType)
                            .hasFieldOrPropertyWithValue("actualTypeArguments", typeParameters);
        }

        @Test
        @DisplayName("with null ownerType")
        void withNullOwnerType() {
            Type rawType = mock(Type.class);
            Type[] typeParameters = new Type[0];
            ParameterizedType type = Types.parameterized(null, rawType, typeParameters);
            assertThat(type).isNotNull();
            assertThat(type).hasFieldOrPropertyWithValue("ownerType", null)
                            .hasFieldOrPropertyWithValue("rawType", rawType)
                            .hasFieldOrPropertyWithValue("actualTypeArguments", typeParameters);
        }

        @Test
        @DisplayName("with null rawType")
        void withNullRawType() {
            assertThatThrownBy(() -> Types.parameterized(mock(Type.class), null, new Type[0])).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with null typeParameters")
        void withNullTypeParameters() {
            assertThatThrownBy(() -> Types.parameterized(mock(Type.class), mock(Type.class), (Type[]) null)).isInstanceOf(
                NullPointerException.class);
        }

        @Test
        @DisplayName("with null type parameter")
        void withNullTypeParameter() {
            assertThatThrownBy(() -> Types.parameterized(mock(Type.class), mock(Type.class), new Type[1])).isInstanceOf(
                NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("rawType(Type)")
    class RawType {

        @Test
        @DisplayName("with primitive type")
        void withPrimitiveType() {
            assertThat(Types.rawType(RawTypeFixture.PRIMITIVE_TYPE)).isEqualTo(int.class);
        }

        @Test
        @DisplayName("with class type")
        void withClassType() {
            assertThat(Types.rawType(RawTypeFixture.CLASS_TYPE)).isEqualTo(Integer.class);
        }

        @Test
        @DisplayName("with parameterized type")
        void withParameterized() {
            assertThat(Types.rawType(RawTypeFixture.PARAMETERIZED_TYPE)).isEqualTo(List.class);
        }

        @Test
        @DisplayName("with generic array type")
        void withGenericArray() {
            assertThat(Types.rawType(RawTypeFixture.GENERIC_ARRAY_TYPE)).isEqualTo(List[].class);
        }

        @Test
        @DisplayName("with type variable")
        void withTypeVariable() {
            assertThat(Types.rawType(RawTypeFixture.TYPE_VARIABLE)).isEqualTo(Number.class);
        }

        @Test
        @DisplayName("with wildcard with upper bound")
        void withWildcardWithUpperBound() {
            assertThat(Types.rawType(RawTypeFixture.UPPER_BOUND_WILDCARD_TYPE)).isEqualTo(String.class);
        }

        @Test
        @DisplayName("with wildcard with null upper bound")
        void withWildcardWithNullUpperBound() {
            WildcardType type = mock(WildcardType.class);
            when(type.getUpperBounds()).thenReturn(null);
            //
            assertThat(Types.rawType(type)).isEqualTo(Object.class);
        }

        @Test
        @DisplayName("with wildcard with empty upper bound")
        void withWildcardWithEmptyUpperBound() {
            WildcardType type = mock(WildcardType.class);
            when(type.getUpperBounds()).thenReturn(new Type[0]);
            //
            assertThat(Types.rawType(type)).isEqualTo(Object.class);
        }

        @Test
        @DisplayName("with wildcard with lower bound")
        void withWildcardWithLowerBound() {
            assertThat(Types.rawType(RawTypeFixture.LOWER_BOUND_WILDCARD_TYPE)).isEqualTo(Object.class);
        }

        @Test
        @DisplayName("with an unknown Type implementation")
        void withUnknownTypeImplementation() {
            assertThat(Types.rawType(mock(Type.class))).isEqualTo(Object.class);
        }

        @Test
        @DisplayName("with a TypeVariable with no bounds")
        void withTypeVariableWithNoBounds() {
            TypeVariable<?> typeVar = mock(TypeVariable.class);
            when(typeVar.getBounds()).thenReturn(null).thenReturn(new Type[0]);
            assertThat(Types.rawType(typeVar)).isEqualTo(Object.class);
            assertThat(Types.rawType(typeVar)).isEqualTo(Object.class);
        }
    }

    @Nested
    @DisplayName("arrayOf(Type)")
    class ArrayOf {

        @Test
        @DisplayName("with int")
        void intArray() {
            assertThat(Types.arrayOf(int.class)).isEqualTo(int[].class);
        }

        @Test
        @DisplayName("with byte")
        void byteArray() {
            assertThat(Types.arrayOf(byte.class)).isEqualTo(byte[].class);
        }

        @Test
        @DisplayName("with short")
        void shortArray() {
            assertThat(Types.arrayOf(short.class)).isEqualTo(short[].class);
        }

        @Test
        @DisplayName("with long")
        void longArray() {
            assertThat(Types.arrayOf(long.class)).isEqualTo(long[].class);
        }

        @Test
        @DisplayName("with boolean")
        void booleanArray() {
            assertThat(Types.arrayOf(boolean.class)).isEqualTo(boolean[].class);
        }

        @Test
        @DisplayName("with char")
        void charArray() {
            assertThat(Types.arrayOf(char.class)).isEqualTo(char[].class);
        }

        @Test
        @DisplayName("with float")
        void floatArray() {
            assertThat(Types.arrayOf(float.class)).isEqualTo(float[].class);
        }

        @Test
        @DisplayName("with double")
        void doubleArray() {
            assertThat(Types.arrayOf(double.class)).isEqualTo(double[].class);
        }

        @Test
        @DisplayName("with String")
        void stringArray() {
            assertThat(Types.arrayOf(String.class)).isEqualTo(String[].class);
        }

        @Test
        @DisplayName("with other Type")
        void typeArray() {
            Type type = mock(Type.class);
            when(type.toString()).thenReturn("Foo");
            //
            Type result = Types.arrayOf(type);
            //
            assertThat(result).isInstanceOf(GenericArrayType.class).hasFieldOrPropertyWithValue("genericComponentType", type);
            assertThat(result).hasToString("Foo[]");
        }

        @Test
        @DisplayName("with void")
        void voidArray() {
            assertThatThrownBy(() -> Types.arrayOf(void.class)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("with Void")
        void voidClassArray() {
            assertThatThrownBy(() -> Types.arrayOf(Void.class)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("with null")
        void nullComponent() {
            assertThatThrownBy(() -> Types.arrayOf(null)).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("anySuper(Type...)")
    class AnySuper {
        @Test
        @DisplayName("with null bounds array")
        void withNull() {
            assertThatThrownBy(() -> Types.anySuper((Type[]) null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with null bound")
        void withNullBound() {
            assertThatThrownBy(() -> Types.anySuper((Type) null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with a subtype")
        void withSubtype() {
            Type subType = mock(Type.class);
            when(subType.toString()).thenReturn("Foo");
            Type[] subTypes = new Type[]{subType};
            WildcardType wildcardType = Types.anySuper(subTypes);
            assertThat(wildcardType).isNotNull();
            assertThat(wildcardType.getUpperBounds()).isNotNull().isEmpty();
            assertThat(wildcardType.getLowerBounds()).isEqualTo(subTypes);
            assertThat(wildcardType).hasToString("? super Foo");
        }
    }

    @Nested
    @DisplayName("anyExtends(Type...)")
    class AnyExtends {
        @Test
        @DisplayName("with null bounds array")
        void withNull() {
            assertThatThrownBy(() -> Types.anyExtends((Type[]) null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with null bound")
        void withNullBound() {
            assertThatThrownBy(() -> Types.anyExtends((Type) null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with a supertype")
        void withSupertype() {
            Type superType = mock(Type.class);
            when(superType.toString()).thenReturn("Foo");
            Type[] superTypes = new Type[]{superType};
            WildcardType wildcardType = Types.anyExtends(superTypes);
            assertThat(wildcardType).isNotNull();
            assertThat(wildcardType.getLowerBounds()).isNotNull().isEmpty();
            assertThat(wildcardType.getUpperBounds()).isEqualTo(superTypes);
            assertThat(wildcardType).hasToString("? extends Foo");
        }
    }

}
