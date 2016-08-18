package com.seuic.reader;

/**
 * Created by 16641 on 2016/8/13.
 * email: mengliangliang@seuic.com
 */

public interface IICCardReader {

    /**
     * 获取卡状态
     *
     * @return 0正常，非0不正常
     */
    int getICStatus();

    /**
     * 读数据
     *
     * @param Offset 数据地址偏移
     * @param Len    读取数据长度
     * @param Data   读取数据长度
     * @return true成功，false失败
     */
    boolean readICData(int Offset, int Len, byte[] Data);

    /**
     * 写数据
     *
     * @param PassWord 密码
     * @param Offset   数据地址偏移
     * @param Len      写入数据长度
     * @param Data     写入的数据
     * @return true成功，false失败
     */
    boolean writeICData(byte[] PassWord, int Offset, int Len, byte[] Data);

    /**
     * 复位
     *
     * @return true成功，false失败
     */
    boolean resetIC();

    /**
     * 密码校验
     *
     * @param PassWord 密码
     * @return true成功，false失败
     */
    boolean checkICPwd(byte[] PassWord);

    /**
     * 剩余错误重试次数
     * @return 0,1,2,3
     */
    int getRemainTimes();
}
