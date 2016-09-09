package com.tudou.youkuDigService.dao;

import com.tudou.youkuDigService.entity.UserCommentOperate0;

public interface UserCommentOperate0Mapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserCommentOperate0 record);

    int insertSelective(UserCommentOperate0 record);

    UserCommentOperate0 selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCommentOperate0 record);

    int updateByPrimaryKey(UserCommentOperate0 record);
}