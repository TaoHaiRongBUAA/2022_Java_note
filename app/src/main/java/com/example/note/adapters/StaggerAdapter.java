package com.example.note.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.note.Note;
import com.example.note.R;

import java.util.List;


public class StaggerAdapter  extends NoteBaseAdapter
        implements View.OnClickListener, ItemTouchCallBack.OnItemTouchListener{

    private static final String TAG = "Adapter";

    public StaggerAdapter(LayoutInflater inf, List<Note> arry) {
        super(inf, arry);
    }


    @Override
    protected View getSubView(ViewGroup parent, int viewType) {
        return View.inflate(parent.getContext(), R.layout.note_item, null);
    }


}
