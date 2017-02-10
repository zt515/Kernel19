package io.kiva.kernel.utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import io.kiva.kernel.R;
import android.widget.ImageView;
import android.content.Context;

public class BubbleKit
{
    public static Bitmap convertBubble(Context ctx, int resId) {
        Bitmap bitmap1 = BitmapFactory.decodeResource(ctx.getResources(), resId);
        Bitmap result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);  
        Canvas canvas = new Canvas(result);

        Matrix orig = canvas.getMatrix(); 
        orig.setScale(-1, 1);
        orig.postTranslate(bitmap1.getWidth(), 0);
        canvas.drawBitmap(bitmap1, orig, null); 

        return result;
    }
}
