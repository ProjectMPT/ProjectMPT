package com.projectmpt.projectmpt;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

public class FirstFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static FirstFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);




        EditText editText = (EditText) view.findViewById(R.id.txtNeed);

        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((MeetActivity)FirstFragment.this.getActivity()).needTxt = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        EditText editTextShort = (EditText) view.findViewById(R.id.txtNeedShort);

        editTextShort.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextShort.setRawInputType(InputType.TYPE_CLASS_TEXT);


        editTextShort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((MeetActivity)FirstFragment.this.getActivity()).headingTxt = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


     return view;
    }




}