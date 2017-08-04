package io.github.iamyours.radapter;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Created by yanxx on 2017/8/2.
 */

public class Types {
    public static final TypeName ANDROID_CONTEXT = ClassName.bestGuess("android.content.Context");
    public static final TypeName ANDROID_VIEW = ClassName.bestGuess("android.view.View");
    public static final TypeName ANDROID_VIEW_GROUP = ClassName.bestGuess("android.view.ViewGroup");
    public static final TypeName ANDROID_LAYOUT_INFLATER = ClassName.bestGuess("android.view.LayoutInflater");
    public static final TypeName ANDROID_RECYCLERVIEW_VIEW_HOLDER = ClassName.bestGuess("android.support.v7.widget.RecyclerView.ViewHolder");
    public static final ClassName ANDROID_RECYCLERVIEW_ADAPTER = ClassName.bestGuess("android.support.v7.widget.RecyclerView.Adapter");
}
