package com.cheese.shiro.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 表达式工具
 * @author sobann
 */
public class ExpressUtils {

    private static List<Character> charTokens;

    static {
        List<Character> characters = new ArrayList<>();
        characters.add('(');
        characters.add(')');
        characters.add('=');
        characters.add('<');
        characters.add('>');
        characters.add('+');
        characters.add('-');
        characters.add('`');
        characters.add(' ');
        charTokens = characters;
    }

    /**
     * 获取表达式中变量，默认分隔符
     * @param express
     * @return
     */
    public static List<String> getVars(String express){
        return getVars(express,charTokens);
    }

    /**
     * 获取表达式中变量
     * @param express
     * @param tokens 指定分隔符
     * @return
     */
    public static List<String> getVars(String express, Collection<Character> tokens){
        List<String> vars = new ArrayList<>();
        StringBuffer var = new StringBuffer("");
        for (int i = 0; i < express.length(); i++) {
            char c = express.charAt(i);
            if(tokens.contains(c)){
                if(var.length()>0){
                    String s = var.toString();
                    if(!StringUtils.isNumeric(s) && !vars.contains(s)){
                        vars.add(s);
                    }
                    var = new StringBuffer("");
                }
            }else {
                var.append(c);
            }

        }
        if(var.length()>0){
            String s = var.toString();
            if(!StringUtils.isNumeric(s) && !vars.contains(s)){
                vars.add(s);
            }
        }
        return vars;
    }

    /**
     * 获取变量替换后的表达式（默认分隔符）
     * @param express
     * @param replace 替换实现类
     * @return
     */
    public static String variableReplace(String express,Replace replace){
        return variableReplace(express,charTokens,replace);
    }

    /**
     * 获取变量替换后的表达式
     * @param express
     * @param tokens 指定分隔符
     * @param replace 替换实现类
     * @return
     */
    public static String variableReplace(String express, Collection<Character> tokens, Replace replace){
        StringBuffer var = new StringBuffer("");
        StringBuffer exp = new StringBuffer("");
        for (int i = 0; i < express.length(); i++) {
            char c = express.charAt(i);
            if(tokens.contains(c)){
                if(var.length()>0){
                    String key = var.toString();
                    String value = replace.get(key);
                    exp.append(value);
                    var = new StringBuffer("");
                }
                exp.append(c);
            }else {
                var.append(c);
            }

        }
        if(var.length()>0){
            String key = var.toString();
            String value = replace.get(key);
            exp.append(value);
        }
        return exp.toString();
    }
}
