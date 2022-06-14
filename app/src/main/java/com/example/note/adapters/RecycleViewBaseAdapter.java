package com.example.note.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.Note;
import com.example.note.R;

import java.util.List;

// Adapter的基类
public abstract class RecycleViewBaseAdapter extends RecyclerView.Adapter<RecycleViewBaseAdapter.InnerHolder>
    implements View.OnClickListener, ItemTouchCallBack.OnItemTouchListener
{

    LayoutInflater inflater;
    List<Note> allNotes ;

    public void setAllNotes(List<Note> allNotes){
        this.allNotes = allNotes;
    }

    public RecycleViewBaseAdapter(LayoutInflater inf,List<Note> arry){
        this.inflater=inf;
        this.allNotes=arry;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getSubView(parent, viewType);
        recyclerView = (RecyclerView) parent;
        return new InnerHolder(view);
    }

    protected abstract View getSubView(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        Note note = allNotes.get(position);
        holder.mtv_title.setText(note.getTitle());
        holder.mtv_content.setText(note.getContent());
        holder.mtv_time.setText(note.getTime());
    }

    @Override
    public int getItemCount() {
        if(allNotes == null){
            return 0;
        }
        return allNotes.size();
    }


    @Override
    public void onMove(int fromPosition, int toPosition) {
        Log.d("onMove", "onMove: onMove is called!");
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                swapId(allNotes, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                swapId(allNotes, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    private void swapId(List<Note> allNotes, int i, int i1) {
        long id1 = allNotes.get(i).getId();
        long id2 = allNotes.get(i1).getId();
        allNotes.get(i).setId(id2);
        allNotes.get(i+1).setId(id1);
    }

    @Override
    public void onSwiped(int position) {

    }


    //自定义接口实现点击事件
    public interface OnRecyclerViewItemClickListener{
        void onItemClick(RecyclerView parent, View view, int position);
        void onItemLongClick(RecyclerView parent, View view, int position);
    }

    private NoteListAdapter.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private RecyclerView recyclerView;

    //实现接口，设置监听器

    public void setOnRecyclerViewItemClickListener(NoteListAdapter.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
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

        private TextView mtv_title, mtv_content, mtv_time;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mtv_title = itemView.findViewById(R.id.list_title);
            mtv_content  = itemView.findViewById(R.id.list_content);
            mtv_time = itemView.findViewById(R.id.list_time);
        }

    }


}

