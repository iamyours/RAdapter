package io.github.iamyours.adapter;

import android.content.Context;
import android.view.View;

/**
 * Created by yanxx on 2017/7/28.
 */

public abstract class RViewHolder<T> {
    public View root;
    protected Context context;

    public void setRoot(View root) {
        this.root = root;
        root.setTag(this);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public abstract void bind(T item, int position);
}
