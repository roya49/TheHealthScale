package Utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class PieUtils {
    public static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
