package admin.service.impl;

import admin.entity.TesseractRoleBtn;
import admin.mapper.TesseractRoleBtnMapper;
import admin.service.ITesseractRoleBtnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-19
 */
@Service
public class TesseractRoleBtnServiceImpl extends ServiceImpl<TesseractRoleBtnMapper, TesseractRoleBtn> implements ITesseractRoleBtnService {

    @Override
    public Integer countPermissions(List<String> roleNameList, String menuCode, String btnCode) {
        return getBaseMapper().countPermissions(roleNameList, menuCode, btnCode);
    }


}
