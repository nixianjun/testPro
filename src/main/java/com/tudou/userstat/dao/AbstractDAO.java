package com.tudou.userstat.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 数据库处理的基类
 * 
 * @author clin
 * 
 */
public abstract class AbstractDAO {

	@Resource
	protected SqlSessionTemplate writeSqlSession;
	
	@Resource
	protected SqlSessionTemplate tudouSqlSession;
	
	@Resource
	protected SqlSessionTemplate itemChSqlSession;
	
	@Resource
	protected SqlSessionTemplate umsSqlSession;
	
	@Resource
	protected JdbcTemplate itemChJdbcTemplate;
	
	@Resource
	protected JdbcTemplate writeJdbcTemplate;
	
	@Resource
	protected JdbcTemplate subJdbcTemplate;
	
	@Resource
	protected JdbcTemplate tudouJdbcTemplate;
	
	@Resource
	protected JdbcTemplate homeJdbcTemplate;
	
	@Resource
	protected JdbcTemplate userTimeLineJdbcTemplate;
}
