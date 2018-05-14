
package com.yaheen.pdaapp.adapter.base;

import android.content.Context;
import android.content.res.Resources;

public abstract class CommonAdapter<T> extends AbstractSafeAdapter<T> implements
        IFreeHandDevHelper {

    public CommonAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public Resources getResources() {
        return getContext().getResources();
    }

    @Override
    public int getColorByHelper(int id) {
        return getResources().getColor(id);
    }

    @Override
    public float getDimensionByHelper(int id) {
        return getResources().getDimension(id);
    }

    @Override
    public int getDimensionPixelSizeByHelper(int id) {
        return getResources().getDimensionPixelSize(id);
    }

    @Override
    public int[] getIntArrayByHelper(int id) {
        return getResources().getIntArray(id);
    }

    @Override
    public int getIntegerByHelper(int id) {
        return getResources().getInteger(id);
    }

    @Override
    public String[] getStringArrayByHelper(int id) {
        return getResources().getStringArray(id);
    }

}
