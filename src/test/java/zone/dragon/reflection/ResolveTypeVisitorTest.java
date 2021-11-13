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

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Bryan Harclerode
 * @date 8/28/2021
 */
@DisplayName("ResolveTypeVisitor")
public class ResolveTypeVisitorTest {

    @Nested
    @DisplayName("resolveType(Type, Map<TypeVariable<?>,Type>)")
    class ResolveType {

        @Test
        @DisplayName("with null")
        void withNull() {
            ResolveTypeVisitor visitor = new ResolveTypeVisitor(Collections.emptyMap());
            //
            assertThatThrownBy(() -> visitor.visit((Class<?>) null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("with no type parameters")
        void withNoTypeParameters() {

        }

        @Test
        @DisplayName("with no type parameters resolved")
        void withNoResolvedTypeParameters() {

        }

        @Test
        @DisplayName("with some type parameters resolved")
        void withSomeResolvedTypeParameters() {

        }

        @Test
        @DisplayName("with all type parameters resolved")
        void withAllResolvedTypeParameters() {
            Map<TypeVariable<?>, Type> resolvedVariables = new HashMap<>();
            resolvedVariables.put(Map.class.getTypeParameters()[0], String.class);
            resolvedVariables.put(Map.class.getTypeParameters()[1], Integer.class);
            ResolveTypeVisitor visitor = new ResolveTypeVisitor(resolvedVariables);
            //
            assertThat(visitor.visit(Map.class)).isEqualTo(Types.parameterized(Map.class, String.class, Integer.class));
        }

    }
}
