package com.tudou.ids.dao;

import com.tudou.ids.entity.TdItemDelBk;
import com.tudou.ids.entity.TdItemDelBkWithBLOBs;

public interface TdItemDelBkMapper {
    int deleteByPrimaryKey(Integer itemid);

    int insert(TdItemDelBkWithBLOBs record);

    int insertSelective(TdItemDelBkWithBLOBs record);

    TdItemDelBkWithBLOBs selectByPrimaryKey(Integer itemid);

    int updateByPrimaryKeySelective(TdItemDelBkWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TdItemDelBkWithBLOBs record);

    int updateByPrimaryKey(TdItemDelBk record);
}