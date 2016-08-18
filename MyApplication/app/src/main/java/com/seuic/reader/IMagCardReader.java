package com.seuic.reader;

/**
 * Created by 16641 on 2016/8/1.
 email: mengliangliang@seuic.com
 */

public interface IMagCardReader {

    /**
     * 获取卡状态
     * @return 0正常，非0不正常
     */
    int getMagStatus();

    /**
     * 读取数据
     * @param Bank 磁道
     * @param Offset 数据地址偏移
     * @param Len 读取数据长度
     * @param Data 读取的数据
     * @return true成功，false失败
     */
    boolean readMagData(int Bank, int Offset, int Len, byte[] Data);

    /**
     * 写数据
     * @param Bank 磁道
     * @param Offset 数据地址偏移
     * @param Len 写入数据长度
     * @param Data 写入的数据
     * @return true成功，false失败
     */
    boolean writeMagData(int Bank, int Offset, int Len, byte[] Data);

    /**
     * 复位
     * @return true成功，false失败
     */
    boolean resetMag();
}