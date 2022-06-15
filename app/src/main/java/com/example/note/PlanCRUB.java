package com.example.note;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// plan 的增删改查操作

public class PlanCRUB {
    final String TAG = "PlanCRUB";
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    private static final String[] columns = {
            PlanDatabase.ID,
            PlanDatabase.CONTENT,
            PlanDatabase.ISFINISHED,
    };

    public PlanCRUB(Context context){
        dbHandler = new PlanDatabase(context);
    }

    public void open(){
        db = dbHandler.getWritableDatabase();
    }

    public void close(){
        dbHandler.close();
    }

    public Plan addPlan(Plan plan) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlanDatabase.CONTENT, plan.getContent());
        contentValues.put(PlanDatabase.ISFINISHED, plan.getIsFinished());
        long insertId = db.insert(PlanDatabase.TABLE_NAME, null, contentValues);
        plan.setId(insertId);
        return plan;
    }

    public Plan getPlan(long id){
        Cursor cursor = db.query(PlanDatabase.TABLE_NAME, columns, PlanDatabase.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Plan tmp = new Plan(cursor.getString(1), cursor.getInt(2));
        tmp.setId(id);
        return tmp;
    }

    @SuppressLint("Range")
    public List<Plan> getAllPlans(){
//        Log.d(TAG, "in getAllPlans");

        Cursor cursor = db.query(PlanDatabase.TABLE_NAME, columns,
                null, null, null, null, null);

        List<Plan> plans = new ArrayList<>();

//        Log.d(TAG, "query over! with count" + cursor.getCount());

        if (cursor.getCount() > 0){
            while(cursor.moveToNext()){
                Plan plan = new Plan();
                plan.setId(cursor.getLong(cursor.getColumnIndex(PlanDatabase.ID)));
                plan.setContent(cursor.getString(cursor.getColumnIndex(PlanDatabase.CONTENT)));
                plan.setIsFinished(cursor.getInt(cursor.getColumnIndex(PlanDatabase.ISFINISHED)));
                plans.add(plan);
            }
        }
//        Log.d(TAG, "add over!");
        return plans;
    }

    public int updatePlan(Plan plan){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlanDatabase.CONTENT, plan.getContent());
        contentValues.put(PlanDatabase.ISFINISHED, plan.getIsFinished());
    return db.update(PlanDatabase.TABLE_NAME, contentValues,
            PlanDatabase.ID + "=?", new String[]{String.valueOf(plan.getId())});
    }

    public void removePlan(Plan plan){
        db.delete(PlanDatabase.TABLE_NAME, PlanDatabase.ID + "=" + plan.getId(), null);
    }

}
