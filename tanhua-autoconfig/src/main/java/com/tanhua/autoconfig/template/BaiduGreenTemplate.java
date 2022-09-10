package com.tanhua.autoconfig.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tanhua.autoconfig.properties.BaiduGreenProperties;
import com.tanhua.commons.utils.AuditResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * @Description: baidu内容审核
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Slf4j
public class BaiduGreenTemplate {


    /**
     * 获取accessToken的
     */
    private final static String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
    /**
     * 文字检测
     */
    private final static String textHttp = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";

    private final static String imageHttp = "https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/v2/user_defined";
    public static String baiduAccessToken = null;

    private BaiduGreenProperties properties;


    public BaiduGreenTemplate(BaiduGreenProperties properties) {
        this.properties = properties;
        log.info("开始获取token");
        this.setBaiduAccessToken();
        log.info("获取token成功");
        // 获取token地址
//        String getAccessTokenUrl = authHost
//                // 1. grant_type为固定参数
//                + "grant_type=client_credentials"
//                // 2. 官网获取的 API Key
//                + "&client_id=" + properties.getApiKey()
//                // 3. 官网获取的 Secret Key
//                + "&client_secret=" + properties.getSecretKey();
//        BufferedReader in = null;
//        try {
//            URL realUrl = new URL(getAccessTokenUrl);
//            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
//            connection.setRequestMethod("GET");
//            connection.connect();
//            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String result = "";
//            String line;
//            while ((line = in.readLine()) != null) {
//                result += line;
//            }
//            /**
//             * 返回结果示例
//             */
//            log.info("result:{}", result);
//            JSONObject jsonObject = JSON.parseObject(result);
//            baiduAccessToken = jsonObject.getString("access_token");
//            log.info("access_token:{}", baiduAccessToken);
//        } catch (Exception e) {
//            log.error("获取token失败:{}", e.toString());
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException e) {
//                log.error("获取百度文字检查token失败:{}", e.toString());
//            }
//        }
    }


    /**
     * 发送get请求
     *
     * @param text
     * @return
     */
    public JSONObject sendText(String text) throws IOException {

        //%2B是+号的意思，通过 GET方式传值的时候，+号会被浏览器处理为空，所以需要转换为%2b。
        text = text.replaceAll(" ", "%2B");
        String result = MyOkHttpUtils.doGetMethod(textHttp + "?access_token=" + baiduAccessToken + "&text=" + text);
        log.info("OKhttp结果:{}", result);
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject;
    }

    public JSONObject sendImage(String imageUrl) throws IOException {

        //%2B是+号的意思，通过 GET方式传值的时候，+号会被浏览器处理为空，所以需要转换为%2b。
        imageUrl = imageUrl.replaceAll(" ", "%2B");
        String result = MyOkHttpUtils.doGetMethod(imageHttp + "?access_token=" + baiduAccessToken + "&imgUrl=" + imageUrl);
        log.info("OKhttp结果:{}", result);
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject;
    }

    /**
     * 检查文本是否合规
     *
     * @param text
     * @return
     */
    public String checkText(String text) {
        try {
            if (StringUtils.isEmpty(text)) {
                return null;
            }
            JSONObject result = sendText(text);
            //错误码不为空时，返回，内层错误提示信息
            Integer conclusionType = result.getInteger("conclusionType");
            log.info("conclusionType:-----{}", conclusionType);
            if (!StringUtils.isEmpty(conclusionType)) {
                if (conclusionType.equals(1)) {
                    return AuditResult.TEXT_PASS;
                } else if (conclusionType.equals(2)) {
                    return AuditResult.TEXT_BLOCK;
                } else if (conclusionType.equals(3) || conclusionType.equals(4)) {
                    return AuditResult.TEXT_ERROR;
                }
                return result.getJSONArray("data").getJSONObject(0).getString("msg");
            }
            //合规，则为0
            //审核结果类型，可取值1、2、3、4，分别代表1：合规，2：不合规，3：疑似，4：审核失败
            //得到data的arraylist，第一个对象的msg，疑似

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String checkImage(List<String> medias) {
        try {
            //严谨一点要判空，但是这里不需要
            for (String media : medias) {
                JSONObject result = sendImage(media);
                Integer conclusionType = result.getInteger("conclusionType");
                if (conclusionType.equals(2)) {
                    return AuditResult.TEXT_BLOCK;
                }
                if (conclusionType.equals(3) || conclusionType.equals(4)) {
                    return AuditResult.TEXT_ERROR;
                }
            }
            return AuditResult.TEXT_PASS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String checkImage(String imageUrl) {
        try {
            if (StringUtils.isEmpty(imageUrl)) {
                return null;
            }
            JSONObject result = sendImage(imageUrl);
            //错误码不为空时，返回，内层错误提示信息
            Integer conclusionType = result.getInteger("conclusionType");
            if (conclusionType.equals(1)) {
                return AuditResult.TEXT_PASS;
            } else if (conclusionType.equals(2)) {
                return AuditResult.TEXT_BLOCK;
            } else if (conclusionType.equals(3) || conclusionType.equals(4)) {
                return AuditResult.TEXT_ERROR;
            }
            //合规，则为0
            //审核结果类型，可取值1、2、3、4，分别代表1：合规，2：不合规，3：疑似，4：审核失败
            //得到data的arraylist，第一个对象的msg，疑似
            return result.getJSONArray("data").getJSONObject(0).getString("msg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 设置百度accesstoken，获取
     */
    public void setBaiduAccessToken() {
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + properties.getApiKey()
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + properties.getSecretKey();
        try {
            String result = MyOkHttpUtils.doGetMethod(getAccessTokenUrl);
            JSONObject jsonObject = JSON.parseObject(result);
            baiduAccessToken = jsonObject.getString("access_token");
            log.info("access_token:{}", baiduAccessToken);
        } catch (IOException e) {
            log.error("获取token失败:{}", e.toString());
        }
    }
}