package org.duckdns.nick2.connstatus.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.duckdns.nick2.connstatus.Global;
import org.duckdns.nick2.connstatus.MyLog;

public class PageViewModel extends ViewModel {
    private final static String TAG = Global.CAT_VIEWMODEL;

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            MyLog.log(TAG, "apply " + input);
            return "Hello world from section: " + input;
        }
    });

    public void setIndex(int index) {
        MyLog.log(TAG, "setIndex " + index);
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        MyLog.log(TAG, "getText: " + mText);
        return mText;
    }
}