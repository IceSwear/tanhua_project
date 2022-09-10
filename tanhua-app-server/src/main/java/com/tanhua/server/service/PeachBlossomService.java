package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.dubbo.api.PeachBlossomApi;
import com.tanhua.dubbo.api.RemainingTimesApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.PeachBlossom;
import com.tanhua.model.mongo.RemainingTimes;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PeachBlossomVo;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * @Description: PeachBlossomService桃花传音服务
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@Service
public class PeachBlossomService {


    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @DubboReference
    private PeachBlossomApi peachBlossomApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private RemainingTimesApi remainingTimesApi;

    public void sendVoice(MultipartFile soundFile) throws IOException {
        //其实要校验的，但是我不知道后缀是啥格式,先不弄了
        String originalFilename = soundFile.getOriginalFilename();
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        StorePath storePath = fastFileStorageClient.uploadFile(soundFile.getInputStream(), soundFile.getSize(), suffixName, null);
        String voiceUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        peachBlossomApi.savePeachBlosoomVoice(UserHolder.getUserId(), voiceUrl);
    }


    /**
     * 获取桃花传音信息
     *
     * @return
     */
    public PeachBlossomVo receivePeachBlossomMessage() {
        Long currentUserId = UserHolder.getUserId();
        RemainingTimes rt = remainingTimesApi.findByUserId(currentUserId);
        if (Objects.isNull(rt) || rt.getRemainingTimes() < 1) {
            throw new BusinessException(ErrorResult.builder().errMessage("次数不足，请明天再试！").build());
        }
        PeachBlossom peachBlossom = peachBlossomApi.getRandomVoiceExceptSelf(currentUserId);

        if (Objects.isNull(peachBlossom)) {
            throw new BusinessException(ErrorResult.builder().errMessage("暂无合适的桃花传音，请晚点再试").build());
        }
        //get peachBlossom,非空
        UserInfo userInfo = userInfoApi.findById(peachBlossom.getPublishUserId());
        if (!Objects.isNull(userInfo)) {
            PeachBlossomVo vo = PeachBlossomVo.init(userInfo, peachBlossom);
            vo.setRemainingTimes(rt.getRemainingTimes());
            log.info("桃花传音Vo:{}", vo);
            return vo;
        }
        return null;
    }
}
