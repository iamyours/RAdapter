package io.radapter.iamyours.radapter.holder;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import io.github.iamyours.adapter.annotations.BindLayout;
import io.github.iamyours.adapter.annotations.Inject;
import io.radapter.iamyours.radapter.BaseViewHolder;
import io.radapter.iamyours.radapter.R;
import io.radapter.iamyours.radapter.Student;


/**
 * Created by yanxx on 2017/7/28.
 */
@BindLayout(value = R.layout.item_student,isRecycler = true)
public class StudentHolder extends BaseViewHolder<Student> {
    @Inject
    CallBack callBack2;

    boolean readOnly;
    @BindView(R.id.textView)
    TextView tv;


    public interface CallBack {
        void call(Student student);
    }

    @Override
    public void bind(final Student item, int position) {
        tv.setText(item.name);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack2 != null) callBack2.call(item);
            }
        });
    }
}
