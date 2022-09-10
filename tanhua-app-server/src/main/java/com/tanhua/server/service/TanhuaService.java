package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.Visitors;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.NearUserVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;

/**
 * @Description: 探花服务层 tanhua service
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */
@Service
@Slf4j
public class TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserLikeApi userLikeApi;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MessageService messageService;

    @DubboReference
    private UserLocationApi userLocationApi;

    //指定默认数据
    @Value("${tanhua.default.recommend.users}")
    private String defaultRecommendUsers;

    @DubboReference
    private VisitorsApi visitorsApi;

    /**
     * get today Best
     * 得到今日佳人
     *
     * @return
     */
    public TodayBest todayBest() {
        //get id firstly ,开始先得到id
        Long userId = UserHolder.getUserId();
        //get recommendUser by userId ,得到推荐用户，today Best with max score
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        //if recommed user is null,default
        if (Objects.isNull(recommendUser)) {
            log.info("都找不到，那就返回吧一个虚拟的吧");
            recommendUser = new RecommendUser();
            recommendUser.setUserId(RandomUtil.randomLong(1, 49));
            recommendUser.setScore(96.3d);
        }
        log.info("今日佳人：{}", recommendUser);
        //get userInfo from id of recommend user,从推荐用户的id中去查询用户信息
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        //TodayBest todayBest = new TodayBest();
        //build object of today best.构建今日家人对象
        //封装思想
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        return vo;
    }


    /**
     * query recommend user via pagebean
     * 通过pagebean查询推荐用户 ，分页查询
     *
     * @param dto
     * @return
     */
    public PageResult recommendation(RecommendUserDto dto) {
        //get list of recommend users from mongo db 从mogondb数据库中获取推荐用户列表集合
        //RecommendUserDto--RecommendUserDto(page=2, pagesize=10, gender=woman, lastLogin=1天, age=30, city=北京城区, education=本科)
        PageResult pr = recommendUserApi.queryRecommendUserList(dto.getPage(), dto.getPagesize(), UserHolder.getUserId());
        log.info("pr:{}", pr);
        //得到items
        List<RecommendUser> items = (List<RecommendUser>) pr.getItems();
        log.info("items:{}", items);
        if (Objects.isNull(items)) {
            //items为空怎么直接返回
            return pr;
        }
        log.info("进行条件判断");
        //if items are not null,get list of ids, and condition to get map
        //如果 items非空，则得到ids的集合
        List<Long> ids = new ArrayList<>();
        //将id都收集起来
        items.forEach(s -> {
            ids.add(s.getUserId());
        });
        //build condition for querying-构造查询条件

        UserInfo userInfoCondition = new UserInfo();
        Integer age = dto.getAge();
        String gender = dto.getGender();
        if (!Objects.isNull(age)) {
            log.info("年龄条件不为空,为 {}", age);
            userInfoCondition.setAge(age);
            //TODO 我这里写死，不然<30 的数据太少了，之后自己随意改
            userInfoCondition.setAge(45);
        }
        if (!Objects.isNull(gender)) {
            log.info("性别条件不为空，为 {}", gender);
            userInfoCondition.setGender(gender);
        }
//        userInfoCondition.setAge(dto.getAge());
//        userInfoCondition.setGender(dto.getGender());
        //get map
        Map<Long, UserInfo> map = null;
        try {
            map = userInfoApi.findByIds(ids, userInfoCondition);
        } catch (Exception e) {

            throw new BusinessException(ErrorResult.builder().errMessage("老色批，你查的太多快了了！！即使心上人儿也需要慢慢驻足欣赏，请晚点再试试！！").build());
        }
        log.info("map:{}", map);
        if (map.isEmpty()) {
            throw new BusinessException(ErrorResult.builder().errMessage("老色批，强撸灰飞烟灭！今天就给你推荐这么多，明天再来吧！！").build());
        }
        // build a list of TodayBest to contain
        List<TodayBest> list = new ArrayList<>();
        for (RecommendUser item : items) {
            Long userId = item.getUserId();
            UserInfo userInfo = map.get(userId);
            if (userInfo != null) {
                TodayBest vo = TodayBest.init(userInfo, item);
                list.add(vo);
            }
        }
        log.info("list:{}", list);
        pr.setItems(list);
        return pr;
    }

    /**
     * todaybest info  佳人信息
     *
     * @param userId 用户id
     * @return
     */
    public TodayBest personalInfo(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        RecommendUser user = recommendUserApi.queryByUserId(userId, UserHolder.getUserId());
        //set visitor  构造访客数据
        Visitors visitors = new Visitors();
        visitors.setUserId(userId);
        visitors.setVisitorUserId(UserHolder.getUserId());
        visitors.setFrom("首页");
        visitors.setDate(System.currentTimeMillis());
        //visitors.setVisitDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        visitors.setVisitDate(new DateTime().toString("yyyyMMdd"));
        visitors.setScore(user.getScore());
        //save visitors
        visitorsApi.saveVisitors(visitors);
        return TodayBest.init(userInfo, user);
    }


    /**
     * get questions for strangers by userId
     * 通过userId查询陌生人信息
     *
     * @param userId
     * @return
     */
    public String strangerQuestions(Long userId) {
        //query by userId to get questions
        Question question = questionApi.findByUserId(userId);
        return question == null ? "Ikun~~一个人我的心应该放在哪里?我喜欢唱跳，Rap，篮球?" : question.getTxt();
    }


    /**
     * reply questions of stranger 回复陌生人问题
     *
     * @param userId
     * @param reply
     */
    public void replyStrangerQuestions(Long userId, String reply) {
        log.info("回复陌生人问题replyStrangerQuestions,userID:{},reply:{}", userId, reply);
        //get current userId 得到当前用户id
        Long currentUserId = UserHolder.getUserId();
        //get current userInfor 得到当前用户的info
        UserInfo userInfo = userInfoApi.findById(currentUserId);
        //set a map to contain the json to come up with 构造map集合的json 提交到环信
        Map map = new HashMap();
        map.put("userId", currentUserId);
        map.put("huanXinId", Constants.HX_USER_PREFIX + currentUserId);
        map.put("nickname", userInfo.getNickname());
        //调用查询到对方的问题
        map.put("strangerQuestion", strangerQuestions(userId));
        map.put("reply", reply);
        //将map弄成JsonString
        String message = JSON.toJSONString(map);
        log.info("JSON-message:{}", message);
        //send message 发送信息
        Boolean sendMsg = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + userId, message);
        if (!sendMsg) {
            //发送失败 则返回false
            throw new BusinessException(ErrorResult.error());
        }
    }

    /**
     * query card list in recommends user
     * 查询推荐列表，探花
     *
     * @return
     */
    public List<TodayBest> queryCardsList() {
        Long currentUserId = UserHolder.getUserId();
        //query to get recommender user from mongo
        //用dubbo远程调用推荐人api得到符合数据的推荐用户列表,指定数量
        List<RecommendUser> recommendUsersList = recommendUserApi.queryCardsList(currentUserId, 10);
        //judge if list is empty, if yes- create a default list
        //判断推荐的list是否为空，如果是，则开始创建默认表
        if (CollUtil.isEmpty(recommendUsersList)) {
            //set a default recommend user list
            //创建一个默认的推荐用户表
            //TODO 这里可以用随机，不用指定也行
            recommendUsersList = new ArrayList<>();
            //split to array by ","
            //以","分割为数组
            String[] defaultUserIds = defaultRecommendUsers.split(",");
            //go through every default id to set up the default recommender user
            //遍历每个默认id去创建默认的推荐用户
            for (String recommendId : defaultUserIds) {
                //new a recommend user
                //创建一个新的用户
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(recommendId));
                recommendUser.setToUserId(currentUserId);
                recommendUser.setScore(RandomUtil.randomDouble(60, 90, 2, RoundingMode.HALF_UP));
                recommendUsersList.add(recommendUser);
            }
        }
        //get userids list via hutool
        //用hutool得到userids的列表
        List<Long> userIds = CollUtil.getFieldValues(recommendUsersList, "userId", Long.class);
        //none condtion
        //无其他条件
        Map<Long, UserInfo> infoMap = userInfoApi.findByIds(userIds, new UserInfo());
        List<TodayBest> tbs = new ArrayList<>();
        for (RecommendUser recommendUser : recommendUsersList) {
            UserInfo userInfo = infoMap.get(recommendUser.getUserId());
            if (!Objects.isNull(userInfo)) {
                TodayBest todayBest = TodayBest.init(userInfo, recommendUser);
                tbs.add(todayBest);
            }
        }
        return tbs;
    }


    /**
     * tanhua like
     * 探花喜欢
     *
     * @param likeUserId
     */
    public void likeUser(Long likeUserId) {
        Long currentUserId = UserHolder.getUserId();
        //use dubbo api to save or update in mongo
        //调用dubbo api服务在mongo里保存或者更新
        Boolean done = userLikeApi.saveOrUpdate(currentUserId, likeUserId, true);
        if (!done) {
            throw new BusinessException(ErrorResult.error());
        }
        //save like in redis,and remove dislike
        //将喜欢保存在redis，并删除不喜欢，更新成功
        stringRedisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + currentUserId, likeUserId.toString());
        stringRedisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + currentUserId, likeUserId.toString());
        //judge if they like each other
        //判断2人是否相互喜欢
        if (isLike(likeUserId, currentUserId)) {
            //if true,then add friend relationship on huanxin and frieds.use messageService.contacts();
            //如果互相喜欢，则将好友关系保存在huanxin和mongo里，调用messageService.contacts方法
            log.info("相互喜欢，那就好友关系表一起做好,环信也能通讯");
            messageService.contacts(likeUserId);
        }


    }


    /**
     * judge  if 2 ids like each other
     * 判断2个id是否互相喜欢  确认1喜欢2
     *
     * @param userId1
     * @param userId2
     * @return
     */
    public Boolean isLike(Long userId1, Long userId2) {
        return stringRedisTemplate.opsForSet().isMember(Constants.USER_LIKE_KEY + userId1, userId2.toString());
    }

    /**
     * unlike 不喜欢
     *
     * @param unlikeUserId
     */
    public void unlikeUser(Long unlikeUserId) {
        Long currentUserId = UserHolder.getUserId();
        //use dubbo api to save or update in mongo
        //调用dubbo api服务在mongo里保存或者更新
        Boolean done = userLikeApi.saveOrUpdate(currentUserId, unlikeUserId, false);
        if (!done) {
            throw new BusinessException(ErrorResult.error());
        }
        //save dislike in redis,and remove like
        //将不喜欢保存在redis，并删除喜欢
        stringRedisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY + currentUserId, unlikeUserId.toString());
        stringRedisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY + currentUserId, unlikeUserId.toString());
//        stringRedisTemplate.opsForSet().size()
        //judge if they like each other
        //判断2人是否相互喜欢
        if (isLike(unlikeUserId, currentUserId)) {
            //TODO 这里的逻辑不太严谨，可能是单方面喜欢，不过删除是没问题,反正不喜欢就删了
            //if true,then remove friend relationship on huanxin and frieds.
            //如果2人互相喜欢过，那么删除2人的好友关系，在mongo和Huanxin里
            messageService.remove(unlikeUserId);
        }
    }

    /**
     * search nearby 搜附近
     *
     * @param gender
     * @param distance
     * @return
     */
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        List<NearUserVo> vos = new ArrayList<>();
        Long currentUserId = UserHolder.getUserId();
        //use dubbo api to get nearby user
        //用dubbo api 查询附近的用户，返回为为所有用户id
        List<Long> nearbyIds = userLocationApi.queryNearUser(currentUserId, distance);
        //judge if the list is empty
        //判断neabyids的list是否为空
        if (CollUtil.isEmpty(nearbyIds)) {
            //if it is empty, just return empty list
            //如果为空，返回空集
            log.info("附近的人为空集，没有附近的人!");
            return Collections.EMPTY_LIST;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setGender(gender);
        //user dubbo api of userInfoApi to get user details
        //调用 dubbo api得到 用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(nearbyIds, userInfo);
        for (Long nearbyId : nearbyIds) {
            //skip adding the current user id
            //跳过添加当前用户id
            if (nearbyId == currentUserId) {
                continue;
            }
            UserInfo info = map.get(nearbyId);
            //judge if info is null,判断如果info为空
            if (!Objects.isNull(info)) {
                NearUserVo vo = NearUserVo.init(info);
                vos.add(vo);
            }
        }
        log.info("NearUserVo附近的人信息:{}", vos);
        return vos;
    }
}
