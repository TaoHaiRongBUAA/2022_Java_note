package com.example.note.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.note.Note;
import com.example.note.R;

import java.util.ArrayList;
import java.util.List;
 // 线性的note adapter
public class NoteListAdapter extends NoteBaseAdapter{


    public NoteListAdapter(LayoutInflater inf, List<Note> arry) {
        super(inf, arry);
    }

    @Override
    protected View getSubView(ViewGroup parent, int viewType) {
        return View.inflate(parent.getContext(), R.layout.note_item_list, null);
    }


}
