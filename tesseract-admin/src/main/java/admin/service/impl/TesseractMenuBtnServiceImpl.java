package admin.service.impl;

import admin.entity.TesseractBtnResource;
import admin.entity.TesseractMenuBtn;
import admin.mapper.TesseractMenuBtnMapper;
import admin.service.ITesseractMenuBtnService;
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
public class TesseractMenuBtnServiceImpl extends ServiceImpl<TesseractMenuBtnMapper, TesseractMenuBtn> implements ITesseractMenuBtnService {

    @Override
    public List<TesseractBtnResource> listBtnByMenuIdList(List<Integer> menuIdList) {
        return getBaseMapper().listBtnByMenuIdList(menuIdList);
    }
}
