package com.tanhua.server.service;

import cn.hutool.core.util.RandomUtil;
import com.tanhua.autoconfig.template.EmailTemplate;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.ActionType;
import com.tanhua.model.enums.FreezingRangeType;
import com.tanhua.model.vo.CountsPageVo;
import com.tanhua.model.vo.CountsVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 用户服务
 * @Author: Spike Wong
 * @Date: 2022/8/8
 */
@Service
@Slf4j
public class UserService {

    /*
     * magic value, set PREFIX to make code easier to read
     */
    private final static String REDIS_PRE_FIX_MODIFY = "CHECK_CODE_MODIFY_";
    /*
    default password is 123456
     */
    @Autowired
    private EmailTemplate emailTemplate;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VisitorsApi visitorsApi;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference
    private UserApi userApi;

    @DubboReference
    private FriendApi friendApi;
    @DubboReference
    private UserLikeApi userLikeApi;
    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Autowired
    private UserFreezeService userFreezeService;
    @Autowired
    private MqMessageService messageService;


    /**
     * send code to phone 发送验证码到手机
     *
     * @param phoneNumber
     */
    public void sentMsgToPhone(String phoneNumber) {
        User user = userApi.findByMobile(phoneNumber);
        //如果不为空，判断用户状态
        if (!Objects.isNull(user)) {
            //和状态1进行比较
            userFreezeService.checkUserStatus(FreezingRangeType.BAN_LOGOIN.getType(), user.getId());
        }
        //generate random number ,length =6
        String code = RandomUtil.randomNumbers(6);
        log.info("verificationCode:{}", code);
        //TODO 这里我用111111写死来代替，以后再改回来,上面log会显示验证码
        code = "111111";
        //send
//        smsTemplate.sendSms(phoneNumber, code);
        //save into redis with duration time - 5 mins
        stringRedisTemplate.opsForValue().set(Constants.SMS_CODE + phoneNumber, code, 5, TimeUnit.MINUTES);
//        stringRedisTemplate.opsForValue().set(Constants.SMS_CODE + phoneNumber, code, Duration.ofMinutes(5));  另外一种表达
        //save code into redis
    }

    /**
     * send code to email,same as above phone
     *
     * @param email
     */
    public void sentMsgToEmail(String email) {
        //generate random number ,length =6
        String code = RandomUtil.randomNumbers(6);
        try {
            emailTemplate.sendCode(email, code);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //save code into redis
        stringRedisTemplate.opsForValue().set(Constants.EMAIL_CODE + email, code, 5, TimeUnit.MINUTES);

    }


    /**
     * verify if code of phone meets the condition  对手机校验验回填的验证码
     *
     * @param phone
     * @param code
     * @return
     */
    public Map loginVerification(String phone, String code) {
        //get corrective code from redis with right key 查看redis里是否存在值
        String codeOfRedis = stringRedisTemplate.opsForValue().get(Constants.SMS_CODE + phone);
        //judge the condition,make sure the code exit and 2 codes are same，判断，若为空，则抛异常
        if (StringUtils.isEmpty(codeOfRedis) || !code.equals(codeOfRedis)) {
            // throw the exception if it doesn't meet the requirement
            throw new BusinessException(ErrorResult.loginError());
            //throw new RuntimeException("验证码错误");
        }
        //then remove the code from redis by key 若符合条件（即相等），则删除redis的值
        stringRedisTemplate.delete(Constants.SMS_CODE + phone);
        //call dubbo service to findy Mobile
        User user = userApi.findByMobile(phone);
        //judge the user is new or old, default we set it as false then to deal with
        Boolean isNew = false;
        //define action type 定义动作类型
        String actionType = ActionType.LOGIN.getType();
        if (Objects.isNull(user)) {
            //null=new need to register 为空，则需要注册
            actionType = ActionType.REGISTER.getType();
            user = new User();
            user.setMobile(phone);
            //user.setCreated(new Date());
            //user.setUpdated(new Date());
            user.setPassword(DigestUtils.md5Hex(Constants.INIT_PASSWORD.getBytes()));
            //user.setPassword(org.springframework.util.DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD.getBytes()));
            //save the info of new user and return user ID
            Long id = userApi.save(user);
            user.setId(id);
            isNew = true;
            //register huanxin user 注册环信用户
            //noted that hx should be added
            String hxUserName = Constants.HUANXIN_PREFIX + user.getId();
            //初始密码为123456
            Boolean create = huanXinTemplate.createUser(hxUserName, Constants.INIT_PASSWORD);
            if (create) {
                user.setHxUser(hxUserName);
                user.setHxPassword(Constants.INIT_PASSWORD);
                //更新huanxin信息
                userApi.updateWithHuanXinInfo(user);
            }
        }
        //发送队列信息
        messageService.sendLogService(user.getId(), actionType, Constants.QUEUE_KEY_USER, null);
        //generate token with id & email through jwts 创建hashmap寸如信息 4个
        Map tokenMap = new HashMap();
        tokenMap.put("id", user.getId());
        tokenMap.put("phone", user.getMobile());
        //生成token
        String token = JwtUtils.getToken(tokenMap);
        log.info("Token:{}", token);
        //return map after deal with the logic
        Map retMap = new HashMap();
        retMap.put("token", token);
        retMap.put("isNew", isNew);
        return retMap;
    }


    public Map loginVerificationForEmail(String email, String code) {
        String codeOfRedis = stringRedisTemplate.opsForValue().get(Constants.EMAIL_CODE + email);
        if (StringUtils.isEmpty(codeOfRedis) || !code.equals(codeOfRedis)) {
            throw new RuntimeException("验证码错误");
        }
        stringRedisTemplate.delete(Constants.EMAIL_CODE + email);
        User user = userApi.findByEmail(email);
        Boolean isNew = false;
        //define action type 定义动作类型
        String actionType = ActionType.LOGIN.getType();
        if (Objects.isNull(user)) {
            //null=new need to register 为空，则需要注册
            actionType = ActionType.REGISTER.getType();
            user = new User();
            user.setMobile(email);
            //user.setCreated(new Date());
            //user.setUpdated(new Date());
            user.setPassword(DigestUtils.md5Hex(Constants.INIT_PASSWORD.getBytes()));
            //user.setPassword(org.springframework.util.DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD.getBytes()));
            //save the info of new user and return user ID
            Long id = userApi.save(user);
            user.setId(id);
            isNew = true;
            //register huanxin user 注册环信用户
            //noted that hx should be added
            String hxUserName = Constants.HUANXIN_PREFIX + user.getId();
            //初始密码为123456
            Boolean create = huanXinTemplate.createUser(hxUserName, Constants.INIT_PASSWORD);
            if (create) {
                user.setHxUser(hxUserName);
                user.setHxPassword(Constants.INIT_PASSWORD);
                //更新huanxin信息
                userApi.updateWithHuanXinInfo(user);
            }
        }
        //发送队列信息
        messageService.sendLogService(user.getId(), actionType, Constants.QUEUE_KEY_USER, null);
        //generate token with id & email through jwts 创建hashmap寸如信息 4个
        Map tokenMap = new HashMap();
        tokenMap.put("id", user.getId());
        tokenMap.put("phone", user.getEmail());
        //生成token
        String token = JwtUtils.getToken(tokenMap);
        log.info("Token:{}", token);
        //return map after deal with the logic
        Map retMap = new HashMap();
        retMap.put("token", token);
        retMap.put("isNew", isNew);
        return retMap;
    }


    /**
     * send code,it's for updating phone number;修改手机，发验证码操作
     */
    public void sendVerificationCode() {
        String code = RandomUtil.randomNumbers(6);
        log.info("VerificationCode For Changing Phone Number:{}", code);
        //TODO 这里我用111111写死来代替，以后再改回来 here I use "111111" to replace the ramdom code,just for testing and will roll back after all things done
        code = "111111";
        stringRedisTemplate.opsForValue().set(REDIS_PRE_FIX_MODIFY + UserHolder.getUserId(), code, 5, TimeUnit.MINUTES);
    }

    /**
     * 校验验证码
     *
     * @param code
     * @return
     */
    public Map checkVerificationCode(String code) {
        //从redis中拿出来
        String codeOfRedis = stringRedisTemplate.opsForValue().get(REDIS_PRE_FIX_MODIFY + UserHolder.getUserId());
        if (StringUtils.isEmpty(codeOfRedis) || !code.equals(codeOfRedis)) {
            throw new BusinessException(ErrorResult.loginError());
//            throw new RuntimeException("验证码错误");
        }
        stringRedisTemplate.delete(REDIS_PRE_FIX_MODIFY + UserHolder.getUserId());
        Boolean verification = true;
        Map retMap = new HashMap();
        retMap.put("verification", verification);
        return retMap;
    }


    /**
     * update phone 修改手机号
     *
     * @param phone
     */
    public void updateUphone(String phone) {
        log.info("修改手机号：{},ID:{}", phone, UserHolder.getUserId());
        //get user by phone 根据手机获取用户
        User user = userApi.findByMobile(phone);
        //judge if user exit
        if (!Objects.isNull(user)) {
            throw new BusinessException(ErrorResult.mobileError());
        } else {
            //if this phone is used by nobody,then execute the update method 如果手机号无人使用，执行更新方法
            userApi.updateMobileById(phone, UserHolder.getUserId());
        }
    }

    public CountsVo counts(Long userId) {
        //love each other === becomed friends
//        int eachLoveCount = friendApi.countFriendsById(userId);
        //loveCount=i love 我单方面喜欢的数量

        //换一种方式，相互喜欢
        int loveEachOtherCount = userLikeApi.loveEachOtherCount(userId);

        int loveCount = userLikeApi.loveAloneCount(userId);
        //be followed count 喜欢我，但我不喜欢
        int fanCount = userLikeApi.fanCount(userId);
        return new CountsVo(loveEachOtherCount, loveCount, fanCount);
    }

    public PageResult listUserInfoByType(String type, Integer page, Integer pagesize) {
        //得到当前id
        Long currentUserId = UserHolder.getUserId();
        //判断类型
        //TODO 1 互相关注,2 我关注,3 粉丝,4 谁看过我
        PageResult pr = new PageResult();
        if ("1".equals(type)) {
            log.info("相互关注的列表");
            //相互关注
            pr = userLikeApi.loveEachOtherList(currentUserId, page, pagesize);
        }
        if ("2".equals(type)) {
            log.info("我关注的列表");
            pr = userLikeApi.loveAloneList(currentUserId, page, pagesize);
        }
        if ("3".equals(type)) {
            log.info("我粉丝的列表");
            pr = userLikeApi.fanList(currentUserId, page, pagesize);
        }
        if ("4".equals(type)) {
            log.info("谁看过我的列表--访客列表");
            //TODO 这里还有没有弄，访客做完再弄
            String key = Constants.VISITORS_USER;
            //当前用户
            String hashKey = String.valueOf(currentUserId);
            //记录查看时间
            stringRedisTemplate.opsForHash().put(key, hashKey, String.valueOf(System.currentTimeMillis()));
            pr = visitorsApi.visitorsList(currentUserId, page, pagesize);
        }
        List<Long> ids = (List<Long>) pr.getItems();
        if (Collections.isEmpty(ids)) {
            return new PageResult();
        }
        Map<Long, UserInfo> byIdsMap = userInfoApi.findByIds(ids, new UserInfo());
        log.info("得到的Map集合为:{}", byIdsMap);
        List<CountsPageVo> vos = new ArrayList<>();
        for (Long id : ids) {
            if (!Objects.isNull(id)) {
                UserInfo userInfo = byIdsMap.get(id);
                CountsPageVo vo = CountsPageVo.init(userInfo);
                String redisKey = Constants.USER_LIKE_KEY + currentUserId;
                //redis 判断
                if (stringRedisTemplate.opsForSet().isMember(redisKey, id.toString())) {
                    //存在 则设备true
                    log.info("喜欢，AlreadyLove设为true");
                    vo.setAlreadyLove(true);
                }
                vos.add(vo);
            }
        }
        pr.setItems(vos);
        return pr;
    }


    /**
     * 是否喜欢
     *
     * @param uid
     * @return
     */
    public Boolean alreadyLove(String uid) {
        Boolean alreadyLove = userLikeApi.alreadyLove(UserHolder.getUserId(), uid);
        log.info("alreadyLove--:{}", alreadyLove);
        return alreadyLove;
    }

    /**
     * 喜欢 - 取消
     *
     * @param userId
     */
    public void likeDeleted(Long userId) {
        Long currentUserId = UserHolder.getUserId();
        //redis里删除setKey
        stringRedisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY + currentUserId, userId.toString());
        //mongo里面删除user_like关系（单向好友关系，不需要）
        userLikeApi.removeLike(currentUserId, userId);
    }


    /**
     * 粉丝 - 喜欢，就会成为朋友了
     *保存三个数据，redis 关系，userlike关系，好友表关系
     *
     * @param userId
     */
    public void fansLike(Long userId) {
        Long currentUserId = UserHolder.getUserId();
        //redis 里设置喜欢
        stringRedisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + currentUserId, userId.toString());
        stringRedisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + userId, currentUserId.toString());

        //mongo userLike 相互保存 里设置 关系保存
        userLikeApi.saveOrUpdate(currentUserId, userId, true);
        userLikeApi.saveOrUpdate(userId, currentUserId, true);

        //朋友也相互保存
        friendApi.saveRelationship(userId,currentUserId);
    }
}
