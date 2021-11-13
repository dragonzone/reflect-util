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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bryan Harclerode
 * @date 8/22/2021
 */
@DisplayName("RawTypeVisitor")
public class RawTypeVisitorTest {

    @Nested
    @DisplayName("rawType(Type)")
    class RawType {

        @Test
        @DisplayName("with a class")
        void withClass() {
            assertThat(RawTypeVisitor.rawType(ArrayList.class)).isEqualTo(ArrayList.class);
            assertThat(RawTypeVisitor.rawType(List.class)).isEqualTo(List.class);
            assertThat(RawTypeVisitor.rawType(String.class)).isEqualTo(String.class);
            assertThat(RawTypeVisitor.rawType(Integer.class)).isEqualTo(Integer.class);
            assertThat(RawTypeVisitor.rawType(Map.class)).isEqualTo(Map.class);
            assertThat(RawTypeVisitor.rawType(ConcurrentHashMap.class)).isEqualTo(ConcurrentHashMap.class);
        }

        @Test
        @DisplayName("with a parameterized type")
        void withParameterizedType() {
            Type type = Types.parameterized(null, Map.class, String.class, Number.class); // Map<String, Number>
            assertThat(RawTypeVisitor.rawType(type)).isEqualTo(Map.class);
        }

        @Test
        @DisplayName("with a simple wildcard-extends")
        void withSimpleWidcardExtends() {
            Type type = Types.anyExtends(Types.parameterized(null, Map.class, String.class, Number.class)); // ? extends Map<String, Number>
            assertThat(RawTypeVisitor.rawType(type)).isEqualTo(Map.class);
        }

        @Test
        @DisplayName("with a simple wildcard-super")
        void withSimpleWidcardSuper() {
            Type type = Types.anySuper(Types.parameterized(null, Map.class, String.class, Number.class)); // ? super Map<String, Number>
            assertThat(RawTypeVisitor.rawType(type)).isEqualTo(Object.class);
        }

    }
}
