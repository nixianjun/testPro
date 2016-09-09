package com.tudou.ids.dao;

import com.tudou.ids.entity.CommentDigPopularity0;

public interface CommentDigPopularity0Mapper {
    int deleteByPrimaryKey(Integer id);

	int insert(CommentDigPopularity0 record);

	int insertSelective(CommentDigPopularity0 record);

	CommentDigPopularity0 selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(CommentDigPopularity0 record);

	int updateByPrimaryKey(CommentDigPopularity0 record);

	int deleteByPrimaryKey(Integer id);

    int insert(CommentDigPopularity0 record);

    int insertSelective(CommentDigPopularity0 record);

    CommentDigPopularity0 selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommentDigPopularity0 record);

    int updateByPrimaryKey(CommentDigPopularity0 record);
}