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
import java.lang.reflect.WildcardType;
import java.util.Map;

import lombok.NonNull;

import static zone.dragon.reflection.Types.commonSuper;

/**
 * @author Darth Android
 * @date 5/1/2021
 */
public class ReifyTypeVisitor extends ResolveTypeVisitor {

    public ReifyTypeVisitor(@NonNull Map<? extends TypeVariable<?>, Type> typeVariables) {
        super(typeVariables);
    }

    @Override
    public Type visit(WildcardType wildcard) {
        Type type = super.visit(wildcard);
        if (type instanceof WildcardType) {
            type = commonSuper(((WildcardType) type).getUpperBounds());
        }
        return TypeVisitor.visit(type, this);
    }

    @Override
    public Type visit(TypeVariable<?> variable) {
        if (typeVariables.containsKey(variable)) {
            return TypeVisitor.visit(typeVariables.get(variable), this);
        }
        return TypeVisitor.visit(commonSuper(variable.getBounds()), this);
    }
}
