package com.example.note;


import androidx.annotation.NonNull;

public class Plan extends Item {
    private int isFinished;

    public Plan() {
    }

    public Plan(String content, int isFinished) {
        this.content = content;
        this.isFinished = isFinished;
    }


    public int getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(int isFinished) {
        this.isFinished = isFinished;
    }

    @NonNull
    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isFinished=" + isFinished +
                '}';
    }
}
