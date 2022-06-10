package com.example.note.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.Note;
import com.example.note.R;

import java.util.List;


public class StaggerAdapter  extends RecycleViewBaseAdapter
        implements View.OnClickListener{


    public StaggerAdapter(LayoutInflater inf, List<Note> arry) {
        super(inf, arry);
    }

    @Override
    protected View getSubView(ViewGroup parent, int viewType) {
        return View.inflate(parent.getContext(), R.layout.list_item, null);
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

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private RecyclerView recyclerView;

    //实现接口，设置监听器

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
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




}
