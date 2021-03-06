package com.nork.product.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nork.product.dao.GroupProductDetailsMapper;
import com.nork.product.model.GroupProductDetails;
import com.nork.product.model.search.GroupProductDetailsSearch;
import com.nork.product.service.GroupProductDetailsService;

/**   
 * @Title: GroupProductDetailsServiceImpl.java 
 * @Package com.nork.product.service.impl
 * @Description:产品模块-产品组合关联表ServiceImpl
 * @createAuthor pandajun 
 * @CreateDate 2016-06-22 20:37:16
 * @version V1.0   
 */
@Service("groupProductDetailsService")
public class GroupProductDetailsServiceImpl implements GroupProductDetailsService {

	private GroupProductDetailsMapper groupProductDetailsMapper;

	@Autowired
	public void setGroupProductDetailsMapper(
			GroupProductDetailsMapper groupProductDetailsMapper) {
		this.groupProductDetailsMapper = groupProductDetailsMapper;
	}

	/**
	 * 新增数据
	 *
	 * @param groupProductDetails
	 * @return  int 
	 */
	@Override
	public int add(GroupProductDetails groupProductDetails) {
		groupProductDetailsMapper.insertSelective(groupProductDetails);
		return groupProductDetails.getId();
	}

	/**
	 *    更新数据
	 *
	 * @param groupProductDetails
	 * @return  int 
	 */
	@Override
	public int update(GroupProductDetails groupProductDetails) {
		return groupProductDetailsMapper
				.updateByPrimaryKeySelective(groupProductDetails);
	}
	
	/**
	 *    删除数据
	 *
	 * @param id
	 * @return  int 
	 */
	@Override
	public int delete(Integer id) {
		return groupProductDetailsMapper.deleteByPrimaryKey(id);
	}

	/**
	 *    获取数据详情
	 *
	 * @param id
	 * @return  GroupProductDetails 
	 */
	@Override
	public GroupProductDetails get(Integer id) {
		return groupProductDetailsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 所有数据
	 * 
	 * @param  groupProductDetails
	 * @return   List<GroupProductDetails>
	 */
	@Override
	public List<GroupProductDetails> getList(GroupProductDetails groupProductDetails) {
	    return groupProductDetailsMapper.selectList(groupProductDetails);
	}
	
	/**
	 *    获取数据数量
	 *
	 * @param  groupProductDetails
	 * @return   int
	 */
	@Override
	public int getCount(GroupProductDetailsSearch groupProductDetailsSearch){
		return  groupProductDetailsMapper.selectCount(groupProductDetailsSearch);	
    }
	

	/**
	 *    分页获取数据
	 *
	 * @param  groupProductDetails
	 * @return   List<GroupProductDetails>
	 */
	@Override
	public List<GroupProductDetails> getPaginatedList(
			GroupProductDetailsSearch groupProductDetailsSearch) {
		return groupProductDetailsMapper.selectPaginatedList(groupProductDetailsSearch);
	}

	/**
	 * 根据组合id查找组合明细
	 * @author huangsongbo
	 * @param groupId 组合id
	 * @return
	 */
	public List<GroupProductDetails> findDetailsByGroupId(Integer groupId) {
		return groupProductDetailsMapper.findDetailsByGroupId(groupId);
	}
	/**
	 * 根据组合id查找产品编码
	 * @author xiaoxc
	 * @param groupId 组合id
	 * @return
	 */
	public List<GroupProductDetails> getByGroupIdProductCodeList(Integer groupId) {
		return groupProductDetailsMapper.byGroupIdProductCodeList(groupId);
	}
	/**
	 * 通过组合id 获取 组合产品，并且获取相关资源
	 * @param id
	 * @return
	 */
	@Override
	public List<GroupProductDetails> getDataAndResourcesByGroupId(Integer id) {
		return groupProductDetailsMapper.getDataAndResourcesByGroupId(id);
	}

}
