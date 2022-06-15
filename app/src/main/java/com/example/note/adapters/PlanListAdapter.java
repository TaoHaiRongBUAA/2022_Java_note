package com.example.note.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.note.Plan;
import com.example.note.R;

import java.util.ArrayList;
import java.util.List;

//  线性 的  adapter
public class PlanListAdapter extends PlanBaseAdapter  {

    public PlanListAdapter(LayoutInflater inf, List<Plan> arry) {
        super(inf, arry);
    }

    @Override
    protected View getSubView(ViewGroup parent, int viewType) {
        return View.inflate(parent.getContext(), R.layout.plan_item_list, null);
    }




}
