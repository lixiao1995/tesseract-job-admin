package com.kevin.message.protpcpl.test.serialize;

import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.serialize.ISerialize;
import com.kevin.message.protocol.serialize.impl.Hessian2Serializer;
import org.junit.Test;

/**
 * @projectName: scepter
 * @className: Hessian2Test
 * @description:
 * @author: liangxuekai
 * @createDate: 2019-08-22 21:08
 * @updateUser: liangxuekai
 * @updateDate: 2019-08-22 21:08
 * @updateRemark: 修改内容
 * @version: 1.0
 */
public class Hessian2Test {

    private ISerialize serialize = new Hessian2Serializer();

    @Test
    public void Hessian2Test1(){

        try {
            byte[] serialize = this.serialize.serialize(new Protocol());
            for (int i = 0; i < serialize.length; i++) {
                System.out.println(serialize[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
