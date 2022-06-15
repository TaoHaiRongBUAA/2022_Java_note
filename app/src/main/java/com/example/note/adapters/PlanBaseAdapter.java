package com.example.note.adapters;

import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.Plan;
import com.example.note.R;

import java.util.ArrayList;
import java.util.List;

// Adapter的基类
public abstract class PlanBaseAdapter extends RecyclerView.Adapter<PlanBaseAdapter.InnerHolder>
    implements View.OnClickListener, ItemTouchCallBack.OnItemTouchListener, Filterable
{

    LayoutInflater inflater;
    List<Plan> allPlans ;
    MyFilter mFilter;
    List<Plan> backList;


    public void setAllPlans(List<Plan> allPlans){
        this.allPlans = allPlans;
    }

    public PlanBaseAdapter(LayoutInflater inf, List<Plan> arry){
        this.inflater=inf;
        this.allPlans=arry;
        backList = arry;
        Log.d("on create", "PlanBaseAdapter: backlist = :" + backList.toString());
    }

    // 获得ViewHolder
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getSubView(parent, viewType);
        recyclerView = (RecyclerView) parent;
        return new InnerHolder(view);
    }

    // 返回具体的列表项layout文件
    protected abstract View getSubView(ViewGroup parent, int viewType);

    // 使用ViewHolder对列表中的列表项元素进行绑定数据 和 时间
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        Plan plan = allPlans.get(position);
        holder.mtv_content.setText(plan.getContent());
        holder.cb_finished.setChecked((plan.getIsFinished() == 1));
        holder.cb_finished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("checkbox", "onCheckedChanged: values = " + plan.toString());
                if (b == true){
                    Log.d("checkbox", "onCheckedChanged: true");
                    holder.mtv_content.setPaintFlags(holder.mtv_content.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else{
                    Log.d("checkbox", "onCheckedChanged: true");
                    holder.mtv_content.setPaintFlags(holder.mtv_content.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
    }

    // 返回列表中的值
    @Override
    public int getItemCount() {
        if(allPlans == null){
            return 0;
        }
        return allPlans.size();
    }

    // 拖动后回调函数
    @Override
    public void onMove(int fromPosition, int toPosition) {
        Log.d("onMove", "onMove: onMove is called! in PlanAdapter " + "\n"
                + "with fromPosition = " + fromPosition + "\n"
                + "toPosition = " + toPosition + "\n"
                + "list size = " + allPlans.size()
                + "list[0] = " + allPlans.get(0).toString() + "\n"
                + "list[1] = " + allPlans.get(1).toString() + "\n"
        );

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
//                Log.d("onMove", "onMove:  swap " + i + " with " + (i+1));
                swapId(allPlans, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
//                Log.d("onMove", "onMove:  swap " + i + " with " + (i-1));
                swapId(allPlans, i, i - 1);
            }
        }

        Log.d("onMove", "onMove: onMove is end! in PlanAdapter");
        notifyItemMoved(fromPosition, toPosition);
    }

    // 交换item 的id ，改变顺序
    private void swapId(List<Plan> allPlans, int i, int i1) {
        Log.d("onMove", "in swap: " + "swap " + i + " with " + i1);
        long id1 = allPlans.get(i).getId();
        long id2 = allPlans.get(i1).getId();
        allPlans.get(i).setId(id2);
        allPlans.get(i1).setId(id1);
    }

    // 侧滑事件回调
    @Override
    public void onSwiped(int position) {

    }


    //自定义接口实现点击事件
    public interface OnRecyclerViewItemClickListener{
        void onItemClick(RecyclerView parent, View view, int position);
        void onItemLongClick(RecyclerView parent, View view, int position);
    }

    private PlanBaseAdapter.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private RecyclerView recyclerView;

    //实现接口，并设置监听器
    public void setOnRecyclerViewItemClickListener(PlanBaseAdapter.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public void onClick(View view) {
        int postion = recyclerView.getChildAdapterPosition(view);
        if(onRecyclerViewItemClickListener != null)
            onRecyclerViewItemClickListener.onItemClick(recyclerView, view, postion);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if(onRecyclerViewItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    onRecyclerViewItemClickListener.onItemClick(recyclerView, view,
                            recyclerView.getChildAdapterPosition(view));
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view){
                    onRecyclerViewItemClickListener.onItemLongClick(recyclerView, view,
                            recyclerView.getChildAdapterPosition(view));
                    return false;
                }
            });
        }
    }



    public class InnerHolder extends RecyclerView.ViewHolder {

        private TextView mtv_content;
        private CheckBox cb_finished;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mtv_content = itemView.findViewById(R.id.plan_content);
            cb_finished = (CheckBox) itemView.findViewById(R.id.plan_checkbox);
        }

    }


    // 过滤器， for search
    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new MyFilter();
        }
        return mFilter;
    }


    class MyFilter extends Filter {
        //在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<Plan> list;
            Log.d("adapter", "performFiltering: charSequence = " + charSequence);
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (Plan plan : backList) {
                    if (plan.getContent().contains(charSequence)) {
                        list.add(plan);
                    }
                }
            }

            Log.d("adapter", "\n key = " + charSequence + "\n"
                    + "allItem: " + allPlans.toString() + "\n"
                    + "list : " + backList.toString() + "\n"
                    + "res:" + list.toString()
            );
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中

            return result;
        }

        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            allPlans = (List<Plan>)filterResults.values;
            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变
            }else {
                Log.d("Adapter", "get search result error!");
            }
        }
    }


}

