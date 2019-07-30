package admin.util;

import admin.core.scheduler.CronExpression;
import admin.entity.TesseractRole;
import admin.pojo.DO.StatisticsLogDO;
import admin.security.SecurityUserContextHolder;
import admin.security.SecurityUserDetail;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static admin.constant.AdminConstant.SUPER_ADMIN_NAME;

@Slf4j
public class AdminUtils {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    /**
     * 实体公共字段填充
     */
    public static final String COMMONFIELD_CREATETIME = "createTime";
    public static final String COMMONFIELD_UPDATETIME = "updateTime";
    public static final String COMMONFIELD_CREATEUSERID = "createUserId";
    public static final String COMMONFIELD_CREATEUSERNAME = "createUserName";
    public static final String COMMONFIELD_UPDATEUSERID = "updateUserId";
    public static final String COMMONFIELD_UPDATEUSERNAME = "updateUserName";

    /**
     * 根据StatisticsLogDO，构建返回统计列表
     *
     * @param statisticsLogDOList
     * @param statisticsDays
     * @return
     */
    public static Collection<Integer> buildStatisticsList(List<StatisticsLogDO> statisticsLogDOList, Integer statisticsDays) {
        LinkedHashMap<String, Integer> linkedHashMap = Maps.newLinkedHashMap();
        LocalDate startDate = LocalDate.now().minusDays(6);
        for (int i = 0; i < statisticsDays; i++) {
            linkedHashMap.put(startDate.format(DATE_FORMATTER), 0);
            startDate = startDate.plusDays(1);
        }
        statisticsLogDOList.forEach(statisticsLogDO -> {
            linkedHashMap.put(statisticsLogDO.getDataStr(), statisticsLogDO.getNum());
        });
        return linkedHashMap.values();
    }

    /**
     * 构建执行条件
     *
     * @param queryWrapper
     * @param obj
     */
    public static void buildCondition(QueryWrapper queryWrapper, Object obj) {
        Class<?> aClass = obj.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        try {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                String name = field.getName();
                //通用设置group_id
                if ("groupId".equals(name)) {
                    checkRoleAndCallback(groupId -> {
                        queryWrapper.eq("group_id", groupId);
                    });
                }
                if (!"serialVersionUID".equals(name) && value != null) {
                    //添加进查询条件
                    //String 采用like处理
                    if (value instanceof String && !"".equals(((String) value).trim())) {
                        queryWrapper.like(StringUtils.camelToUnderline(name), value);
                    } else {
                        queryWrapper.eq(StringUtils.camelToUnderline(name), value);
                    }

                }
            }
        } catch (Exception e) {
            log.error("buildCondition 发生异常:{}", e.getMessage());
            throw new TesseractException("构建查询条件出错");
        }
    }

    /**
     * 检测当前角色是否是超级管理员，如果是则回调
     *
     * @param consumer
     */
    public static void checkRoleAndCallback(Consumer<Integer> consumer) {
        SecurityUserDetail user = SecurityUserContextHolder.getUser();
        List<String> roleNameList = user.getRoleList().stream().map(TesseractRole::getRoleName).collect(Collectors.toList());
        //每个用户必须有属于的组和角色
        if (CollectionUtils.isEmpty(roleNameList)) {
            throw new TesseractException("当前用户没有角色，请先设置角色");
        }
        @NotNull Integer groupId = user.getGroupId();
        if (groupId == null) {
            throw new TesseractException("当前用户没有组，请先设置组");
        }
        //如果是不是超级管理员，则回调
        if (!roleNameList.contains(SUPER_ADMIN_NAME)) {
            consumer.accept(groupId);
        }
    }

    /**
     * <<<<<<< HEAD
     * 创建实体，设置公共属性
     *
     * @param obj
     * @param currentTimeMillis
     * @param user
     */
    public static void buildNewEntityCommonFields(Object obj, long currentTimeMillis, SecurityUserDetail user) {
        Integer userId = user.getId();
        String userName = user.getName();
        try {
            Class<?> aClass = obj.getClass();
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_CREATETIME, currentTimeMillis);
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_UPDATETIME, currentTimeMillis);
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_CREATEUSERID, userId);
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_CREATEUSERNAME, userName);
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_UPDATEUSERID, userId);
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_UPDATEUSERNAME, userName);
        } catch (Exception e) {
            log.error("buildNewEntityCommonFields 发生异常:{}", e.getMessage());
            throw new TesseractException("创建实体，设置公共属性出错");
        }
    }


    /**
     * 更新实体，设置公共属性
     *
     * @param obj
     * @param currentTimeMillis
     * @param user
     */
    public static void buildUpdateEntityCommonFields(Object obj, long currentTimeMillis, SecurityUserDetail user) {
        Integer userId = user.getId();
        String userName = user.getName();
        try {
            Class<?> aClass = obj.getClass();
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_UPDATETIME, currentTimeMillis);
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_UPDATEUSERID, userId);
            ReflectionUtils.setBeanFieldValue(obj, aClass, COMMONFIELD_UPDATEUSERNAME, userName);
        } catch (Exception e) {
            log.error("buildUpdateEntityCommonFields 发生异常:{}", e.getMessage());
            throw new TesseractException("更新实体，设置公共属性出错");
        }
    }

    /**
     * @param cron
     * @return
     * @throws Exception
     */
    public static Long caculateNextTime(String cron) throws Exception {
        CronExpression cronExpression = new CronExpression(cron);
        return cronExpression.getTimeAfter(new Date()).getTime();
    }

    public static String epochMiliToString(Long epochMilli, DateTimeFormatter dateTimeFormatter) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        String string;
        if (dateTimeFormatter != null) {
            string = localDateTime.format(dateTimeFormatter);
        } else {
            string = localDateTime.format(DATE_TIME_FORMATTER);
        }
        return string;
    }

    /**
     * 加密字符串
     *
     * @param string
     * @return
     */
    public static String bcryptEncode(String string) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(string);
    }
}
