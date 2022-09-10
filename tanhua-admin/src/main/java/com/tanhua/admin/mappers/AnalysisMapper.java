package com.tanhua.admin.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.Analysis;


public interface AnalysisMapper extends BaseMapper<Analysis> {


    Integer queryCumulativeUsers();

}
