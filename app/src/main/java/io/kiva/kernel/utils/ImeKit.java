package io.kiva.kernel.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.inputmethod.InputMethodManager;

/**
 * @author Kiva
 *         <p>
 *         监听输入法在对应View上的打开和关闭事件
 */
public final class ImeKit {

    private ImeKit() {
    }

    private static int imeHeight = 256;

    public static void listenImeEvent(View v, final OnImeChangedListener l) {
        v.addOnLayoutChangeListener(new OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View arg0, int arg1, int arg2, int arg3,
                                       int bottom, int arg5, int arg6, int arg7, int oldBottom) {
                if (bottom == oldBottom) {
                    return;
                }

                // 高度变化太小，忽略
                if (Math.abs(bottom - oldBottom) < 20) {
                    return;
                }

//                if (imeHeight == 0) {
                imeHeight = Math.abs(bottom - oldBottom);
//                }

                if (bottom < oldBottom) {
                    l.onImeChange(true);
                } else {
                    l.onImeChange(false);
                }
            }
        });
    }

    public static void hideIme(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    public static void showIme(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) {
            imm.showSoftInput(view, 0);
        }
    }

    public static int getImeHeight() {
        return imeHeight;
    }

    public interface OnImeChangedListener {
        void onImeChange(boolean open);
    }
}