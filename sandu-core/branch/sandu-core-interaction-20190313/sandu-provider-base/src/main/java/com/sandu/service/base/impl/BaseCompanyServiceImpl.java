package com.sandu.service.base.impl;

import com.google.gson.Gson;
import com.sandu.api.base.common.ResponseEnvelope;
import com.sandu.api.base.common.cache.RedisKeyConstans;
import com.sandu.api.base.common.util.GsonUtil;
import com.sandu.api.base.common.util.RequestUtils;
import com.sandu.api.base.input.GroupPurchaseActivitySearch;
import com.sandu.api.base.model.*;
import com.sandu.api.base.service.BaseCompanyService;
import com.sandu.api.base.service.RedisService;
import com.sandu.service.base.dao.BaseCompanyDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @version V1.0
 * @Title: BaseCompanyServiceImpl.java
 * @Package com.sandu.product.service.biz
 * @Description:产品模块-企业表ServiceImpl
 * @createAuthor pandajun
 * @CreateDate 2015-06-15 17:01:45
 */
@Service("baseCompanyService")
public class BaseCompanyServiceImpl implements BaseCompanyService {

    private final static Gson GSON = new Gson();
    private final static String CLASS_LOG_PREFIX = "[产品模块-企业服务]:";
    private final static Logger logger = LoggerFactory.getLogger(BaseCompanyServiceImpl.class);
    @Autowired
    private BaseCompanyDao baseCompanyDao;
    @Autowired
    private RedisService redisService;

    /**
     * 获取数据详情
     *
     * @param id
     * @return BaseCompany
     */
    @Override
    public BaseCompany get(Integer id) {
        return baseCompanyDao.selectByPrimaryKey(id);
    }


    /**
     * 所有数据
     *
     * @param baseCompany
     * @return List<BaseCompany>
     */
    @Override
    public List<BaseCompany> getList(BaseCompany baseCompany) {
        return baseCompanyDao.selectList(baseCompany);
    }

	@Override
	public BaseCompany getCompanyByDomainName(String companyDomainName) {
		//根据域名从缓存获取相关数据，如果查不到，则从数据库里面查询，查到则重新放到缓存里面。
        String companyJson = redisService.getMap(RedisKeyConstans.COMPANY_DOMAIN_MAP, companyDomainName);
        BaseCompany baseCompany = null;
        if(StringUtils.isNotEmpty(companyJson)){
        	 baseCompany = GsonUtil.json2Bean(companyJson, BaseCompany.class);
        }else {
        	 BaseCompany queryBaseCompany = new BaseCompany();
        	 queryBaseCompany.setCompanyDomainName(companyDomainName);
        	 queryBaseCompany.setIsDeleted(0);
            logger.info(CLASS_LOG_PREFIX + "根据域名从缓存获取相关数据:CompanyDomainName:{}", companyDomainName);
             List<BaseCompany> baseCompanyList = this.getList(queryBaseCompany);
             logger.info(CLASS_LOG_PREFIX + "根据域名从缓存获取相关数据完成:CompanyDomainName:{},baseCompanyList:{}", companyDomainName,  null == baseCompanyList ? null : GSON.toJson(baseCompanyList));

             if (baseCompanyList != null && baseCompanyList.size() > 0) {
                 if (baseCompanyList.size() > 1) {
                     logger.error(CLASS_LOG_PREFIX + "根据域名从缓存获取相关数据,获得数据超过1条，请重新配置域名信息。:companyDomainName:{},baseCompanyList:{}", companyDomainName, GSON.toJson(baseCompanyList));
                 }
            	 baseCompany = baseCompanyList.get(0);
            	 redisService.addMap(RedisKeyConstans.COMPANY_DOMAIN_MAP, companyDomainName , GsonUtil.bean2Json(baseCompany));
             }
        }
        return baseCompany;
	}

    @Override
    public BaseCompany getCompanyByConpanyAppId(String conpanyAppId)  {
        //根据域名从缓存获取相关数据，如果查不到，则从数据库里面查询，查到则重新放到缓存里面。
        String companyJson = redisService.getMap(RedisKeyConstans.COMPANY_APP_ID , conpanyAppId);
        BaseCompany baseCompany = null;
        if(StringUtils.isNotEmpty(companyJson)){
            baseCompany = GsonUtil.json2Bean(companyJson, BaseCompany.class);
        }else {
            BaseCompany queryBaseCompany = new BaseCompany();
            queryBaseCompany.setAppId(conpanyAppId);
            queryBaseCompany.setIsDeleted(0);
            logger.info(CLASS_LOG_PREFIX + "根据域名从缓存获取相关数据:conpanyAppId:{}", conpanyAppId);
            List<BaseCompany> baseCompanyList = baseCompanyDao.selectListByCompanyAPPId(queryBaseCompany);
            logger.info(CLASS_LOG_PREFIX + "根据域名从缓存获取相关数据完成:conpanyAppId:{},baseCompanyList:{}", conpanyAppId,  null == baseCompanyList ? null : GSON.toJson(baseCompanyList));

            if (baseCompanyList != null && baseCompanyList.size() > 0) {
                if (baseCompanyList.size() > 1) {
                    logger.error(CLASS_LOG_PREFIX + "根据域名从缓存获取相关数据,获得数据超过1条，请重新配置域名信息。:companyDomainName:{},baseCompanyList:{}", conpanyAppId, GSON.toJson(baseCompanyList));
                }
                baseCompany = baseCompanyList.get(0);
                redisService.addMap(RedisKeyConstans.COMPANY_APP_ID , conpanyAppId , GsonUtil.bean2Json(baseCompany));
            }
        }
        return baseCompany;
    }

    @Override
    public BaseCompany getCompanyByDomainUrl(String domainUrl) {

        BaseCompany company = null;
        if(StringUtils.isNotBlank(domainUrl)) {
            if(domainUrl.contains("servicewechat")) {
                //Mini program
                String appId = domainUrl.substring(RequestUtils.getCharacterPosition(domainUrl,3)+1,RequestUtils.getCharacterPosition(domainUrl,4));
                company = getCompanyByConpanyAppId(appId);
            }else{
                String domainName = domainUrl.substring(domainUrl.indexOf("//")+2,domainUrl.indexOf("."));
                company = getCompanyByDomainName(domainName);
            }

        }
        return company;
    }

    @Override
    public ResponseEnvelope validateCompany(Integer companyId, Integer brandId) {
        logger.debug("Process in BaseCompanyServiceImpl.validateCompany method parameter companyId:{},branId:{}",companyId, brandId);
        ResponseEnvelope response = new ResponseEnvelope(true,"true");
        if(null == companyId || null == brandId){
            logger.error("brandId or companyId is null");
            return new ResponseEnvelope(false,"请求参数异常");
        }

        BaseCompany company = baseCompanyDao.getCompanyByBrandId(brandId);
        if(null == company){
            logger.error("brandId:{}, not found company data",brandId);
            return new ResponseEnvelope(false,"品牌有且只能属于一个公司");
        }
        if(StringUtils.isBlank(company.getCompanyDomainName())||company.getCompanyDomainName().equals("")) {
        	return new ResponseEnvelope(false,"该公司没有品牌网站", null);
        }
        company.setCompanyUrl(company.getCompanyDomainName());
        if(companyId.intValue() != company.getId().intValue()){
            logger.error("brandId：{} not belong to companyId:{}", brandId, companyId);
            return new ResponseEnvelope(false,"该品牌不属于该公司", company);
        }

        return response;
    }

    @Override
    public BaseCompany getCompanyByBrandId(Integer brandId) {
        return baseCompanyDao.getCompanyByBrandId(brandId);
    }

    @Override
    public MiniProgramDashboard getMiniProgramDashboardByAppId(String appId) {
        return baseCompanyDao.getMiniProgramDashboardByAppId(appId);
    }

    @Override
    public CompanyBrandDesc getCompanyBrandDesc(String appId,String getRichContext) {
        CompanyBrandDesc desc =  null;
        CompanyMiniProgramConfig miniProgramConfig = baseCompanyDao.getCompanyMiniProgramConfigByAppId(appId);
        if(miniProgramConfig != null) {
            desc = baseCompanyDao.getCompanyBrandDesc(miniProgramConfig.getCompanyId());
            if("Y".equalsIgnoreCase(getRichContext) && desc != null) {
                RichContext search = new RichContext();
                search.setBusinessId(miniProgramConfig.getCompanyId());
                search.setBusinessType(Integer.valueOf(0));
                RichContext context = baseCompanyDao.getRichContext(search);
                if(context != null) {
                    desc.setRichContext(context.getRichContext());
                }
            }
		}
        return desc;
    }

    @Override
    public RichContext getRichContext(RichContext context) {
        return baseCompanyDao.getRichContext(context);
    }

    @Override
    public List <MiniProgramActBargain> getMiniProgramActBargainListByAppId(String appId) {
        return baseCompanyDao.getMiniProgramActBargain(appId);
    }

    @Override
    public List <GroupPurchaseActivity> getMiniProgramGroupPurchaseActivityListByAppId(GroupPurchaseActivitySearch search) {
        CompanyMiniProgramConfig miniProgramConfig = baseCompanyDao.getCompanyMiniProgramConfigByAppId(search.getAppId());
        search.setCompanyId(miniProgramConfig.getCompanyId());
        List <GroupPurchaseActivity> result  = baseCompanyDao.getGroupPurchaseActivityList(search);
        List<GroupPurchaseActivity> uniqueResult = result.stream().collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(Comparator.comparing(GroupPurchaseActivity::getUniqueKey))), ArrayList::new)
        );
        Comparator<GroupPurchaseActivity> comparator = (h1, h2) -> h1.getId().compareTo(h2.getId());
        uniqueResult.sort(comparator.reversed());
        if(uniqueResult.size() >0) {
            List<Long> spuIds = new ArrayList <>();
            for(GroupPurchaseActivity activity : uniqueResult) {
                Long spuId = activity.getSpuId();
                if(spuId != null && spuId.longValue() > 0L) {
                    spuIds.add(spuId);
                }
            }
            if(spuIds.size() > 0) {
                List<GoodsSpuPriceInfo> goodsSpuPriceInfos = baseCompanyDao.getGoodsSpuPriceInfoList(spuIds);
                for(GroupPurchaseActivity activity : uniqueResult) {
                    for(GoodsSpuPriceInfo goodsSpuPriceInfo : goodsSpuPriceInfos) {
                        if(activity.getSpuId() != null &&
                                activity.getSpuId().longValue() == goodsSpuPriceInfo.getSpuId().longValue()) {
                            activity.setSpuPrice(goodsSpuPriceInfo.getPrice());
                        }
                    }
                }
            }
        }
        result = null;
        return uniqueResult;
    }

    @Override
    public List<CompanyMiniProgramConfig> getMiniProgramConfigByCompanyId(Integer companyId) {
        return baseCompanyDao.getMiniProgramConfigByCompanyId(companyId);
    }

    private Map<Long, GroupPurchaseActivity> getGroupPurchaseActivityMap(List <GroupPurchaseActivity> dataList) {
        return dataList.stream().collect(Collectors.toMap(GroupPurchaseActivity::getSpuId, Function.identity(), (key1, key2) -> key2));
    }


}
