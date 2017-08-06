# RAdapter
Auto generate ListView or RecyclerView Adapter code
## Step 1
```
compile 'io.github.iamyours:radapter-lib:0.0.2'
annotationProcessor 'io.github.iamyours:radapter-compiler:0.0.2'
```
## Step 2
Create XXXViewHolder for ListView or RecyclerView
```
@BindLayout(R.layout.item_student)\\ for RecyclerView use @BindLayout(value = R.layout.item_student,isRecycler = true)
public class StudentHolder extends BaseViewHolder<Student> {
    @Inject
    CallBack callBack2;

    boolean readOnly;
    @BindView(R.id.textView)//ButterKnife Annotation
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
```
## Step 3
Create BaseViewHolder extends RViewHolder
```
  public abstract class BaseViewHolder<T> extends RViewHolder<T> {
    @Override
    public void setRoot(View root) {
        super.setRoot(root);
        ButterKnife.bind(this,root);//
    }
}
```
## For ListView 
```
public final class StudentRAdapter extends BaseAdapter {
  private Context mContext;

  private List<Student> mData;

  private StudentHolder.CallBack callBack2;

  public StudentRAdapter(Context mContext, List<Student> mData) {
    this.mContext = mContext;
    this.mData = mData;
  }

  @Override
  public int getCount() {
    return mData.size();
  }

  @Override
  public Object getItem(int position) {
    return mData.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    StudentHolder holder;
    if(convertView==null) {
      holder = new StudentHolder();
      convertView = LayoutInflater.from(mContext).inflate(2130968604, null);
      holder.setRoot(convertView);
      holder.setContext(mContext);
      holder.callBack2 = callBack2;
    }
    else{
       holder = (StudentHolder)convertView.getTag();
    }
    holder.bind(mData.get(position), position);
    return convertView;
  }

  public void setCallBack2(StudentHolder.CallBack callBack2) {
    this.callBack2 = callBack2;
  }
}
```
## for RecyclerView
```
  public final class StudentRAdapter extends RecyclerView.Adapter<StudentRAdapter.ViewHolder> {
  private Context mContext;

  private List<Student> mData;

  private StudentHolder.CallBack callBack2;

  public StudentRAdapter(Context mContext, List<Student> mData) {
    this.mContext = mContext;
    this.mData = mData;
  }

  @Override
  public StudentRAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(mContext).inflate(2130968604, parent,false);
    ViewHolder holder = new ViewHolder(v);
    holder.mHolder.setContext(mContext);
    holder.mHolder.callBack2 = callBack2;
    return holder;
  }

  @Override
  public void onBindViewHolder(StudentRAdapter.ViewHolder holder, int position) {
    holder.mHolder.bind(mData.get(position),position);;
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public void setCallBack2(StudentHolder.CallBack callBack2) {
    this.callBack2 = callBack2;
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {
    StudentHolder mHolder;

    public ViewHolder(View view) {
      super(view);
      mHolder = new StudentHolder();
      mHolder.setRoot(view);
    }
  }
}
```

