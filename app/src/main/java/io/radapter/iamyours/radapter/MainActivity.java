package io.radapter.iamyours.radapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.radapter.iamyours.radapter.holder.StudentHolder;
import io.radapter.iamyours.radapter.holder.StudentRAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ListView listView;
    private List<Student> students = new ArrayList<>();
    private StudentRAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.listView);
        initData();
    }

    private void initData() {
        Student student = new Student();
        student.name = "张三";
        students.add(student);

        student = new Student();
        student.name = "张三2";
        students.add(student);

        student = new Student();
        student.name = "张三3";
        students.add(student);
        adapter = new StudentRAdapter(this, students);
        adapter.setCallBack2(new StudentHolder.CallBack() {
            @Override
            public void call(Student student) {
                Toast.makeText(getApplicationContext(), student.name, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
