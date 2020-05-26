package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author liuchenxi
 * @email liuchenxi938@gmail.com
 * @date 2020-05-06 13:01:20
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    MemberLevelEntity getDefaultLevel();

}
