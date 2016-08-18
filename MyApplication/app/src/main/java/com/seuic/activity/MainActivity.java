package com.seuic.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.seuic.reader.IC4442CardReader;
import com.seuic.reader.IICCardReader;
import com.seuic.reader.IMagCardReader;
import com.seuic.reader.PA1200Reader;

import java.util.Arrays;

public class MainActivity extends Activity implements
        NavigationView.OnNavigationItemSelectedListener,
        Toolbar.OnMenuItemClickListener,
        CompoundButton.OnCheckedChangeListener,
        NumberPicker.OnValueChangeListener,
        DialogInterface.OnClickListener {

    private static final int MAGCARD_TRACK1_LENGTH = 79;
    private static final int MAGCARD_TRACK2_LENGTH = 40;
    private static final int MAGCARD_TRACK3_LENGTH = 107;
    private static final int IC_CARD_LENGTH = 256;
    private static final String PF_GLOBAL_NAME = "com.seuic.application";
    private static final String PF_BOOLEAN_TRACK1 = "track1Enable";
    private static final String PF_BOOLEAN_TRACK2 = "track2Enable";
    private static final String PF_BOOLEAN_TRACK3 = "track3Enable";
    private static final String PF_INT_TRACK1_OFFSET = "numPk1Offset";
    private static final String PF_INT_TRACK1_LEN = "numPk1Len";
    private static final String PF_INT_TRACK2_OFFSET = "numPk2Offset";
    private static final String PF_INT_TRACK2_LEN = "numPk2Len";
    private static final String PF_INT_TRACK3_OFFSET = "numPk3Offset";
    private static final String PF_INT_TRACK3_LEN = "numPk3Len";
    private static final String PF_INT_TRACK3_WRITE_OFFSET = "numPkMagWriteOffset";
    private static final String PF_INT_TRACK3_WRITE_LEN = "numPkMagWriteLen";
    private static final String PF_INT_IC_READ_OFFSET = "ic_read_offset";
    private static final String PF_INT_IC_READ_LEN = "ic_read_len";
    private static final String PF_INT_IC_WRITE_OFFSET = "ic_write_offset";
    private static final String PF_INT_IC_WRITE_LEN = "ic_write_len";

    // 抽屉容器
    DrawerLayout layDrawer;
    // Toolbar相关
    Toolbar toolbarTop;
    // Snackbar相关
    private CoordinatorLayout layCoordinator;
    //磁卡
    View magView;
    private TextView tvMagCard;
    private IMagCardReader magCardReader;
    // 状态相关
    private boolean isNavigateMag = true;
    // 磁卡设置的dialog
    private AlertDialog altDlgMagSetting;
    private Switch switch1;
    private Switch switch2;
    private Switch switch3;
    private NumberPicker numPk1Offset;
    private NumberPicker numPk1Len;
    private NumberPicker numPk2Offset;
    private NumberPicker numPk2Len;
    private NumberPicker numPk3Offset;
    private NumberPicker numPk3Len;
    // 写磁卡的dialog
    private AlertDialog altDlgMagWrite;
    private NumberPicker numPkMagWriteOffset;
    private NumberPicker numPkMagWriteLen;
    private EditText etTrack3Write;
    private TextView tvTrack3Write;

    // IC卡
    View icView;
    private TextView tvICCard;
    private IICCardReader icCardReader;
    // IC卡密码已校验的状态标志
    private boolean icCardPwdChecked = false;
    // 写IC卡的剩余错误次数
    private int times = 0;
    // IC卡设置的dialog
    private AlertDialog altDlgICSetting;
    private NumberPicker numPkICOffset;
    private NumberPicker numPKICLen;
    // 写IC卡密码的dialog
    private EditText etPwd;
    private TextView tvPwd;
    // 写IC卡的dialog
    private AlertDialog altDlgICWrite;
    private NumberPicker numPkICWriteOffset;
    private NumberPicker numPkICWriteLen;
    private EditText etICWrite;
    private TextView tvICWrite;


    // 配置相关
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 横竖屏控制
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        setContentView(R.layout.activity_main);

        initData();
        initViews();
    }

    private void initData() {

        // 将toolbar设为当前Activity的ActionBar
        //setActionBar(toolbarTop);

        // 解码器初始化
        magCardReader = PA1200Reader.getInstance();
        icCardReader = IC4442CardReader.getInstance();

        // 配置参数初始化
        preferences = getSharedPreferences(PF_GLOBAL_NAME, MODE_PRIVATE);
    }

    private void initViews() {
        initToolbar();
        initDrawerLayout();
        // Snackbar相关
        layCoordinator = (CoordinatorLayout) findViewById(R.id.layout_coordinator);
        initMagView();
        initIcView();
        initMagSettingDialog();
        initMagWriteDialog();
        initICSettingDialog();
        initICWriteDialog();
    }

    private void initToolbar() {
        toolbarTop = (Toolbar) findViewById(R.id.toolbar);
        toolbarTop.setTitle(getString(R.string.tv_title_magcard));
        toolbarTop.inflateMenu(R.menu.menu_magcard);
        toolbarTop.setOnMenuItemClickListener(this);
    }

    private void initDrawerLayout() {
        // 抽屉容器
        layDrawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        // 抽屉触发器
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, layDrawer, toolbarTop, R.string.open, R.string.close);// DrawerLayout抽屉打开关闭效果
        actionBarDrawerToggle.syncState();
        layDrawer.setDrawerListener(actionBarDrawerToggle);

        // 抽屉布局
        ((NavigationView) findViewById(R.id.view_navigation)).setNavigationItemSelectedListener(this);
    }

    private void initMagView() {
        // 磁卡界面相关
        magView = findViewById(R.id.include_view_mag);
        tvMagCard = (TextView) findViewById(R.id.tv_magcard);
    }

    private void initIcView() {
        // ic卡界面相关
        icView = findViewById(R.id.include_view_ic);
        tvICCard = (TextView) findViewById(R.id.tv_iccard);
    }

    private void initMagSettingDialog() {
        altDlgMagSetting = new AlertDialog
                .Builder(MainActivity.this)
                .setTitle(R.string.settings)
                .setView(R.layout.dialog_mag_settings)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null)
                .create();
        altDlgMagSetting.create();

        switch1 = (Switch) altDlgMagSetting.findViewById(R.id.switch_track1);
        switch2 = (Switch) altDlgMagSetting.findViewById(R.id.switch_track2);
        switch3 = (Switch) altDlgMagSetting.findViewById(R.id.switch_track3);
        switch1.setChecked(preferences.getBoolean(PF_BOOLEAN_TRACK1, true));
        switch2.setChecked(preferences.getBoolean(PF_BOOLEAN_TRACK2, true));
        switch3.setChecked(preferences.getBoolean(PF_BOOLEAN_TRACK3, true));
        switch1.setOnCheckedChangeListener(this);
        switch2.setOnCheckedChangeListener(this);
        switch3.setOnCheckedChangeListener(this);

        numPk1Offset = (NumberPicker) altDlgMagSetting.findViewById(R.id.numPicker1_offset);
        numPk1Len = (NumberPicker) altDlgMagSetting.findViewById(R.id.numPicker1_len);
        numPk2Offset = (NumberPicker) altDlgMagSetting.findViewById(R.id.numPicker2_offset);
        numPk2Len = (NumberPicker) altDlgMagSetting.findViewById(R.id.numPicker2_len);
        numPk3Offset = (NumberPicker) altDlgMagSetting.findViewById(R.id.numPicker3_offset);
        numPk3Len = (NumberPicker) altDlgMagSetting.findViewById(R.id.numPicker3_len);
        //numPk1Offset.setFormatter(this);
        //numPk1Len.setFormatter(this);
        numPk1Offset.setOnValueChangedListener(this);
        numPk2Offset.setOnValueChangedListener(this);
        numPk3Offset.setOnValueChangedListener(this);

        numPk1Offset.setMaxValue(MAGCARD_TRACK1_LENGTH);
        numPk1Offset.setValue(preferences.getInt(PF_INT_TRACK1_OFFSET, 0));
        numPk1Offset.setMinValue(0);

        numPk1Len.setMaxValue(MAGCARD_TRACK1_LENGTH - numPk1Offset.getValue());
        numPk1Len.setValue(preferences.getInt(PF_INT_TRACK1_LEN, MAGCARD_TRACK1_LENGTH));
        numPk1Len.setMinValue(0);

        numPk2Offset.setMaxValue(MAGCARD_TRACK2_LENGTH);
        numPk2Offset.setValue(preferences.getInt(PF_INT_TRACK2_OFFSET, 0));
        numPk2Offset.setMinValue(0);

        numPk2Len.setMaxValue(MAGCARD_TRACK2_LENGTH - numPk2Offset.getValue());
        numPk2Len.setValue(preferences.getInt(PF_INT_TRACK2_LEN, MAGCARD_TRACK2_LENGTH));
        numPk2Len.setMinValue(0);

        numPk3Offset.setMaxValue(MAGCARD_TRACK3_LENGTH);
        numPk3Offset.setValue(preferences.getInt(PF_INT_TRACK3_OFFSET, 0));
        numPk3Offset.setMinValue(0);

        numPk3Len.setMaxValue(MAGCARD_TRACK3_LENGTH - numPk3Offset.getValue());
        numPk3Len.setValue(preferences.getInt(PF_INT_TRACK3_LEN, MAGCARD_TRACK3_LENGTH));
        numPk3Len.setMinValue(0);

        numPk1Offset.setWrapSelectorWheel(false);// 禁止循环显示
        numPk1Len.setWrapSelectorWheel(false);
        numPk2Offset.setWrapSelectorWheel(false);
        numPk2Len.setWrapSelectorWheel(false);
        numPk3Offset.setWrapSelectorWheel(false);
        numPk3Len.setWrapSelectorWheel(false);
        numPk1Offset.setEnabled(switch1.isChecked());
        numPk1Len.setEnabled(switch1.isChecked());
        numPk2Offset.setEnabled(switch2.isChecked());
        numPk2Len.setEnabled(switch2.isChecked());
        numPk3Offset.setEnabled(switch3.isChecked());
        numPk3Len.setEnabled(switch3.isChecked());
    }

    private void initMagWriteDialog() {
        altDlgMagWrite = new AlertDialog
                .Builder(MainActivity.this)
                .setTitle(R.string.btn_magcard_write)
                .setView(R.layout.dialog_wirte_track3)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null)
                .create();
        altDlgMagWrite.create();

        numPkMagWriteOffset = (NumberPicker) altDlgMagWrite.findViewById(R.id.dialog_write_mag_offset);
        numPkMagWriteOffset.setOnValueChangedListener(this);
        numPkMagWriteOffset.setMaxValue(MAGCARD_TRACK3_LENGTH);
        numPkMagWriteOffset.setValue(preferences.getInt(PF_INT_TRACK3_WRITE_OFFSET, 0));
        numPkMagWriteOffset.setMinValue(0);
        numPkMagWriteOffset.setWrapSelectorWheel(false);

        numPkMagWriteLen = (NumberPicker) altDlgMagWrite.findViewById(R.id.dialog_write_mag_len);
        numPkMagWriteLen.setOnValueChangedListener(this);
        numPkMagWriteLen.setMaxValue(MAGCARD_TRACK3_LENGTH - numPkMagWriteOffset.getValue());
        numPkMagWriteLen.setValue(preferences.getInt(PF_INT_TRACK3_WRITE_LEN, MAGCARD_TRACK3_LENGTH));
        numPkMagWriteLen.setMinValue(0);
        numPkMagWriteLen.setWrapSelectorWheel(false);

        tvTrack3Write = (TextView) altDlgMagWrite.findViewById(R.id.dialog_write_mag_textView_content);
        tvTrack3Write.setText(getString(R.string.write_track3_text, numPkMagWriteLen.getValue()));

        etTrack3Write = (EditText) altDlgMagWrite.findViewById(R.id.dialog_write_mag_editText_content);
        etTrack3Write.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvTrack3Write.setText(getString(R.string.write_track3_text, numPkMagWriteLen.getValue() - s.length()));

                if (temp.length() > numPkMagWriteLen.getValue()) {
                    s.delete(etTrack3Write.getSelectionStart() - 1, etTrack3Write.getSelectionEnd());
                    etTrack3Write.setText(s);
                    etTrack3Write.setSelection(etTrack3Write.length());
                    //int tempSelection = selectionEnd;
                    //etTrack3Write.setSelection(tempSelection);
                }
            }
        });
    }

    private void initICSettingDialog() {
        altDlgICSetting = new AlertDialog
                .Builder(MainActivity.this)
                .setTitle(R.string.settings)
                .setView(R.layout.dialog_ic_settings)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null)
                .create();
        altDlgICSetting.create();

        numPkICOffset = (NumberPicker) altDlgICSetting.findViewById(R.id.ic_numPicker1_offset);
        numPKICLen = (NumberPicker) altDlgICSetting.findViewById(R.id.ic_numPicker1_len);
        numPkICOffset.setOnValueChangedListener(this);
        numPkICOffset.setMaxValue(IC_CARD_LENGTH);
        numPkICOffset.setValue(preferences.getInt(PF_INT_IC_READ_OFFSET, 0));
        numPkICOffset.setMinValue(0);
        numPKICLen.setMaxValue(IC_CARD_LENGTH - numPk1Offset.getValue());
        numPKICLen.setValue(preferences.getInt(PF_INT_IC_READ_LEN, IC_CARD_LENGTH));
        numPKICLen.setMinValue(0);
        numPkICOffset.setWrapSelectorWheel(false);
        numPKICLen.setWrapSelectorWheel(false);
    }

    private void initICWriteDialog() {
        altDlgICWrite = new AlertDialog
                .Builder(MainActivity.this)
                .setTitle(R.string.btn_iccard_write)
                .setView(R.layout.dialog_ic_write)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null)
                .create();
        altDlgICWrite.create();

        numPkICWriteOffset = (NumberPicker) altDlgICWrite.findViewById(R.id.dialog_write_ic_offset);
        numPkICWriteOffset.setOnValueChangedListener(this);
        numPkICWriteOffset.setMaxValue(IC_CARD_LENGTH);
        numPkICWriteOffset.setValue(preferences.getInt(PF_INT_IC_WRITE_OFFSET, 0));
        numPkICWriteOffset.setMinValue(0);
        numPkICWriteOffset.setWrapSelectorWheel(false);

        numPkICWriteLen = (NumberPicker) altDlgICWrite.findViewById(R.id.dialog_write_ic_len);
        numPkICWriteLen.setOnValueChangedListener(this);
        numPkICWriteLen.setMaxValue(IC_CARD_LENGTH - numPkICOffset.getValue());
        numPkICWriteLen.setValue(preferences.getInt(PF_INT_IC_WRITE_LEN, IC_CARD_LENGTH));
        numPkICWriteLen.setMinValue(0);
        numPkICWriteLen.setWrapSelectorWheel(false);

        tvICWrite = (TextView) altDlgICWrite.findViewById(R.id.dialog_write_ic_textView_content);
        tvICWrite.setText(getString(R.string.write_track3_text, numPkICWriteLen.getValue()));

        etICWrite = (EditText) altDlgICWrite.findViewById(R.id.dialog_write_ic_editText_content);
        etICWrite.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvICWrite.setText(getString(R.string.write_track3_text, numPkICWriteLen.getValue() - s.length()));

                if (temp.length() > numPkICWriteLen.getValue()) {
                    s.delete(etICWrite.getSelectionStart() - 1, etICWrite.getSelectionEnd());
                    etICWrite.setText(s);
                    etICWrite.setSelection(etICWrite.length());
                }
            }
        });
    }

    /*
    菜单按钮监听
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                if (isNavigateMag) {
                    numPk1Offset.setValue(preferences.getInt(PF_INT_TRACK1_OFFSET, 0));
                    numPk1Len.setValue(preferences.getInt(PF_INT_TRACK1_LEN, MAGCARD_TRACK1_LENGTH));
                    numPk2Offset.setValue(preferences.getInt(PF_INT_TRACK2_OFFSET, 0));
                    numPk2Len.setValue(preferences.getInt(PF_INT_TRACK2_LEN, MAGCARD_TRACK2_LENGTH));
                    numPk3Offset.setValue(preferences.getInt(PF_INT_TRACK3_OFFSET, 0));
                    numPk3Len.setValue(preferences.getInt(PF_INT_TRACK3_LEN, MAGCARD_TRACK3_LENGTH));
                    switch1.setChecked(preferences.getBoolean(PF_BOOLEAN_TRACK1, true));
                    switch2.setChecked(preferences.getBoolean(PF_BOOLEAN_TRACK2, true));
                    switch3.setChecked(preferences.getBoolean(PF_BOOLEAN_TRACK3, true));
                    numPk1Offset.setEnabled(switch1.isChecked());
                    numPk1Len.setEnabled(switch1.isChecked());
                    numPk2Offset.setEnabled(switch2.isChecked());
                    numPk2Len.setEnabled(switch2.isChecked());
                    numPk3Offset.setEnabled(switch3.isChecked());
                    numPk3Len.setEnabled(switch3.isChecked());

                    altDlgMagSetting.show();
                } else {
                    numPkICOffset.setValue(preferences.getInt(PF_INT_IC_READ_OFFSET, 0));
                    numPKICLen.setValue(preferences.getInt(PF_INT_IC_READ_LEN, IC_CARD_LENGTH));

                    altDlgICSetting.show();
                }
                break;
        }
        return true;
    }

    /*
    左侧导航栏监听
    */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_magcard:
                switchToMagCardView();
                break;
            case R.id.menu_iccard:
                switchToIcCardView();
                break;
        }
        layDrawer.closeDrawers();
        return true;
    }

    private void switchToMagCardView() {
        if (!isNavigateMag) {
            toolbarTop.setTitle(getString(R.string.tv_title_magcard));
            magView.setVisibility(View.VISIBLE);
            icView.setVisibility(View.INVISIBLE);
            isNavigateMag = true;
        }
    }

    private void switchToIcCardView() {
        if (isNavigateMag) {
            toolbarTop.setTitle(getString(R.string.tv_title_iccard));
            magView.setVisibility(View.INVISIBLE);
            icView.setVisibility(View.VISIBLE);
            isNavigateMag = false;
        }
    }

    /*
    switch监听
    */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_track1:
                if (buttonView.isChecked()) {
                    //状态控制
                    numPk1Len.setEnabled(true);
                    numPk1Offset.setEnabled(true);
                } else {
                    numPk1Len.setEnabled(false);
                    numPk1Offset.setEnabled(false);
                }
                break;

            case R.id.switch_track2:
                if (buttonView.isChecked()) {
                    numPk2Len.setEnabled(true);
                    numPk2Offset.setEnabled(true);
                } else {
                    numPk2Len.setEnabled(false);
                    numPk2Offset.setEnabled(false);
                }
                break;

            case R.id.switch_track3:

                if (buttonView.isChecked()) {
                    numPk3Len.setEnabled(true);
                    numPk3Offset.setEnabled(true);
                } else {
                    numPk3Len.setEnabled(false);
                    numPk3Offset.setEnabled(false);
                }
                break;
        }
    }

    /*
    numberPicker监听
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.numPicker1_offset:
                numPk1Len.setMaxValue(MAGCARD_TRACK1_LENGTH - picker.getValue());
                break;
            case R.id.numPicker2_offset:
                numPk2Len.setMaxValue(MAGCARD_TRACK2_LENGTH - picker.getValue());
                break;
            case R.id.numPicker3_offset:
                numPk3Len.setMaxValue(MAGCARD_TRACK3_LENGTH - picker.getValue());
                break;

            case R.id.dialog_write_mag_offset:
                numPkMagWriteLen.setMaxValue(MAGCARD_TRACK3_LENGTH - picker.getValue());
                tvTrack3Write.setText(getString(R.string.write_track3_text, numPkMagWriteLen.getValue() - etTrack3Write.length()));
                break;
            case R.id.dialog_write_mag_len:
                tvTrack3Write.setText(getString(R.string.write_track3_text, picker.getValue() - etTrack3Write.length()));
                break;

            case R.id.ic_numPicker1_offset:
                numPKICLen.setMaxValue(IC_CARD_LENGTH - picker.getValue());
                break;

            case R.id.dialog_write_ic_offset:
                numPkICWriteLen.setMaxValue(IC_CARD_LENGTH - picker.getValue());
                break;
            case R.id.dialog_write_ic_len:
                tvICWrite.setText(getString(R.string.write_track3_text, picker.getValue() - etICWrite.length()));
                break;
        }
    }

    /* dialog button 监听 */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (dialog.equals(altDlgMagSetting)) {
            // 保存配置
            preferences.edit().putBoolean(PF_BOOLEAN_TRACK1, switch1.isChecked()).apply();
            preferences.edit().putBoolean(PF_BOOLEAN_TRACK2, switch2.isChecked()).apply();
            preferences.edit().putBoolean(PF_BOOLEAN_TRACK3, switch3.isChecked()).apply();
            preferences.edit().putInt(PF_INT_TRACK1_OFFSET, numPk1Offset.getValue()).apply();
            preferences.edit().putInt(PF_INT_TRACK1_LEN, numPk1Len.getValue()).apply();
            preferences.edit().putInt(PF_INT_TRACK2_OFFSET, numPk2Offset.getValue()).apply();
            preferences.edit().putInt(PF_INT_TRACK2_LEN, numPk2Len.getValue()).apply();
            preferences.edit().putInt(PF_INT_TRACK3_OFFSET, numPk3Offset.getValue()).apply();
            preferences.edit().putInt(PF_INT_TRACK3_LEN, numPk3Len.getValue()).apply();
        } else if (dialog.equals(altDlgICSetting)) {
            preferences.edit().putInt(PF_INT_IC_READ_OFFSET, numPkICOffset.getValue()).apply();
            preferences.edit().putInt(PF_INT_IC_READ_LEN, numPKICLen.getValue()).apply();
        } else if (dialog.equals(altDlgMagWrite)) {
            // 长度不够
            if (etTrack3Write.length() < numPkMagWriteLen.getValue()) {
                Snackbar.make(layCoordinator, R.string.length_too_short, Snackbar.LENGTH_LONG).show();
            } else {
                //TODO:此句可能需要进一步商讨
                if (magCardReader.writeMagData(3, numPkMagWriteOffset.getValue(), numPkMagWriteLen.getValue(), String.valueOf(etTrack3Write.getText()).getBytes())) {
                    Snackbar.make(layCoordinator, R.string.tips_write_succeed, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(layCoordinator, R.string.tips_write_failed, Snackbar.LENGTH_LONG).show();
                }

                preferences.edit().putInt(PF_INT_TRACK3_WRITE_OFFSET, numPkMagWriteOffset.getValue()).apply();
                preferences.edit().putInt(PF_INT_TRACK3_WRITE_LEN, numPkMagWriteLen.getValue()).apply();
//              preferences.edit().putString("track3_write_edittext", etTrack3Write.getText().toString()).apply();
            }
        } else if (dialog.equals(altDlgICWrite)) {
            if (etICWrite.length() < numPkICWriteLen.getValue()) {
                Snackbar.make(layCoordinator, R.string.length_too_short, Snackbar.LENGTH_LONG).show();
            } else {
                //TODO:为什么每次写ic卡都要密码？
                if (icCardReader.writeICData(new byte[1], numPkICWriteOffset.getValue(), numPkICWriteLen.getValue(), String.valueOf(etICWrite.getText()).getBytes())) {
                    Snackbar.make(layCoordinator, R.string.tips_write_succeed, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(layCoordinator, R.string.tips_write_failed, Snackbar.LENGTH_LONG).show();
                }
                preferences.edit().putInt(PF_INT_IC_WRITE_OFFSET, numPkICWriteOffset.getValue()).apply();
                preferences.edit().putInt(PF_INT_IC_WRITE_LEN, numPkICWriteLen.getValue()).apply();
            }
        }
    }

    public void findFuncByView(View view) {
        switch (view.getId()) {
            case R.id.btn_magcard_read:
                magCardRead(view);
                break;
            case R.id.btn_magcard_write:
                magCardWrite(view);
                break;
            case R.id.btn_magcard_reset:
                magCardReset(view);
                break;

            case R.id.btn_iccard_read:
                icCardRead(view);
                break;
            case R.id.btn_iccard_write:
                icCardWrite(view);
                break;
            case R.id.btn_iccard_reset:
                icCardReset(view);
                break;
        }
    }

    public void showSnackError(final View view, int stringId) {
        final Snackbar snackbar = Snackbar.make(layCoordinator, stringId, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                findFuncByView(view);
            }
        });
        snackbar.show();
    }

    /*
    磁卡按钮监听
     */
    public boolean isMagCardReady(final View view) {
        if (magCardReader.getMagStatus() != 0) {
            final Snackbar snackbar = Snackbar.make(layCoordinator, getString(R.string.tv_magcard_tips), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                    findFuncByView(view);
                }
            });
            snackbar.show();
            return false;
        }
        return true;
    }

    public void magCardReset(final View view) {
        if (isMagCardReady(view)) {
            if (magCardReader.resetMag()) {
                Snackbar.make(layCoordinator, R.string.tips_reset_succeed, Snackbar.LENGTH_SHORT).show();
            } else {
                showSnackError(view, R.string.tips_reset_failed);
            }
        }
    }

    public void magCardRead(final View view) {
        if (isMagCardReady(view)) {
            String result = "";
            boolean isSucceed = true;
            if (preferences.getBoolean(PF_BOOLEAN_TRACK1, true)) {
                byte[] data = new byte[preferences.getInt(PF_INT_TRACK1_LEN, MAGCARD_TRACK1_LENGTH)];
                if (magCardReader.readMagData(1, preferences.getInt(PF_INT_TRACK1_OFFSET, 0), preferences.getInt(PF_INT_TRACK1_LEN, MAGCARD_TRACK1_LENGTH), data))
                    result += getString(R.string.tv_track1, new String(data));
                else {
                    isSucceed = false;
                }
            }
            if (preferences.getBoolean(PF_BOOLEAN_TRACK2, true)) {
                byte[] data = new byte[preferences.getInt(PF_INT_TRACK2_LEN, MAGCARD_TRACK2_LENGTH)];
                if (magCardReader.readMagData(2, preferences.getInt(PF_INT_TRACK2_OFFSET, 0), preferences.getInt(PF_INT_TRACK2_LEN, MAGCARD_TRACK2_LENGTH), data))
                    result += getString(R.string.tv_track2, new String(data));
                else {
                    isSucceed = false;
                }
            }
            if (preferences.getBoolean(PF_BOOLEAN_TRACK3, true)) {
                byte[] data = new byte[preferences.getInt(PF_INT_TRACK3_LEN, MAGCARD_TRACK3_LENGTH)];
                if (magCardReader.readMagData(3, preferences.getInt(PF_INT_TRACK3_OFFSET, 0), preferences.getInt(PF_INT_TRACK3_LEN, MAGCARD_TRACK3_LENGTH), data))
                    result += getString(R.string.tv_track3, new String(data));
                else {
                    isSucceed = false;
                }
            }
            if (!isSucceed) {
                showSnackError(view, R.string.tips_read_error);
            } else {
                tvMagCard.setText(result);
            }
        }
    }

    public void magCardWrite(final View view) {
        if (isMagCardReady(view)) {
            numPkMagWriteOffset.setValue(preferences.getInt(PF_INT_TRACK3_WRITE_OFFSET, 0));
            numPkMagWriteLen.setValue(preferences.getInt(PF_INT_TRACK3_WRITE_LEN, MAGCARD_TRACK3_LENGTH));
            tvTrack3Write.setText(getString(R.string.write_track3_text, numPkMagWriteLen.getValue()));
            altDlgMagWrite.show();
        }
    }


    /* IC卡功能实现 */
    public boolean isICCardReady(final View view) {
        if (icCardReader.getICStatus() != 0) {
            final Snackbar snackbar = Snackbar.make(layCoordinator, R.string.tv_iccard_tips, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:这句真的有用吗？
                    snackbar.dismiss();
                    findFuncByView(view);
                }
            });
            snackbar.show();
            return false;
        }
        return true;
    }

    public void icCardReset(final View view) {
        if (isICCardReady(view)) {
            if (icCardReader.resetIC()) {
                Snackbar.make(layCoordinator, R.string.tips_reset_succeed, Snackbar.LENGTH_LONG).show();
            } else {
                showSnackError(view, R.string.tips_reset_failed);
            }
        }
    }

    public void icCardRead(final View view) {
        if (isICCardReady(view)) {
            byte[] data = new byte[preferences.getInt(PF_INT_IC_READ_LEN, IC_CARD_LENGTH)];
            if (icCardReader.readICData(preferences.getInt(PF_INT_IC_READ_OFFSET, 0), preferences.getInt(PF_INT_IC_READ_LEN, IC_CARD_LENGTH), data)) {
                tvICCard.setText(Arrays.toString(data));
            } else {
                showSnackError(view, R.string.tips_read_error);
            }
        }
    }

    public void initICPwdDialog() {
        // 密码校验
        AlertDialog alertDialog = new AlertDialog
                .Builder(MainActivity.this)
                .setTitle(getString(R.string.dialog_iccard_title, times))
                .setView(R.layout.dialog_ic_pwd)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etPwd.length() < 24) {// 输入长度判断
                            dialog.dismiss();
                            Snackbar.make(layCoordinator, R.string.length_too_short, Snackbar.LENGTH_LONG).show();
                        } else {
                            if (icCardReader.checkICPwd(etPwd.getText().toString().getBytes())) {// 密码判断
                                icCardPwdChecked = true;
                            } else {
                                Snackbar.make(layCoordinator, R.string.iccard_pass_wrong, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        alertDialog.show();

        tvPwd = (TextView) alertDialog.findViewById(R.id.dialog_ic_pwd_textview);
        tvPwd.setText(getString(R.string.tv_pwd, 24));
        etPwd = (EditText) alertDialog.findViewById(R.id.dialog_ic_pwd_edittext);
        etPwd.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvPwd.setText(getString(R.string.tv_pwd, 24 - s.length()));

                if (temp.length() > 24) {
                    s.delete(etPwd.getSelectionStart() - 1, etPwd.getSelectionEnd());
                    etPwd.setText(s);
                }
            }
        });
    }

    public void icCardWrite(View view) {
        if (isICCardReady(view)) {
            if (!icCardPwdChecked) {
                times = icCardReader.getRemainTimes();
                if (times <= 0) {// 剩余次数判断
                    Snackbar.make(layCoordinator, R.string.iccard_locked, Snackbar.LENGTH_LONG).show();
                } else {
                    initICPwdDialog();
                }
            } else {
                // 直接写
                numPkICWriteOffset.setValue(preferences.getInt(PF_INT_IC_WRITE_OFFSET, 0));
                numPkICWriteLen.setValue(preferences.getInt(PF_INT_IC_WRITE_LEN, IC_CARD_LENGTH));
                tvICWrite.setText(getString(R.string.write_track3_text, numPkICWriteLen.getValue()));
                altDlgICWrite.show();
            }
        }
    }
}