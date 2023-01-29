package com.cheese.shiro.common.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具
 * @author sobann
 */
public class RegexUtils {
    private static final Logger logger = LoggerFactory.getLogger(RegexUtils.class);
    private static Map<String,Pattern> patterns = new ConcurrentHashMap<>();

    private static Pattern getPattern(String regex){
        Pattern pattern = patterns.get(regex);
        if(pattern==null){
            try {
                pattern = Pattern.compile(regex);
            } catch (Exception e) {
               logger.error("Compile Regex Error:"+regex,e);
                return null;
            }
            patterns.put(regex,pattern);
        }
        return pattern;
    }

    public static Collection<String> extractWithRegex(String regex,int index,Collection<String> strs){
        if(StringUtils.isBlank(regex)){
            return strs;
        }
        List<String> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(strs)){
            return  result;
        }
        Pattern pattern = getPattern(regex);
        if(pattern==null){
            return null;
        }
        for (String str : strs) {
            Matcher matcher = pattern.matcher(str);
            if(matcher.find()){
                String  extract = matcher.group(index);
                result.add(extract);
            }else{
                logger.error("Can Extract {} with regex = {} and index = {}",str,regex,index);
                return null;
            }
        }
        return result;
    }

    public static String extractWithRegex(String regex,int index,String str){
        if(StringUtils.isBlank(regex)){
            return str;
        }
        Pattern pattern = getPattern(regex);
        if(pattern==null){
            return null;
        }
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            return matcher.group(index);
        }
        logger.error("Can not Extract {} with regex = {} and index = {}",str,regex,index);
        return null;
    }
}
