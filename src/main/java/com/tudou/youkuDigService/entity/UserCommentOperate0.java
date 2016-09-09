package com.tudou.youkuDigService.entity;

import java.util.Date;

public class UserCommentOperate0 {
    private Long id;

    private Long itemid;

    private Long commentid;

    private Long userid;

    private Boolean optype;

    private Date optime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemid() {
        return itemid;
    }

    public void setItemid(Long itemid) {
        this.itemid = itemid;
    }

    public Long getCommentid() {
        return commentid;
    }

    public void setCommentid(Long commentid) {
        this.commentid = commentid;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Boolean getOptype() {
        return optype;
    }

    public void setOptype(Boolean optype) {
        this.optype = optype;
    }

    public Date getOptime() {
        return optime;
    }

    public void setOptime(Date optime) {
        this.optime = optime;
    }
}