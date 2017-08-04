package io.github.iamyours.radapter;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * Created by yanxx on 2017/7/31.
 */

public class ListViewAdapterCreator {
    private AdapterData adapterData;

    public ListViewAdapterCreator(AdapterData adapterData) {
        this.adapterData = adapterData;
    }

    public void create(Filer filer) {
        System.out.println("creating " + adapterData.getAdapterName() + "(ListView)...");
        TypeSpec.Builder builder = TypeSpec.classBuilder(adapterData.getAdapterName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        //add fields
        addOverriding(builder);
        addBase(builder);
        addInjectFields(builder);
        JavaFile javaFile = JavaFile.builder(adapterData.getPackageName(), builder.build())
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addInjectFields(TypeSpec.Builder builder) {
        for (FieldBind field : adapterData.getFieldBinds()) {
            String name = field.getName();
            TypeName typeName = field.getTypeName();
            FieldSpec fieldSpec = FieldSpec.builder(typeName, name, Modifier.PRIVATE).build();
            builder.addField(fieldSpec);

            MethodSpec methodSpec = MethodSpec.methodBuilder("set" + StringUtil.captureName(name))
                    .addParameter(typeName, name)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("this.$N = $N", name, name).build();
            builder.addMethod(methodSpec);
        }

    }

    private void addOverriding(TypeSpec.Builder builder) {

        TypeName superType = ClassName.bestGuess("android.widget.BaseAdapter");
        builder.superclass(superType);
        //
        MethodSpec method = MethodSpec.methodBuilder("getCount")
                .addAnnotation(Override.class)
                .returns(int.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return mData.size()")
                .build();
        builder.addMethod(method);

        method = MethodSpec.methodBuilder("getItem")
                .addAnnotation(Override.class)
                .returns(Object.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "position")
                .addStatement("return mData.get(position)")
                .build();
        builder.addMethod(method);

        method = MethodSpec.methodBuilder("getItemId")
                .addAnnotation(Override.class)
                .returns(long.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "position")
                .addStatement("return position")
                .build();
        builder.addMethod(method);


        TypeName viewType = ClassName.bestGuess("android.view.View");
        TypeName viewGroupType = ClassName.bestGuess("android.view.ViewGroup");
        TypeName inflateType = ClassName.bestGuess("android.view.LayoutInflater");
        TypeName viewHolderType = ClassName.bestGuess(adapterData.getHolder());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getView")
                .addAnnotation(Override.class)
                .returns(viewType)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(int.class, "position")
                .addParameter(viewType, "convertView")
                .addParameter(viewGroupType, "parent")
                .addStatement("$T holder", viewHolderType)
                .beginControlFlow("if(convertView==null)")
                .addStatement("holder = new $T()", viewHolderType)
                .addStatement("convertView = $T.from(mContext).inflate(" + adapterData.getLayoutId() + ", null)", inflateType)
                .addStatement("holder.setRoot(convertView)")
                .addStatement("holder.setContext(mContext)");
        //add Inject field
        for (FieldBind field : adapterData.getFieldBinds()) {
            String name = field.getName();
            methodBuilder.addStatement("holder." + name + " = " + name);
        }

        method = methodBuilder.endControlFlow()
                .addCode("else{\n"
                                + "   holder = ($T)convertView.getTag();\n"
                                + "}\n"
                                + "holder.bind(mData.get(position), position);\n"
                        , viewHolderType)
                .addStatement("return convertView")
                .build();
        builder.addMethod(method);

    }

    private void addBase(TypeSpec.Builder builder) {
        //


        //Context
        TypeName contextTypeName = ClassName.bestGuess("android.content.Context");
        FieldSpec contextField = FieldSpec.builder(contextTypeName, "mContext", Modifier.PRIVATE).build();
        System.out.println("contextField:" + contextTypeName);
        builder.addField(contextField);


        ClassName dataType = ClassName.bestGuess(adapterData.getData());
        System.out.println("dataType:" + dataType);
        TypeName dataTypeName = ParameterizedTypeName.get(ClassName.get(List.class), dataType);
        FieldSpec dataField = FieldSpec.builder(dataTypeName, "mData", Modifier.PRIVATE).build();
        builder.addField(dataField);

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextTypeName, "mContext")
                .addParameter(dataTypeName, "mData")
                .addStatement("this.$N = $N", "mContext", "mContext")
                .addStatement("this.$N = $N", "mData", "mData")
                .build();
        builder.addMethod(constructor);

    }

}
