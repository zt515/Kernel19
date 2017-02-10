package io.kiva.kernel.utils;
import android.os.Handler;
import android.os.Looper;

public class UIKit extends Handler
{
    private static UIKit sInstance;
    
    private UIKit() {
        super(Looper.getMainLooper());
    }
    
    public static UIKit get() {
        if (sInstance == null) {
            synchronized (UIKit.class) {
                sInstance = new UIKit();
            }
        }
        return sInstance;
    }
}
