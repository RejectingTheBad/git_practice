package com.seuic.reader;

import java.util.Random;

/**
 * Created by 16641 on 2016/8/15.
 * email: mengliangliang@seuic.com
 */

public class IC4442CardReader implements IICCardReader {

    private int remainTimes = 3;

    public static IC4442CardReader getInstance() {
        return instance;
    }

    private static IC4442CardReader instance = new IC4442CardReader();

    private IC4442CardReader() {
    }

    @Override
    public int getICStatus() {
        return 0;
    }

    @Override
    public boolean readICData(int Offset, int Len, byte[] Data) {
        new Random().nextBytes(Data);
        return true;
    }

    @Override
    public boolean writeICData(byte[] PassWord, int Offset, int Len, byte[] Data) {
        return false;
    }

    @Override
    public boolean resetIC() {
        return true;
    }

    @Override
    public boolean checkICPwd(byte[] PassWord) {
        if (remainTimes > 0) remainTimes--;
        return true;
    }

    @Override
    public int getRemainTimes() {
        return remainTimes;
    }
}
