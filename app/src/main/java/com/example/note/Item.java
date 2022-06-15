package com.example.note;


/**
 * Note 和 Plan 的基类
 */
public class Item {
    long id;
    String content;

    /**
     * 类的构造方法
     */
    public Item() {
    }

    /**
     * 类的构造方法
     * @param content 内容
     */
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
