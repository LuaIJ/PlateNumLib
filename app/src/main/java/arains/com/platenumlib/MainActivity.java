package arains.com.platenumlib;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import arains.com.library.core.InputView;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private InputView mEditCode;
    private KeyboardView mKeyboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditCode = this.findViewById(R.id.edit_code);
        mKeyboardView = this.findViewById(R.id.keyboard_view);
        init();
    }

    private void init() {
        /**
         * 监听输入 (没错就这一行代码即可集成，无需管内部如何实现，你只需在回调中做你的业务逻辑即可)
         */
        mEditCode.setInputCompleteListener(mKeyboardView, new InputView.InputCompleteListener() {
            @Override
            public void notNewEnergyInputComplete() {Log.e(TAG+"----->", "notNewEnergyInputComplete");}

            @Override
            public void inputComplete() {Log.e(TAG+"- ---->", "inputComplete输入完成就会回调这里");}

            @Override
            public void inputIng(String text, boolean isError) {Log.e(TAG+"----->", "inputIng输入中就会回调这里");}

            @Override
            public void deleteContent(boolean isError, int count) {Log.e(TAG+"----->", "deleteContent删除就会回调这里");}

            @Override
            public void inputError() {Log.e(TAG+"----->", "inputError");}
        }).init();// 初始化显示
    }
}
