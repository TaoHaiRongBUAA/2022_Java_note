package com.example.note;



public class Item {
    long id;
    String content;

    public Item() {
    }

    public Item(String content) {
        this.content = content;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
