package com.cheese.shiro.server.core.manager.identifier;

import com.cheese.shiro.common.perm.Permission;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 参考shiro WildcardPermission
 * 通过权限标识符完成细粒度的权限控制
 *
 * @author sobann
 */
public class IdentifierPermission implements Permission {

    protected static final String ALL_TOKEN = "*";
    protected static final String PART_DIVIDER_TOKEN = ":";
    protected static final String SUB_PART_DIVIDER_TOKEN = ",";

    @Override
    public boolean implies(String ownIdentifier, String targetIdentifier) {
        if(StringUtils.isBlank(ownIdentifier) || StringUtils.isBlank(targetIdentifier)){
            return false;
        }
        try {
            List<Set<String>> ownParts = getParts(ownIdentifier);
            List<Set<String>> checkParts = getParts(targetIdentifier);
            int i = 0;
            for (Set<String> otherPart : checkParts) {
                // 权限角色关联时不对角色进行细粒度控制，则代表此角色拥有此权限
                if (ownParts.size() - 1 < i) {
                    return true;
                } else {
                    // 权限包含目标权限时代表包含有权限，循环中通过笛卡尔积进行全匹配
                    Set<String> part = ownParts.get(i);
                    if (!part.contains(ALL_TOKEN) && !part.containsAll(otherPart)) {
                        return false;
                    }
                    i++;
                }
            }

            // If this permission has more parts than the other parts, only imply it if all of the other parts are wildcards
            for (; i < ownParts.size(); i++) {
                Set<String> part = ownParts.get(i);
                if (!part.contains(ALL_TOKEN)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean impliesEntity(String identifier, String entity) {
        if(ALL_TOKEN.equals(identifier)){
            return true;
        }
        String[] split = identifier.split(PART_DIVIDER_TOKEN);
        return split[0].equalsIgnoreCase(entity);
    }

    /**
     * 切割权限标识符，获取权限标识列表
     * @param identifier
     * @return
     * @throws Exception
     */
    public List<Set<String>> getParts(String identifier) throws Exception{
        String[] parts = identifier.split(PART_DIVIDER_TOKEN);
        List<Set<String>> result = new ArrayList<>();
        Arrays.stream(parts).forEach(
                part->{
                    String[] subParts = part.split(SUB_PART_DIVIDER_TOKEN);
                    if (subParts.length==0) {
                        throw new IllegalArgumentException("Identifier cannot contain parts with only dividers. Make sure it is properly formatted");
                    }
                    result.add(Arrays.stream(subParts).map(String::trim).collect(Collectors.toSet()));
                }
        );
        if (result.size()==0) {
            throw new IllegalArgumentException("Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
        }
        return result;
    }
}
