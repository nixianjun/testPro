package com.tudou.ids.dao;

import com.tudou.ids.entity.Itemplaystatus;

public interface ItemplaystatusMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Itemplaystatus record);

    int insertSelective(Itemplaystatus record);

    Itemplaystatus selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Itemplaystatus record);

    int updateByPrimaryKey(Itemplaystatus record);
}