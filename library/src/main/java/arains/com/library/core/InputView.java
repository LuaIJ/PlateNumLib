package arains.com.library.core;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import arains.com.library.R;

/**
 * Created by Arains on 2019/4/5.
 */

public class InputView extends RelativeLayout{

    private EditText editText;
    private TextView[] TextViews;
    private StringBuffer stringBuffer = new StringBuffer();
    private int count = 4;
    private String inputContent;
    private Context context;
    private KeyboardView keyboardView;

    public InputView(Context context) {
        this(context, null);
        this.context = context;
    }

    public InputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public InputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TextViews = new TextView[8];
        View.inflate(context, R.layout.view_code, this);

        editText = (EditText) findViewById(R.id.et);
        TextViews[0] = (TextView) findViewById(R.id.item_code_iv1);
        TextViews[1] = (TextView) findViewById(R.id.item_code_iv2);
        TextViews[2] = (TextView) findViewById(R.id.item_code_iv3);
        TextViews[3] = (TextView) findViewById(R.id.item_code_iv4);
        TextViews[4] = (TextView) findViewById(R.id.item_code_iv5);
        TextViews[5] = (TextView) findViewById(R.id.item_code_iv6);
        TextViews[6] = (TextView) findViewById(R.id.item_code_iv7);
        TextViews[7] = (TextView) findViewById(R.id.item_code_iv8);

        editText.setCursorVisible(false);//将光标隐藏

        setListener();
    }

    public void init() {
        if (keyboardUtil == null) {
            keyboardUtil = new KeyboardUtil((Activity) context, editText, TextViews, inputCompleteListener,keyboardView);
            keyboardUtil.hideSoftInputMethod();
            keyboardUtil.showKeyboard();
        } else {
            keyboardUtil.showKeyboard();
        }
    }

    private KeyboardUtil keyboardUtil;

    private void setListener() {

        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (keyboardUtil == null) {
                    keyboardUtil = new KeyboardUtil((Activity) context, editText, TextViews, inputCompleteListener, keyboardView);
                    keyboardUtil.hideSoftInputMethod();
                    keyboardUtil.showKeyboard();
                } else {
                    keyboardUtil.showKeyboard();
                }
                return false;
            }
        });

    }

    public String getText() {
        if (keyboardUtil != null) {
            return keyboardUtil.getText();
        } else {
            return "";
        }
    }


    public void hideKeyboard() {
        if (keyboardUtil != null) {
            keyboardUtil.hideKeyboard();
        }
    }

    public void showKeyboard() {
        if (keyboardUtil != null) {
            keyboardUtil.showKeyboard();
        }
    }

    /**
     * 清空输入内容
     */
    public void clearEditText() {
        if (keyboardUtil != null) {
            keyboardUtil.clearEditText();
        }
    }

    public void changeKeyboard(boolean isNumber) {
        if (keyboardUtil != null) {
            keyboardUtil.changeKeyboard(isNumber);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private InputCompleteListener inputCompleteListener;

    public InputView setInputCompleteListener(KeyboardView mKeyboardView, InputCompleteListener inputCompleteListener) {
        this.inputCompleteListener = inputCompleteListener;
        this.keyboardView = mKeyboardView;
        return this;
    }

    public interface InputCompleteListener {
        void notNewEnergyInputComplete();

        void inputComplete();

        void inputIng(String text, boolean isError);

        void deleteContent(boolean isError, int count);

        void inputError();
    }

    /**
     * 获取输入文本
     *
     * @return
     */
    public String getEditContent() {
        return inputContent;
    }
}
