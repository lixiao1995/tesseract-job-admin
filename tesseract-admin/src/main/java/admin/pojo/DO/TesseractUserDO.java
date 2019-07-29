package admin.pojo.DO;

import admin.entity.TesseractUser;
import lombok.Data;

import java.util.List;

@Data
public class TesseractUserDO extends TesseractUser {
    private List<Integer> roleIdList;
}
