package com.tudou.ids.entity;

import java.util.Date;

public class Itemplaystatus {
    private Long id;

    private Long itemId;

    private Integer securityStatus;

    private Integer copyrightStatus;

    private Integer policyStatus;

    private Integer playStatus;

    private Date securityUpdateTime;

    private Date copyrightUpdateTime;

    private Date policyUpdateTime;

    private Date playUpdateTime;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getSecurityStatus() {
        return securityStatus;
    }

    public void setSecurityStatus(Integer securityStatus) {
        this.securityStatus = securityStatus;
    }

    public Integer getCopyrightStatus() {
        return copyrightStatus;
    }

    public void setCopyrightStatus(Integer copyrightStatus) {
        this.copyrightStatus = copyrightStatus;
    }

    public Integer getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(Integer policyStatus) {
        this.policyStatus = policyStatus;
    }

    public Integer getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(Integer playStatus) {
        this.playStatus = playStatus;
    }

    public Date getSecurityUpdateTime() {
        return securityUpdateTime;
    }

    public void setSecurityUpdateTime(Date securityUpdateTime) {
        this.securityUpdateTime = securityUpdateTime;
    }

    public Date getCopyrightUpdateTime() {
        return copyrightUpdateTime;
    }

    public void setCopyrightUpdateTime(Date copyrightUpdateTime) {
        this.copyrightUpdateTime = copyrightUpdateTime;
    }

    public Date getPolicyUpdateTime() {
        return policyUpdateTime;
    }

    public void setPolicyUpdateTime(Date policyUpdateTime) {
        this.policyUpdateTime = policyUpdateTime;
    }

    public Date getPlayUpdateTime() {
        return playUpdateTime;
    }

    public void setPlayUpdateTime(Date playUpdateTime) {
        this.playUpdateTime = playUpdateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}