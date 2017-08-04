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
 * Created by yanxx on 2017/8/1.
 */

public class RecyclerViewAdapterCreator {
    private AdapterData adapterData;

    public RecyclerViewAdapterCreator(AdapterData adapterData) {
        this.adapterData = adapterData;
    }

    public void create(Filer filer) {
        System.out.println("creating " + adapterData.getAdapterName() + "(RecyclerView)...");
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


    private void addBase(TypeSpec.Builder builder) {
        //Context
        FieldSpec contextField = FieldSpec.builder(Types.ANDROID_CONTEXT, "mContext", Modifier.PRIVATE).build();
        builder.addField(contextField);
        //List<T>mData
        ClassName dataType = ClassName.bestGuess(adapterData.getData());
        System.out.println("dataType:" + dataType);
        TypeName dataTypeName = ParameterizedTypeName.get(ClassName.get(List.class), dataType);
        FieldSpec dataField = FieldSpec.builder(dataTypeName, "mData", Modifier.PRIVATE).build();
        builder.addField(dataField);

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Types.ANDROID_CONTEXT, "mContext")
                .addParameter(dataTypeName, "mData")
                .addStatement("this.$N = $N", "mContext", "mContext")
                .addStatement("this.$N = $N", "mData", "mData")
                .build();
        builder.addMethod(constructor);
    }

    private void addOverriding(TypeSpec.Builder builder) {
        //create inner class RecyclerView.ViewHolder
        TypeName holderType = ClassName.bestGuess(adapterData.getHolder());
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Types.ANDROID_VIEW, "view")
                .addStatement("super(view)")
                .addStatement("mHolder = new $T()", holderType)
                .addStatement("mHolder.setRoot(view)")
                .build();

        TypeSpec viewHolder = TypeSpec.classBuilder("ViewHolder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addMethod(constructor)
                .addField(holderType, "mHolder")
                .superclass(Types.ANDROID_RECYCLERVIEW_VIEW_HOLDER)
                .build();
        builder.addType(viewHolder);

        TypeName viewHolderType = ClassName.bestGuess(adapterData.getAdapterName() + ".ViewHolder");
        //extends RecyclerView.Adapter<XXX.ViewHolder>
        TypeName recyclerviewAdapter = ParameterizedTypeName.get(Types.ANDROID_RECYCLERVIEW_ADAPTER, viewHolderType);
        builder.superclass(recyclerviewAdapter);

        //public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        MethodSpec.Builder onCreateViewHolder = MethodSpec.methodBuilder("onCreateViewHolder")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Types.ANDROID_VIEW_GROUP, "parent")
                .addParameter(int.class, "viewType")
                .returns(viewHolderType)
                .addStatement("View v = $T.from(mContext).inflate($N, parent,false)", Types.ANDROID_LAYOUT_INFLATER, adapterData.getLayoutId() + "")
                .addStatement("ViewHolder holder = new ViewHolder(v)")
                .addStatement("holder.mHolder.setContext(mContext)");
        //add Inject field
        for (FieldBind field : adapterData.getFieldBinds()) {
            String name = field.getName();
            onCreateViewHolder.addStatement("holder.mHolder." + name + " = " + name);
        }
        onCreateViewHolder.addStatement("return holder");
        builder.addMethod(onCreateViewHolder.build());

        //public void onBindViewHolder(ViewHolder holder, int position)
        MethodSpec method = MethodSpec.methodBuilder("onBindViewHolder")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(viewHolderType, "holder")
                .addParameter(int.class, "position")
                .addStatement("holder.mHolder.bind(mData.get(position),position);")
                .build();
        builder.addMethod(method);
        //public int getItemCount()
        method = MethodSpec.methodBuilder("getItemCount")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(int.class)
                .addStatement("return mData.size()")
                .build();
        builder.addMethod(method);
    }
}
