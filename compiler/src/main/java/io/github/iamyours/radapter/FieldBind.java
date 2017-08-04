package io.github.iamyours.radapter;

import com.squareup.javapoet.TypeName;

/**
 * Created by yanxx on 2017/7/31.
 */

public final class FieldBind {
    private final String name;
    private final TypeName typeName;

    public FieldBind(String name, TypeName typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return "FieldBind{" +
                "name='" + name + '\'' +
                ", typeName=" + typeName +
                '}';
    }
}
