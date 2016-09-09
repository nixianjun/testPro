package com.tudou.ids.entity;

public class TdItemDelBkWithBLOBs extends TdItemDelBk {
    private String link;

    private String comments;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link == null ? null : link.trim();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }
}