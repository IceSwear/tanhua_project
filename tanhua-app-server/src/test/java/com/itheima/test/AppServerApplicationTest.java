package com.itheima.test;


import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@SpringBootTest(classes = AppServerApplication.class)
public class AppServerApplicationTest {

    @DubboReference
    private UserApi userApi;
    @DubboReference
    private RecommendUserApi recommendUserApi;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void saveAudios() throws FileNotFoundException {

//        File file = new File("/Users/kk/Desktop/探花交友总结/untitled folder");
//        File[] files = file.listFiles();
//        for (File file1 : files) {
//            String name = file1.getName();
//            System.out.println(name);
//
//            String suffixName = name.substring(name.lastIndexOf(".") + 1);
//            long totalSpace = file1.length();
//            System.out.println(totalSpace);
//            FileInputStream fs = new FileInputStream(file1);
//            StorePath storePath = fastFileStorageClient.uploadFile(fs, totalSpace, suffixName, null);
//            String fullPath = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
//            System.out.println(fullPath);
//            String dateMark = DateTime.now().toString("yyyyMMdd/");
        Date date = new Date();
        long time = date.getTime();
        System.out.println(date);
        System.out.println(date.toString());
        System.out.println(time);

    }
//        for (File file1 : files) {
//            String name = file1.getName();
//            long totalSpace = file1.getTotalSpace();
//            String suffixName = name.substring(name.lastIndexOf(".") + 1);
//            FileInputStream fs=new FileInputStream(file1);
//            System.out.println();
//            StorePath storePath = fastFileStorageClient.uploadFile(fs, totalSpace, suffixName, null);
//            String fullPath=fdfsWebServer.getWebServerUrl()+storePath;
//            System.out.println(fullPath);
//        }

}


//
//    @Test
//    public void listQuestions() {
//
//        for (int i = 0; i < 13; i++) {
//            QuestionType qt = new QuestionType();
//            qt.setCover("www.baidu.co");
//            qt.setName("高级灵魂题");
//            qt.setLevel("中级");
//            qt.setStar(2);
//            qt.setReportId(RandomUtil.randomNumbers(8).toString());
//            QuestionCollection qc =new QuestionCollection();
//            qc.setQuestion("食无食有病？？？？");
//            OptionCollection oc =new OptionCollection();
//            oc.setOption("你干嘛");
//            mongoTemplate.save(qt);
//            mongoTemplate.save(qc);
//            mongoTemplate.save(oc);
//        }
//    }

//    @DubboReference
//    BlackListApi blackListApi;
//    @Autowired
//    AliyunOssTemplate aliyunOssTemplate;
//
//    @Autowired
//    AipFaceTemplate aipFaceTemplate;
//
//    @Test
//    public void run() throws IOException {
//        String path = "/Users/kk/Downloads/u=2963452315,398280792&fm=85&app=131&size=f242,150&n=0&f=JPEG&fmt=auto.webp";
//        String upload = aliyunOssTemplate.uploadByPath(path);
//        System.out.println(upload);
//    }

//    @Test
//    public void run1() throws IOException {
//        String url = "https://hsam.oss-cn-shanghai.aliyuncs.com/20211113/362624a200780defb2894b03aa7309bd.jpg";
////        String url ="https://hsam.oss-cn-shanghai.aliyuncs.com/20211113/37afb29ecec82ea1f67606e81d7fb35f.jpg";
//        System.out.println(aipFaceTemplate.detect(url));
//    }


/**
 * 黑名单——分页查询测试
 *
 * @throws java.io.IOException
 * <p>
 * 批量注册环信（记得重启服务）
 * <p>
 * 批量注册环信（记得重启服务）
 * <p>
 * 批量注册环信（记得重启服务）
 * <p>
 * 批量注册环信（记得重启服务）
 * <p>
 * 批量注册环信（记得重启服务）
 * <p>
 * 批量注册环信（记得重启服务）
 */
//    @Test
//    public void run3() throws IOException {
//        IPage<UserInfo> byUserId = blackListApi.findByUserId(106L, 2, 2);
//        System.out.println(byUserId.getRecords());
//    }

//    @DubboReference
//    UserInfoApi userInfoApi;
//
//    @Test
//    public void run4() {
//        List ids = new ArrayList();
//        ids.add(1l);
//        ids.add(2l);
//        ids.add(3l);
//        ids.add(4l);
//        Map byIds = userInfoApi.findByIds(ids, null);
//        byIds.forEach((k, v) -> System.out.println(k + "----" + v));
//
//    }

//    @Autowired
//    private HuanXinTemplate huanXinTemplate;
//
//    @Test
//    public void testHuanxin(){
//        huanXinTemplate.createUser("test007","123456");
//    }
//    @Test
//    public void testreplace() {
//
//        String str1 = "Aoc.Iop.Aoc.Iop.Aoc";        //定义三个一样的字符串
//        String str2 = "Aoc.Iop.Aoc.Iop.Aoc";
//        String str3 = "Aoc.Iop.Aoc.Iop.Aoc";
//
//        String str11 = str1.replace(".", "#");
//        String str22 = str2.replaceAll(".", "#");
//        String str33 = str3.replaceFirst(".", "#");
//        System.out.println(str11);
//        System.out.println(str22);
//        System.out.println(str33);
//    }

//    @Autowired
//    private BaiduGreenTemplate baiduGreenTemplate;
//
//    @Test
//    public void textTest() throws IOException {
//        String txt = baiduGreenTemplate.checkImage("http://images.china.cn/site1000/2018-03/17/dfd4002e-f965-4e7c-9e04-6b72c601d952.jpg");
//        System.out.println(txt);
//    }
//    @DubboReference
//    UserApi userApi;
//    @Test
//    public void getById(){
//        User byId = userApi.getById(106L);
//        System.out.println(byId);
//    }
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    @Test
//    public void test() {
//        for (int i = 1; i < 91; i++) {
//            for (int j = 0; j < 11; j++) {
//                RecommendUser recommendUser = new RecommendUser();
//                recommendUser.setUserId(RandomUtil.randomLong(1, 50));
//                recommendUser.setToUserId(Long.valueOf(i));
//                recommendUser.setDate(new DateTime().toString("YYYY/MM/dd"));
//                recommendUser.setScore(RandomUtil.randomDouble(50.00, 99.99, 2, RoundingMode.HALF_UP));
//                System.out.println(recommendUser.toString());
//                RecommendUser save = mongoTemplate.save(recommendUser);
//            }
//        }
//    }


//    @Test
//    public void removeAll() {
//
//    }


/**
 * 批量注册环信（记得重启服务）
 */
//    @Test
//    public void huanXinRegister() {
//        List<User> users = userApi.findyAll();
//        if (!CollUtil.isEmpty(users)) {
//            for (User user : users) {
//                if (!Objects.isNull(user) && user.getId() < 107) {
//                    System.out.println(user);
//                    Long id = user.getId();
//                    Boolean success = huanXinTemplate.createUser(Constants.HUANXIN_PREFIX + id, Constants.INIT_PASSWORD);
//                    if (success){
//                        User updateUser=new User();
//                        updateUser.setHxUser(Constants.HUANXIN_PREFIX + id);
//                        updateUser.setHxPassword(Constants.INIT_PASSWORD);
//                        updateUser.setId(id);
//                        userApi.updateWithHuanXinInfo(updateUser);
//                    }
//                }
//            }
//        }
//
//    }

//    @Test
//    public void demotest() {
//        List<User> users = userApi.findyAll();
//        for (User user : users) {
//            Set<RecommendUser> set1 = new HashSet<>();
//            Set<Long> set2 = new HashSet<>();
//            Long currentUserId = user.getId();
//            while (set1.size() < 15) {
//                long randomLong = RandomUtil.randomLong(1, 50);
//                if (!currentUserId.equals(randomLong) && !set2.contains(randomLong)) {
//                    RecommendUser recommendUser = new RecommendUser();
//                    recommendUser.setUserId(randomLong);
//                    recommendUser.setToUserId(currentUserId);
//                    recommendUser.setScore(RandomUtil.randomDouble(50.00, 99.99, 2, RoundingMode.HALF_UP));
//                    recommendUser.setDate(new DateTime().toString("YYYY/MM/dd"));
//                    set1.add(recommendUser);
//                }
//            }
//            System.out.println(set1);
//            recommendUserApi.addBath(set1, RecommendUser.class);
//        }
//
//    }
//    @Test
//    public void Reo(){
//        recommendUserApi.removeAll();
//    }



