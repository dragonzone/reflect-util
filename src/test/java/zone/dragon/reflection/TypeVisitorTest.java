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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Darth Android
 * @date 8/21/2021
 */
@DisplayName("TypeVisitor")
public class TypeVisitorTest {

    @Nested
    @DisplayName("visit(Type)")
    class Visit {

        @Test
        @DisplayName("with null type")
        void withNullType() {
            assertThatThrownBy(() -> TypeVisitor.visit(null, mock(TypeVisitor.class))).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with null type visitor")
        void withNullTypeVisitor() {
            assertThatThrownBy(() -> TypeVisitor.visit(mock(Type.class), null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with a class")
        void withClass() {
            TypeVisitor<Object> visitor = mock(TypeVisitor.class);
            Object expectedResult = new Object();
            when(visitor.visit(String.class)).thenReturn(expectedResult);
            //
            Object result = TypeVisitor.visit(String.class, visitor);
            //
            assertThat(result).isSameAs(expectedResult);
            verify(visitor).visit(String.class);
            verifyNoMoreInteractions(visitor);
        }

        @Test
        @DisplayName("with a parameterized type")
        void withParameterizedType() {
            TypeVisitor<Object> visitor = mock(TypeVisitor.class);
            ParameterizedType parameterizedType = mock(ParameterizedType.class);
            Object expectedResult = new Object();
            when(visitor.visit(parameterizedType)).thenReturn(expectedResult);
            //
            Object result = TypeVisitor.visit(parameterizedType, visitor);
            //
            assertThat(result).isSameAs(expectedResult);
            verify(visitor).visit(parameterizedType);
            verifyNoMoreInteractions(visitor);
        }

        @Test
        @DisplayName("with a generic array type")
        void withGenericArray() {
            TypeVisitor<Object> visitor = mock(TypeVisitor.class);
            GenericArrayType genericArrayType = mock(GenericArrayType.class);
            Object expectedResult = new Object();
            when(visitor.visit(genericArrayType)).thenReturn(expectedResult);
            //
            Object result = TypeVisitor.visit(genericArrayType, visitor);
            //
            assertThat(result).isSameAs(expectedResult);
            verify(visitor).visit(genericArrayType);
            verifyNoMoreInteractions(visitor);
        }

        @Test
        @DisplayName("with a wildcard type")
        void withWildcardType() {
            TypeVisitor<Object> visitor = mock(TypeVisitor.class);
            WildcardType wildcardType = mock(WildcardType.class);
            Object expectedResult = new Object();
            when(visitor.visit(wildcardType)).thenReturn(expectedResult);
            //
            Object result = TypeVisitor.visit(wildcardType, visitor);
            //
            assertThat(result).isSameAs(expectedResult);
            verify(visitor).visit(wildcardType);
            verifyNoMoreInteractions(visitor);
        }

        @Test
        @DisplayName("with a type variable")
        void withTypeVariable() {
            TypeVisitor<Object> visitor = mock(TypeVisitor.class);
            TypeVariable<?> typeVariable = mock(TypeVariable.class);
            Object expectedResult = new Object();
            when(visitor.visit(typeVariable)).thenReturn(expectedResult);
            //
            Object result = TypeVisitor.visit(typeVariable, visitor);
            //
            assertThat(result).isSameAs(expectedResult);
            verify(visitor).visit(typeVariable);
            verifyNoMoreInteractions(visitor);
        }

        @Test
        @DisplayName("with an unknown type")
        void withUnknownType() {
            TypeVisitor<Object> visitor = mock(TypeVisitor.class);
            Type type = mock(Type.class);
            //
            assertThatThrownBy(() -> TypeVisitor.visit(type, visitor)).isInstanceOf(IllegalArgumentException.class);
            //
            verifyNoMoreInteractions(visitor);
        }
    }
}
