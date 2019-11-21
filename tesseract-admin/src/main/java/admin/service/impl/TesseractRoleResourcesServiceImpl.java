package admin.service.impl;

import admin.entity.TesseractRole;
import admin.entity.TesseractRoleResources;
import admin.entity.TesseractUserRole;
import admin.mapper.TesseractRoleResourcesMapper;
import admin.service.ITesseractRoleResourcesService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-10
 */
@Service
public class TesseractRoleResourcesServiceImpl extends ServiceImpl<TesseractRoleResourcesMapper, TesseractRoleResources> implements ITesseractRoleResourcesService {

}
