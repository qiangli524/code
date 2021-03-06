package com.sandu.node.dao;

import com.sandu.node.model.NodeInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NodeInfo record);

    int insertSelective(NodeInfo record);

    NodeInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(NodeInfo record);

    int updateByPrimaryKey(NodeInfo record);

    List<NodeInfo> getNodeInfo(@Param("start") Integer start,
                               @Param("size") Integer size);

    List<NodeInfo> getNodeInfoSelective(NodeInfo nodeInfo);
}