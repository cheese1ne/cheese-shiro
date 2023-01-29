package com.cheese.shiro.common.manager.identity;

import com.cheese.shiro.common.Coder;
import com.cheese.shiro.common.ContextParser;
import com.cheese.shiro.common.exception.IdentityManagerException;
import com.cheese.shiro.common.exception.NoIdentityException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 身份管理器
 * 初始化以后生效，未初试化下使用，将会抛出异常
 *
 * Coder 以及 ContextParser 须保证各组件中配置一致
 * @see IdentityManagerChecker  应用初始化时，将会检查
 *
 * @author sobann
 */
public class IdentityManager {

    private static final Logger logger = LoggerFactory.getLogger(IdentityManager.class);

    /**
     * 传递Context的参数名称
     */
    private static String ContextTracerName;
    /**
     * 默认身份
     */
    private static String DefaultIdentity;
    /**
     * 编码器
     */
    private static Coder Coder;
    /**
     * 编码器
     */
    private static ContextParser ContextParser;
    /**
     * 是否开启mock模拟测试
     */
    private static boolean mock = false;
    /**
     * mock模拟上下文
     */
    private static String mockContext;

    static {
        IdentityManager.Coder = new JacksonCoder();
        //默认解析器，只存放用户id
        IdentityManager.ContextParser = new ContextParser() {
            @Override
            public Object getPrimary(Object context) {
                return context;
            }

            @Override
            public String getIdentity(Object context) {
                return context.toString();
            }

            @Override
            public Object getSubject(Object context) {
                return context;
            }
        };
    }

    /**
     * 组件配置
     * 如果配置，请在项目初始化中配置
     * 各部分配置，请保持一致
     * @param coder
     * @param contextParser
     */
    public static void configure(Coder coder, ContextParser contextParser){
        IdentityManager.Coder = coder;
        logger.info("Configure Coder :{}",coder.getClass().getCanonicalName());
        IdentityManager.ContextParser = contextParser;
        logger.info("Configure ContextParser :{}",contextParser.getClass().getCanonicalName());
    }

    /**
     * 参数配置
     * 默认由服务注册后，同步至当前实例，不需要配置
     * @param contextTracerName  传递参数名称
     * @param defaultIdentity 默认参数值
     */
    public static void configure(String contextTracerName, String defaultIdentity){
        IdentityManager.ContextTracerName = contextTracerName;
        IdentityManager.DefaultIdentity = defaultIdentity;
        logger.info("IdentityManager Is Configured : contextTracerName={}, DefaultIdentity={}",contextTracerName,defaultIdentity);
    }

    /**
     * 开启虚拟化
     * @param mock
     * @param mockContext
     */
    public static void mock(boolean mock,String mockContext){
        IdentityManager.mock = mock;
        IdentityManager.mockContext = mockContext;
        logger.info("Mock IdentityManager Is Configured, MockContext is {}",mockContext);
    }

    /**
     * 未初始化，调用抛出异常
     * @return
     */
    public static String getContextTracerName() {
        if(StringUtils.isNotBlank(ContextTracerName)){
            return ContextTracerName;
        }else {
            throw new IdentityManagerException("The IdentityName Is Not Configured");
        }
    }

    public static Coder getCoder() {
        return Coder;
    }

    public static ContextParser getContextParser() {
        return ContextParser;
    }

    /**
     * 未初始化，调用抛出异常
     * 返回默认身份信息（未登录，游客）
     * @return
     */
    public static String getDefaultIdentity() {
        if(StringUtils.isNotBlank(DefaultIdentity)){
            return DefaultIdentity;
        }else {
            throw new IdentityManagerException("The DefaultIdentity Is Not Configured");
        }
    }

    /**
     * 检查 管理器是否 配置完全
     * @return
     */
    public static boolean initialized() {
        return StringUtils.isNotBlank(ContextTracerName) && StringUtils.isNotBlank(DefaultIdentity) && Coder !=null && ContextParser !=null;
    }

    /**
     * 当前线程绑定身份信息
     * @param context
     */
    public static void bind(String context){
        if(StringUtils.isNotBlank(context)){
            IdentityHolder.setContext(context);
        }else {
            IdentityHolder.clear();
        }
    }

    /**
     * 解绑身份信息
     */
    public static void unBind(){
        IdentityHolder.clear();
    }

    public static String createContext(Object context){
        try {
            return Coder.encode(context);
        } catch (Exception e) {
            logger.error("Encode Identity Error",e);
            throw  new IdentityManagerException("Create Identity Error",e);
        }
    }

    /**
     * 检查当前是否有context
     * @return
     */
    public static boolean hasContext(){
        try {
            getContext();
            return true;
        } catch (Exception e) {
           return false;
        }
    }

    public static String getContext(){
        if(mock){
            logger.warn("Mock is taking effect !!!,Please ensure is dev Environment");
            return mockContext;
        }
        String context = IdentityHolder.getContext();
        if(StringUtils.isBlank(context)){
            throw new NoIdentityException();
        }
        return context;
    }

    public static Object getContextObject(){
        Object contextObject = IdentityHolder.getContextObject();
        if(contextObject!=null){
            return contextObject;
        }else {
            String context = getContext();
            try {
                contextObject = Coder.decode(context);
                IdentityHolder.setContextObject(contextObject);
                return contextObject;
            } catch (Exception e) {
                throw new IdentityManagerException("Decode Context Error",e);
            }
        }
    }

    /**
     * 获取身份信息
     * 当前线程不含身份信息，将抛出异常
     * 可使用@Login,确保用户当前登录
     * @return
     */
    public static String getIdentity(){
        String identity = IdentityHolder.getIdentity();
        if(StringUtils.isNotBlank(identity)){
            return identity;
        }else {
            Object contextObject = getContextObject();
            identity = ContextParser.getIdentity(contextObject);
            if(StringUtils.isNotBlank(identity) && !identity.equalsIgnoreCase(getDefaultIdentity())){
                IdentityHolder.setIdentity(identity);
                return identity;
            }
        }
        throw new NoIdentityException();
    }

    /**
     * 获取当前身份信息，当前身份信息为空，则返回默认身份信息(游客)
     * 默认身份信息用于获取公共权限
     * 可使用@Login,确保用户当前登录
     * @return
     */
    public static String getIdentityOrDefault(){
        String identity = null;
        try {
            identity = getIdentity();
        } catch (Exception e) {
            identity = getDefaultIdentity();
        }
        return identity;
    }


    /**
     * 获取当前身份信息对象
     * @return
     */
    public static Object getSubject(){
        Object subject = IdentityHolder.getSubject();
        if(subject!=null){
            return subject;
        }else {
            Object contextObject = getContextObject();
            subject = ContextParser.getSubject(contextObject);
            IdentityHolder.setSubject(subject);
            return subject;
        }
    }

    /**
     * 获取当前主键
     *
     * @return
     */
    public static Object getPrimary(){
        Object primary = IdentityHolder.getPrimary();
        if(primary!=null){
            return primary;
        }else {
            Object contextObject = getContextObject();
            primary = ContextParser.getPrimary(contextObject);
            IdentityHolder.setPrimary(primary);
            return primary;
        }
    }




}
