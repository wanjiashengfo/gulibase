package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UsernameExistException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import com.netflix.client.IResponse;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity entity = new MemberEntity();
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUserName());
        entity.setUsername(vo.getUserName());
        entity.setNickname(vo.getUserName());
        entity.setMobile(vo.getPhone());
        //密码要进行加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);
        this.baseMapper.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone)throws PhoneExistException {
        Integer mobile = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(mobile>0){
            throw new PhoneExistException();
        }

    }


    @Override
    public void checkUsernameUnique(String userName)throws UsernameExistException {
        Integer username = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(username>0){
            throw new UsernameExistException();
        }

    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));
        if(entity == null){
            return null;
        }else {
            //获取到数据库password
            String passwordDB = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //密码匹配
            boolean matches = passwordEncoder.matches(password, passwordDB);
            if (matches){
                return entity;
            }else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        String uid = socialUser.getUid();
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity != null){
            //这个用户已经注册
            MemberEntity entity = new MemberEntity();
            entity.setId(memberEntity.getId());
            entity.setAccessToken(socialUser.getAccess_token());
            entity.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(entity);
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        }else {
            //没有查到当前社交用户对应的记录我们就注册一个
            MemberEntity regist = new MemberEntity();
            //查询到当前社交用户的社交平账号信息
            HashMap<String, String> query = new HashMap<>();
            query.put("access_token",socialUser.getAccess_token());
            query.put("uid",socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
            if(response.getStatusLine().getStatusCode()==200){
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                regist.setNickname(name);
                regist.setGender("m".equals(gender)?1:0);
                regist.setSocialUid(socialUser.getUid());
                regist.setAccessToken(socialUser.getAccess_token());
                regist.setExpiresIn(socialUser.getExpires_in());
                this.baseMapper.insert(regist);
            }
            return regist;
        }
    }

}