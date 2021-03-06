package com.cwenhui.chowhound.widget;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.chowhound.R;

public class SearchView extends LinearLayout implements View.OnClickListener, OnEditorActionListener, TextWatcher {

    /**
     * 输入框
     */
    private EditText input;

    /**
     * 删除键
     */
    private ImageView delete;

    /**
     * 返回按钮
     */
    private Button back;

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 搜索回调接口
     */
    private SearchViewListener mListener;

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_search, this);
        initViews();
    }

    private void initViews() {
        input = (EditText) findViewById(R.id.et_search_input);
        delete = (ImageView) findViewById(R.id.iv_search_delete);
        back = (Button) findViewById(R.id.btn_search_back);

        delete.setOnClickListener(this);
        back.setOnClickListener(this);
        input.setOnClickListener(this);
        input.addTextChangedListener(this);
        input.setOnEditorActionListener(this);
    }

    /**
     * 通知监听者 进行搜索操作
     * @param text
     */
    private void notifyStartSearching(String text){
        if (mListener != null) {
            mListener.onSearch(input.getText().toString());
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search_delete:
            	if (mListener != null) {
            		mListener.onInput();
            	}
                input.setText("");
                delete.setVisibility(GONE);
                break;
            case R.id.btn_search_back:
                ((Activity) mContext).finish();
                break;
                
            case R.id.et_search_input:
            	if (mListener != null) {
            		mListener.onInput();
            	}
            	break;
        }
    }

    /**
     * search view回调方法
     */
    public interface SearchViewListener {

        /**
         * 开始搜索
         * @param text 传入输入框的文本
         */
        void onSearch(String text);
        
        void onInput();

    }

	@Override
	public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            notifyStartSearching(input.getText().toString());
        }
        return true;
	}

	@Override
	public void afterTextChanged(Editable arg0) {}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onTextChanged(CharSequence charSequence, int arg1, int arg2, int arg3) {
		if (!"".equals(charSequence.toString())) {
            delete.setVisibility(VISIBLE);
        }
        else {
            delete.setVisibility(GONE);
        }
	}

}
