package com.sandu.system.dao;

import com.sandu.system.model.ResPic;
import com.sandu.system.model.search.ResPicSearch;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @version V1.0
 * @Title: ResPicMapper.java
 * @Package com.sandu.system.dao
 * @Description:系统-图片资源库Mapper
 * @createAuthor pandajun
 * @CreateDate 2015-05-19 16:06:59
 */
@Repository
public interface ResPicMapper {
    int insertSelective(ResPic record);

    int updateByPrimaryKeySelective(ResPic record);

    int deleteByPrimaryKey(Integer id);

    ResPic selectByPrimaryKey(Integer id);

    int selectCount(ResPicSearch resPicSearch);

    List<ResPic> selectPaginatedList(
            ResPicSearch resPicSearch);

    List<ResPic> selectList(ResPic resPic);

    List<ResPic> getPicList(List<String> list);

    List<ResPic> getResPicByIds(List<Integer> ids);
}
