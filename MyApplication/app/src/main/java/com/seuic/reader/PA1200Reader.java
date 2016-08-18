package com.seuic.reader;

import android.util.Log;

import com.seuic.reader.IMagCardReader;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by 16641 on 2016/8/2.
 * email: mengliangliang@seuic.com
 */

public class PA1200Reader implements IMagCardReader {
    private static final String TAG = PA1200Reader.class.getSimpleName();

    private byte[] track1Data = new byte[79];
    private byte[] track2Data = new byte[40];
    private byte[] track3Data = new byte[107];

    private static final PA1200Reader pa1200Reader = new PA1200Reader();

    public static PA1200Reader getInstance() {
        return pa1200Reader;
    }

    private PA1200Reader() {
        /*
            总感觉nextBytes()不随机
         */
        new Random().nextBytes(track1Data);
        new Random().nextBytes(track2Data);
        new Random().nextBytes(track3Data);
    }

    @Override
    public int getMagStatus() {
        /*
            Math.random()*n is bad than Random.nextInt(n) in most instances
         */
        return new Random().nextInt(2);
    }

    @Override
    public boolean readMagData(int Bank, int Offset, int Len, byte[] Data) {

        if (Bank < 0 || Bank > 3 || Offset < 0 || Len < 0 || Data.length < Len)
            return false;
        else {
        /*
        String tmp = "";
        for (int i = 0; i < len; i++) {
            tmp += '0' + (int) (Math.random() % 10);
            tmp += '0' + (int) (Math.random() % ('z' - '0'));
        }
        return tmp;
        */
            switch (Bank) {
                case 1:
                    System.arraycopy(track1Data, Offset, Data, 0, Len);
                    break;
                case 2:
                    System.arraycopy(track2Data, Offset, Data, 0, Len);
                    break;
                case 3:
                    System.arraycopy(track3Data, Offset, Data, 0, Len);
                    break;
            }
            return true;
        }
    }

    @Override
    public boolean writeMagData(int Bank, int Offset, int Len, byte[] Data) {
        if (Bank != 3) {
            return false;

        } else {
            Log.d(TAG, String.format("%s has been write to magCard.", Arrays.toString(Data)));
        }
        return true;
    }

    @Override
    public boolean resetMag() {
        return new Random().nextBoolean();
    }
}
