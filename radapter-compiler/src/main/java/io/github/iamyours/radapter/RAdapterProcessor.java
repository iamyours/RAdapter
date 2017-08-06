package io.github.iamyours.radapter;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import io.github.iamyours.radapter.RViewHolder;
import io.github.iamyours.radapter.annotations.BindLayout;
import io.github.iamyours.radapter.annotations.Inject;

/**
 * Created by yanxx on 2017/7/28.
 */
@AutoService(Processor.class)
public class RAdapterProcessor extends AbstractProcessor {
    private Filer filer;
    private Elements elementUtils;
    private Map<String, AdapterData> map = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
        createRViewHolder();
    }

    private void createRViewHolder() {

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindLayout.class)) {
            System.out.println("======BindLayout========" + element);
            map.put(element.toString(), createAdapterData(element));
            System.out.println(elementUtils.getPackageOf(element));
            System.out.println(elementUtils.getAllAnnotationMirrors(element));
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(Inject.class)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            System.out.println("======Inject=========" + element.getClass());
            System.out.println("simpleName:" + element.getSimpleName());
            String name = element.getSimpleName().toString();
            TypeName typeName = TypeName.get(element.asType());
            FieldBind fieldBind = new FieldBind(name, typeName);
            System.out.println(elementUtils.getPackageOf(element));
            System.out.println(typeElement);
            AdapterData adapterData = map.get(typeElement.toString());
            adapterData.getFieldBinds().add(fieldBind);
        }
        System.out.println("---------------start---------------------");
        for (String key : map.keySet()) {
            System.out.println(key + ":");
            System.out.println(map.get(key));
            AdapterData data = map.get(key);
            if (data.isRecycler()) {
                new RecyclerViewAdapterCreator(data).create(filer);
            } else {
                new ListViewAdapterCreator(data).create(filer);
            }
        }
        System.out.println("-------------end---------------");
        return true;
    }

    private AdapterData createAdapterData(Element element) {
        String adapterName = element.getSimpleName().toString().replaceAll("Holder", "") + "RAdapter";
        AdapterData adapterData = new AdapterData();
        adapterData.setPackageName(elementUtils.getPackageOf(element).toString());
        adapterData.setAdapterName(adapterName);
        BindLayout bindLayout = element.getAnnotation(BindLayout.class);
        adapterData.setRecycler(bindLayout.isRecycler());
        adapterData.setLayoutId(bindLayout.value());
        adapterData.setData(getData(element.asType()));
        adapterData.setHolder(element.toString());
        return adapterData;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(BindLayout.class.getCanonicalName());
        annotations.add(Inject.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    static boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (isTypeEqual(typeMirror, otherType)) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private static DeclaredType getSuper(TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return null;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        Element element = declaredType.asElement();
        if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            DeclaredType superType = (DeclaredType) typeElement.getSuperclass();
            return superType;
        }
        return null;
    }

    private static String getData(TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            System.out.println("DECLARED----------");
            return null;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        Element element = declaredType.asElement();
        boolean isTypeElement = element instanceof TypeElement;
        if (!isTypeElement) {
            return null;
        }

        if (!isSubtypeOfType(typeMirror, RViewHolder.class.getName()+"<?>")) {
            System.err.println("invalid class,must extends "+RViewHolder.class.getName());
            return null;
        }
        DeclaredType superType = getSuper(typeMirror);
        if (superType == null) {
            System.out.println("null");
            return null;
        }
        List<? extends TypeMirror> typeArguments = superType.getTypeArguments();
        System.out.println("size:" + typeArguments.size());
        if (typeArguments.size() == 1) {
            return typeArguments.get(0).toString();
        }
        return null;
    }

    private static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
        return otherType.equals(typeMirror.toString());
    }
}
