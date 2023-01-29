package com.cheese.shiro.server.gateway.handler.auth;

import com.alibaba.fastjson.JSON;
import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.domain.R;
import com.cheese.shiro.common.enums.Status;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.common.manager.uri.entity.AuthInfo;
import com.cheese.shiro.common.manager.uri.entity.AuthUriMapping;
import com.cheese.shiro.common.service.ShiroServiceProvider;
import com.cheese.shiro.common.util.ParamUtils;
import com.cheese.shiro.common.util.RegexUtils;
import com.cheese.shiro.server.gateway.handler.Handler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * 网关或服务的前端管理器处鉴权使用
 * 从request中获取检验参数，需要前置requestTokenChecker,或者提前将context中currentIdentity放置
 * 在token、session验证后调用 对@Auth注解进行检查
 * order 320
 * @author sobann
 */
public class AuthHandler extends Handler {

    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    public static final String PERM = "PermAuth";
    /**
     * 鉴权服务提供者
     */
    protected ShiroServiceProvider shiroServiceProvider;
    /**
     * 权限@Auth资源管理器
     */
    protected UriManager<AuthUriMapping> uriManager;

    protected String unAuthContent =  JSON.toJSONString(R.failed(Status.MethodNotAllowed.getMsg()));
    protected String contentType = "application/json;charset=UTF-8";

    public void setShiroServiceProvider(ShiroServiceProvider shiroServiceProvider) {
        this.shiroServiceProvider = shiroServiceProvider;
    }

    public void setUriManager(UriManager<AuthUriMapping> uriManager) {
        this.uriManager = uriManager;
    }

    public void setUnAuthContent(String unAuthContent) {
        this.unAuthContent = unAuthContent;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean preHandle(Context context) {
        String serviceId = context.getServiceId();
        if(StringUtils.isBlank(serviceId)){
            return true;
        }
        String requestURI = context.getRequestUri();
        //查找匹配信息
        AuthUriMapping matchUriMapping = uriManager.getMatchUriMapping(context);
        if(matchUriMapping==null){
            return true;
        }
        //获取配置项
        List<AuthInfo> auths = matchUriMapping.getAuths();

        //使用已经匹配的pattern,缩小范围
        String bestMatchPattern = context.getBestUriPattern();
        //进行参数校验
        boolean auth = auth(requestURI,bestMatchPattern,context,auths);
        if(!auth){
            //用户无权限时，前置过滤器禁止请求继续访问，设置返回值
            if(StringUtils.isBlank(context.getErrorContent())){
                context.setErrorContent(unAuthContent);
                context.setErrorContentType(contentType);
            }
        }
        return auth;
    }

    /**
     * 校验单当前请求中的参数
     * @param requestURI 当前请求uri
     * @param urlPattern uriPattern @controller中的requestPath
     * @param context 当前请求上下文
     * @param authInfos 配置信息
     * @return
     */
    public boolean auth(String requestURI,String urlPattern,Context context,List<AuthInfo> authInfos){
        if(CollectionUtils.isEmpty(authInfos)){
            return true;
        }
        Object identityContext = context.getIdentityContext();
        String identity = null;
        try {
            //获取当前身份信息
            if(identityContext!=null){
                identity = IdentityManager.getContextParser().getIdentity(identityContext);
            }
        } catch (Exception ignored) {
        }

        for (AuthInfo authInfo : authInfos) {
            if(StringUtils.isBlank(identity)){
                if(authInfo.isLogin()){
                    return false;
                }else{
                    //不用登录 给予默认身份
                    identity = IdentityManager.getDefaultIdentity();
                }
            }
            String identifierExpress = authInfo.getIdentifier();
            //标识符表达式转换为标识符
            String identifier = ParamUtils.getValueWithExpress(identifierExpress, context, urlPattern, requestURI);
            String instanceIdExperss = authInfo.getInstanceId();
            context.setAttribute(PERM,identifier+"#"+instanceIdExperss);
            //可能存在批量校验
            Collection<String> instances = new HashSet<>();
            if("*".equals(instanceIdExperss)){
                instances.add(instanceIdExperss);
            }else if("_".equals(instanceIdExperss)){
                instances.add(instanceIdExperss);
            }else{
                instances = ParamUtils.getParameterFromRequestAndBody(context, instanceIdExperss, urlPattern, requestURI);
                String regex = authInfo.getRegex();
                //查看instanceId是否需要进一步正则提取
                if(StringUtils.isNotBlank(regex)){
                    //进行批量提取
                    try {
                        Collection<String> realIds = RegexUtils.extractWithRegex(regex, authInfo.getIndex(), instances);
                        if(realIds==null){
                            logger.info("Error extract {} With Regex ={} and index={}",JSON.toJSONString(instances),regex,authInfo.getIndex());
                            return false;
                        }
                        //进行替换
                        instances = realIds;
                    } catch (Exception e) {
                        logger.error("ExtractWithRegex error",e);
                        return false;
                    }
                }
            }
            if(!CollectionUtils.isEmpty(instances)){
                String app = authInfo.getApp();
                //判断用户是否具有调用@Auth注解标识的接口的验证
                boolean permitted = shiroServiceProvider.getShiroService().isAllPermitted(new ArrayList<>(instances),identifier,identity,app);
                if(!permitted){
                    return false;
                }
            }else{
                logger.info("identity is not exist or instanceId is empty");
                return false;
            }
        }
        return true;
    }



    @Override
    public int order() {
        return 300;
    }
}
