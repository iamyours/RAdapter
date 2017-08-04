package io.github.iamyours.radapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yanxx on 2017/7/31.
 */

public class AdapterData {
    private String holder;
    private String packageName;
    private String adapterName;
    private String data;
    private int layoutId;
    private boolean recycler;//is for RecyclerView

    public boolean isRecycler() {
        return recycler;
    }

    public void setRecycler(boolean recycler) {
        this.recycler = recycler;
    }

    private List<FieldBind> fieldBinds = new ArrayList<>();

    public List<FieldBind> getFieldBinds() {
        return fieldBinds;
    }

    public void setFieldBinds(List<FieldBind> fieldBinds) {
        this.fieldBinds = fieldBinds;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public String toString() {
        return "AdapterData{" +
                "packageName='" + packageName + '\'' +
                ", adapterName='" + adapterName + '\'' +
                ", data=" + data +
                ", layoutId=" + layoutId +
                ",fields:" + Arrays.toString(fieldBinds.toArray()) +
                '}';
    }
}
