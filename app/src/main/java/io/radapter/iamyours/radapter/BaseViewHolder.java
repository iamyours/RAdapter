package io.radapter.iamyours.radapter;

import android.view.View;

import butterknife.ButterKnife;
import io.github.iamyours.adapter.RViewHolder;

/**
 * Created by yanxx on 2017/7/31.
 */

public abstract class BaseViewHolder<T> extends RViewHolder<T> {
    @Override
    public void setRoot(View root) {
        super.setRoot(root);
        ButterKnife.bind(this,root);
    }
}
