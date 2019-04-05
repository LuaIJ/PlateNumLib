package arains.com.library.core;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import arains.com.library.R;


public class KeyboardUtil {

    private EditText editText;
    private TextView[] mTextViews;
    private StringBuffer stringBuffer = new StringBuffer();
    private int count = 8;
    private String inputContent;
    //======

    private Activity mActivity;
    private KeyboardView mKeyboardView;
    private EditText mEdit;

    private int keyboard_view;

    public void setKeyboard_view(int keyboard_view) {
        this.keyboard_view = keyboard_view;
    }

    /**
     * 省份简称键盘
     */
    private Keyboard provinceKeyboard;
    /**
     * 数字与大写字母键盘
     */
    private Keyboard numberKeyboard;

    private InputView.InputCompleteListener inputCompleteListener;

    public KeyboardUtil(Activity activity, EditText edit, TextView[] textViews, InputView.InputCompleteListener inputCompleteListener, KeyboardView keyboard_view) {
        mActivity = activity;
        mEdit = edit;
        mTextViews = textViews;
        this.inputCompleteListener = inputCompleteListener;
        provinceKeyboard = new Keyboard(activity, R.xml.province);
        numberKeyboard = new Keyboard(activity, R.xml.number);
        mKeyboardView = keyboard_view;
        mKeyboardView.setKeyboard(provinceKeyboard);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(listener);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = mEdit.getText();
            int start = mEdit.getSelectionStart();
            //判定是否是中文的正则表达式 [\\u4e00-\\u9fa5]判断一个中文 [\\u4e00-\\u9fa5]+多个中文
            String reg = "[\\u4e00-\\u9fa5]";
            if (primaryCode == -1) {// 省份简称与数字键盘切换
                if (mEdit.getText().toString().matches(reg)) {
                    changeKeyboard(true);
                }
            } else if (primaryCode == -3) {
                onKeyDelete();
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
                // 判断第一个字符是否是中文,是，则自动切换到数字软键盘
                if (mEdit.getText().toString().matches(reg)) {
                    changeKeyboard(true);
                }
            }

            addText(editable, mEdit.getText().toString());// 追加字符到格子中
        }
    };

    private boolean isError = false;
    /**
     * 追加字符到格子中
     * @param str
     * @param editable
     */
    private void addText(Editable editable, String str) {
        if (inputCompleteListener != null && count < 7) {
            inputCompleteListener.inputIng(null, isError);
        }
        //如果字符不为""时才进行操作
        if (!editable.toString().equals("")) {
            if (stringBuffer.length() > 7) {
                //当文本长度大于3位时editText置空
                mEdit.setText("");
                return;
            } else {
                //将文字添加到StringBuffer中
                stringBuffer.append(editable);
                mEdit.setText("");//添加后将EditText置空
                count = stringBuffer.length();
                inputContent = stringBuffer.toString();
                if (stringBuffer.length() == 7) {
                    //文字长度位7  则调用不是新能源完成输入的监听
                    if (inputCompleteListener != null) {
                        inputCompleteListener.notNewEnergyInputComplete();
                    }
                }
                if (stringBuffer.length() == 8) {
                    //文字长度位8  则调用完成输入的监听
                    if (inputCompleteListener != null) {
                        inputCompleteListener.inputComplete();
                    }
                }
            }

            for (int i = 0; i < stringBuffer.length(); i++) {
                mTextViews[i].setText(String.valueOf(inputContent.charAt(i)));
//                mTextViews[i].setBackgroundResource(R.drawable.bg_user_verify_code_blue);
            }

        }

        if (stringBuffer.length()==2) {
            if (inputCompleteListener != null && !TextUtils.isEmpty(str) && isNumeric(str)) {
                isError = true;
                inputCompleteListener.inputError();
            } else {
                isError = false;
            }
        }
    }

    public String getText() {
        return inputContent;
    }

    public boolean onKeyDelete() {
        if (inputCompleteListener != null) {
            inputCompleteListener.deleteContent(isError, count);
        }
        if (count == 0) {
            count = 8;
            return true;
        }
        if (count == 1) {
            changeKeyboard(false);
        }
        if (stringBuffer.length() > 0) {
            //删除相应位置的字符
            stringBuffer.delete((count - 1), count);
            count--;
            inputContent = stringBuffer.toString();
            mTextViews[stringBuffer.length()].setText("");
//            mTextViews[stringBuffer.length()].setBackgroundResource(R.drawable.bg_user_verify_code_gray);
//            if (inputCompleteListener != null)
//                inputCompleteListener.deleteContent(true);//有删除就通知manger

        }
        return false;
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


    public void clearEditText () {
        stringBuffer.delete(0, stringBuffer.length());
        inputContent = stringBuffer.toString();
        for (int i = 0; i < mTextViews.length; i++) {
            mTextViews[i].setText("");
//            mTextViews[i].setBackgroundResource(R.drawable.bg_user_verify_code_gray);
        }
    }

    /**
     * 指定切换软键盘 isNumber false表示要切换为省份简称软键盘 true表示要切换为数字软键盘
     */
    public void changeKeyboard(boolean isNumber) {
        if (isNumber) {
            mKeyboardView.setKeyboard(numberKeyboard);
        } else {
            mKeyboardView.setKeyboard(provinceKeyboard);
        }
    }

    /**
     * 软键盘展示状态
     */
    public boolean isShow() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * 软键盘展示
     */
    public void showKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 软键盘隐藏
     */
    public void hideKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            mKeyboardView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 禁掉系统软键盘
     */
    public void hideSoftInputMethod() {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }
        if (methodName == null) {
            mEdit.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mEdit, false);
            } catch (NoSuchMethodException e) {
                mEdit.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
