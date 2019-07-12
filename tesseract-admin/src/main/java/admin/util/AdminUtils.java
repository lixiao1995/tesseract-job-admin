package admin.util;

import admin.core.scheduler.CronExpression;
import admin.pojo.StatisticsLogDO;
import admin.pojo.WebUserDetail;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import tesseract.exception.TesseractException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class AdminUtils {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
            linkedHashMap.put(startDate.format(DATE_TIME_FORMATTER), 0);
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
                if (!"serialVersionUID".equals(name) && value != null) {
                    //添加进查询条件
                    //String 采用like处理
                    if (value instanceof String && !"".equals(((String) value).trim())) {
                        queryWrapper.like(name, value);
                    } else {
                        queryWrapper.eq(name, value);
                    }

                }
            }
        } catch (Exception e) {
            log.error("buildCondition 发生异常:{}", e.getMessage());
            throw new TesseractException("构建查询条件出错");
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
    public static void buildNewEntityCommonFields(Object obj, long currentTimeMillis, WebUserDetail user) {
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
    public static void buildUpdateEntityCommonFields(Object obj, long currentTimeMillis, WebUserDetail user) {
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
}
