package admin.service.impl;

import admin.entity.TesseractBtnResource;
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
    public List<TesseractBtnResource> listBtnByRoleIdList(List<Integer> roleIdList) {
        return getBaseMapper().listBtnByRoleIdList(roleIdList);
    }
}
