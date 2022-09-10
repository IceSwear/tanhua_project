package com.tanhua.model.vo.testsoul.report;

import com.tanhua.model.mongo.testsoul.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 问卷列表
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSoulReportVo {

    /**
     * 结论
     */
    private String conclusion;
    /**
     * 封面
     */
    private String cover;
    /**
     * 测量
     */
    private List<Dimension> dimensions;
    /**
     * 相似度
     */
    private List<SimilarYou> similarYou;


    /**
     * 报告vo初始化
     *
     * @param report
     * @param similarYous
     * @param dimensions
     * @return
     */
    public static TestSoulReportVo init(Report report, List<SimilarYou> similarYous, List<Dimension> dimensions) {
        TestSoulReportVo vo = new TestSoulReportVo();
        if (!Objects.isNull(report)) {
            vo.setConclusion(report.getConclusion());
            vo.setCover(report.getCover());
        }
        vo.setSimilarYou(similarYous);
        vo.setDimensions(dimensions);
        return vo;
    }
}
