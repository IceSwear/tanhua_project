package com.tanhua.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mappers.AdminMapper;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import com.tanhua.model.vo.AdminVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 后台管理登录  admin login
     *
     * @param map
     * @return
     */
    public Map login(Map map) {
        log.info("后台登录map:{}", map);
        //校验验证码是否对,从redis取出验证码
        String uuid = (String) map.get("uuid");
        String code = (String) map.get("verificationCode");
        String redisCode = stringRedisTemplate.opsForValue().get(Constants.CAP_CODE + uuid);
        log.info("redis存的验证码:{}", redisCode);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            throw new BusinessException("验证码错误!");
        }
        log.info("验证码存在！且正确~");
        //redis 删除
        stringRedisTemplate.delete(Constants.CAP_CODE + uuid);
        //校验用户名信息是否对
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        password = DigestUtils.md5Hex(password.getBytes());
        QueryWrapper<Admin> qw = new QueryWrapper();
        qw.eq("username", username);
        Admin admin = adminMapper.selectOne(qw);
        Admin admin1 = adminMapper.findByUserName(username);
        log.info("mybatisplus查出的:{}-----手写sql查出的:{}", admin, admin1);
        log.info("查出的admin:{}", admin);
        if (Objects.isNull(admin) || !password.equals(admin.getPassword())) {
            throw new BusinessException("账号或密码错误！！");
        }
        Map tokenMap = new HashMap();
        tokenMap.put("id", admin.getId());
        tokenMap.put("username", admin.getUsername());
        String token = JwtUtils.getToken(tokenMap);
        Map retMap = new HashMap();
        retMap.put("token", token);
        log.info("登录成功~token为:{}", token);
        return retMap;
    }


    /**
     * 管理员详情
     *
     * @return
     */
    public AdminVo profile() {
        Long userId = AdminHolder.getUserId();
        Admin admin = adminMapper.selectById(userId);
        Admin admin1 = adminMapper.findById(userId);
        log.info("mybatisplus查出的:{}-----手写sql查出的:{}", admin, admin1);
        AdminVo vo = AdminVo.init(admin);
        log.info("adminvo对象:{}", vo);
        return vo;
    }


    /**
     * 用户登出
     */
    public void logout() {

    }
}
