
package com.yaheen.pdaapp.adapter.base;

import android.content.res.Resources;

public interface IFreeHandDevHelper {

    Resources getResources();

    int getColorByHelper(int id);

    float getDimensionByHelper(int id);

    int getDimensionPixelSizeByHelper(int id);

    int[] getIntArrayByHelper(int id);

    int getIntegerByHelper(int id);

    String[] getStringArrayByHelper(int id);

}
