package com.example.note.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.Note;
import com.example.note.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecycleViewBaseAdapter implements View.OnClickListener, Filterable {


    private List<Note> backList;
    private MyFilter mFilter;



    public NoteAdapter(LayoutInflater inf, List<Note> arry) {
        super(inf, arry);
        backList = arry;
    }

    @Override
    protected View getSubView(ViewGroup parent, int viewType) {
        return View.inflate(parent.getContext(), R.layout.note_item_list, null);
    }


    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        recyclerView = (RecyclerView) parent;
        return super.onCreateViewHolder(parent, viewType);
    }

    //自定义接口实现点击事件
    public interface OnRecyclerViewItemClickListener{
        void onItemClick(RecyclerView parent, View view, int position);
        void onItemLongClick(RecyclerView parent, View view, int position);
    }

    private NoteAdapter.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private RecyclerView recyclerView;

    //实现接口，设置监听器

    public void setOnRecyclerViewItemClickListener(NoteAdapter.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
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
            List<Note> list;
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (Note note : backList) {
                    if (note.getContent().contains(charSequence) || note.getTitle().contains(charSequence)) {
                        list.add(note);
                    }
                }

//                Log.d("adapter", "\n key = " + charSequence + "\n"
//                        + "list : " + backList.toString() + "\n"
//                        + "res:" + list.toString()
//                );
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中

            return result;
        }
        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            allNotes = (List<Note>)filterResults.values;
            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变
            }else {
                Log.d("Adapter", "get search result error!");
            }
        }
    }
}
