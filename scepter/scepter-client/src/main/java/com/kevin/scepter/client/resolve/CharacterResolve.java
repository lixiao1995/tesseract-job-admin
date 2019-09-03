package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: 字符类型处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:00
 */
public class CharacterResolve implements IResolve<Character> {

    @Override
    public Class<Character> getResolveClass() {
        return Character.class;
    }

    @Override
    public Character resolve(String res) {
        if (res == null || res.length() == 0) {
            throw new IllegalArgumentException("Cannot convert empty string to char.");
        }

        return Character.valueOf(res.charAt(0));
    }

}
