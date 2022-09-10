package com.tanhua.server.task;

import cn.hutool.core.util.RandomUtil;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.mongo.RecommendUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description: 定时推荐任务
 * @Author: Spike Wong
 * @Date: 2022/9/5
 */
@Slf4j
@Component
public class RecommendTask {


    @DubboReference
    private UserApi userApi;

    @DubboReference
    private RecommendUserApi recommendUserApi;


    /**
     * 每日推荐用户定时任务
     */
    @Scheduled(cron = "0 30,55 * * * ?")
    public void recommendUserDaily() {
        log.info("推荐任务开始,清除数据库中------");
        recommendUserApi.removeAll();
        //先找到一共的注册用户，其实这里在登录一瞬间查应该会更好
        List<User> users = userApi.findyAll();
        for (User user : users) {
            Set<RecommendUser> set1 = new HashSet<>();
            Set<Long> set2 = new HashSet<>();
            Long currentUserId = user.getId();
            while (set1.size() < 21) {
                long randomLong = RandomUtil.randomLong(1, 50);
                if (!currentUserId.equals(randomLong) && !set2.contains(randomLong)) {
                    RecommendUser recommendUser = new RecommendUser();
                    recommendUser.setUserId(randomLong);
                    recommendUser.setToUserId(currentUserId);
                    recommendUser.setScore(RandomUtil.randomDouble(50.00, 99.99, 2, RoundingMode.HALF_UP));
                    recommendUser.setDate(new DateTime().toString("YYYY/MM/dd"));
                    set1.add(recommendUser);
                }
            }
            System.out.println(set1);
            recommendUserApi.addBath(set1, RecommendUser.class);
        }
        log.info("推荐用户任务完成~~~");
    }

    public void recommendVideoDaily() {

    }
//        List<RecommendUser> lists = new ArrayList<>();
//        if (!Objects.isNull(list)) {
//            for (User user : list) {
//                if (!Objects.isNull(user)) {
//                    Long toUserId = user.getId();
//                    //插入15条数据
//                    for (int i = 0; i <= 15; i++) {
//                        RecommendUser recommendUser = new RecommendUser();
//                        recommendUser.setUserId(RandomUtil.randomLong(1, 50));
//                        recommendUser.setToUserId(toUserId);
//                        recommendUser.setDate(new DateTime().toString("YYYY/MM/dd"));
//                        recommendUser.setScore(RandomUtil.randomDouble(50.00, 99.99, 2, RoundingMode.HALF_UP));
//                        lists.add(recommendUser);
//                    }
//                }
//            }
//
//        }
//        recommendUserApi.addBath(lists,RecommendUser.class);
//        }

}
