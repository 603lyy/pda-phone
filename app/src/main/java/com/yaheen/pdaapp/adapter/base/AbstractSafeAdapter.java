
package com.yaheen.pdaapp.adapter.base;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import com.yaheen.pdaapp.util.FreeHandSystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jbt
 * @data 2014-10-28
 * @desc 抽象适配器.提供修改适配器数据的方法，这些方法必须运行在UI线程中，否则会抛出异常
 */
abstract class AbstractSafeAdapter<T> extends BaseAdapter {

    protected ArrayList<T> mDatas = new ArrayList<T>();

    protected Context context;

    protected void checkRunInUiThread() {
        if (!FreeHandSystemUtil.isRunInUiThread())
            throw new RuntimeException(
                    "Make sure the content of your adapter is modified from UI thread");
    }

    public AbstractSafeAdapter(Context ctx) {
        context = ctx;
    }

    protected View inflateConvertView(int resource) {
        return LayoutInflater.from(context).inflate(resource, null);
    }

    protected Context getContext() {
        return context;
    }

    public void setDatas(List<T> datas) {
        checkRunInUiThread();
        this.mDatas.clear();
        if (datas != null) {
            this.mDatas.addAll(datas);
        }
    }

    public void setDatas(T[] datas) {
        checkRunInUiThread();
        this.mDatas.clear();
        if (datas != null) {
            for (T t : datas) {
                this.mDatas.add(t);
            }
        }
    }

    public void addDatas(List<T> datas) {
        checkRunInUiThread();
        if (datas != null && datas.size() > 0) {
            this.mDatas.addAll(datas);
        }
    }

    public void addData(T data) {
        checkRunInUiThread();
        if (data != null) {
            this.mDatas.add(data);
        }
    }

    // public void addData(int index, T data) {
    // checkRunInUiThread();
    // if (index >= 0 && index <= mDatas.size()) {
    // if (data != null) {
    // this.mDatas.add(index, data);
    // }
    // }
    // }

    // public void addData(int index, List<T> datas) {
    // checkRunInUiThread();
    // if (index >= 0 && index <= mDatas.size()) {
    // if (datas != null && datas.size() > 0) {
    // this.mDatas.addAll(index, datas);
    // }
    // }
    // }

    public void removeData(T data) {
        checkRunInUiThread();
        if (data != null) {
            this.mDatas.remove(data);
        }
    }

    public void removeData(int position) {
        checkRunInUiThread();
        if (position >= 0 && position < mDatas.size()) {
            this.mDatas.remove(position);
        }
    }

    public void clearData() {
        checkRunInUiThread();
        this.mDatas.clear();
    }

    public ArrayList<T> getDatas() {
        return this.mDatas;
    }

    // public abstract T[] getDatasOfArray();

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return this.mDatas.size();
    }

    @Override
    public T getItem(int position) {
        if (position >= 0 && position < this.mDatas.size()) {
            return this.mDatas.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        // 简单返回数据索引号，可以重写本方法，返回真实数据对应的ID
        return position;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }
}
