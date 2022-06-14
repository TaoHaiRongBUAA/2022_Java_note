package com.example.note.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.Note;
import com.example.note.R;

import java.util.ArrayList;
import java.util.List;

// Adapter的基类
public abstract class RecycleViewBaseAdapter extends RecyclerView.Adapter<RecycleViewBaseAdapter.InnerHolder> {

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

