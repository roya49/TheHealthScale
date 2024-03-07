package PieChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import Utils.PieUtils;

public class PieChart extends View {
    // 饼图的半径
    private static final int RADIUS = (int) PieUtils.dp2px(150);
    // 饼图相对坐标轴的偏移量
    private static final int OFFSET = (int) PieUtils.dp2px(20);
    // 往外拉的扇形的索引值
    private static final int PULLED_OUT_INDEX = 2;
    // 抗锯齿（可以有效的解决毛边的问题）
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 饼图的分布区域
    RectF bounds = new RectF();
    // 从坐标轴的第四象限开始画图
    int[] angles = {60, 100, 120, 80};
    int[] colors = {
            Color.parseColor("#2979FF"),
            Color.parseColor("#C2185B"),
            Color.parseColor("#009688"),
            Color.parseColor("#FF8F00")
    };

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 根据View的宽度重新计算饼图的位置
        int ww = getWidth() / 2;
        bounds.set(ww - RADIUS, ww - RADIUS, ww + RADIUS, ww + RADIUS);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制矩形
        // canvas.drawRect(bounds, paint);
        int currentAngle = 0;
        for (int i = 0; i < angles.length; i++) {
            paint.setColor(colors[i]);
            // save表示保存当前的视图
            canvas.save();
            if (i == PULLED_OUT_INDEX) {
                int tempAngle = angles[i] / 2;
                canvas.translate(
                        (float) Math.cos(Math.toRadians(currentAngle + tempAngle)) * OFFSET,
                        (float) Math.sin(Math.toRadians(currentAngle + tempAngle)) * OFFSET
                );
            }
            canvas.drawArc(bounds, currentAngle, angles[i], true, paint);
            // 恢复由于执行了translate后的canvas视图
            canvas.restore();
            currentAngle += angles[i];
        }
    }
}

