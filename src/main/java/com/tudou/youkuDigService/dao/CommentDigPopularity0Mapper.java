package com.tudou.youkuDigService.dao;

import com.tudou.youkuDigService.entity.CommentDigPopularity0;

public interface CommentDigPopularity0Mapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommentDigPopularity0 record);

    int insertSelective(CommentDigPopularity0 record);

    CommentDigPopularity0 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommentDigPopularity0 record);

    int updateByPrimaryKey(CommentDigPopularity0 record);
}