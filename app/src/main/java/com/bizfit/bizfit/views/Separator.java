package com.bizfit.bizfit.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.AssetManagerOur;

/**
 *
 */
public class Separator extends View {

    private Rect horizontalLine = new Rect();
    private String label = "null";

    private int labelSize = (int) getResources().getDimension(R.dimen.text_body);
    private int horizontalLineHeight = (int) getResources().getDimension(R.dimen.separator_line_height);

    private int labelPaddingLeft = (int) getResources().getDimension(R.dimen.separator_label_padding_left);
    private int labelPaddingBottom = (int) getResources().getDimension(R.dimen.separator_label_padding_right);

    private int labelX;
    private int labelY;

    private Paint paint;
    private TextPaint textPaint;

    private final String INSTANCE_STATE = "saved_instance";


    public Separator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(false);
        requestLayout();
        initPainters();
    }

    private void initPainters() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorBlack10));
        paint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setColor(getResources().getColor(R.color.colorBlack87));
        textPaint.setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(labelSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(label
                , labelX
                , labelY
                , textPaint);

        canvas.drawRect(horizontalLine
                , paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);
        int h = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        horizontalLine.set(left + getPaddingLeft()
                , bottom - top - horizontalLineHeight - getPaddingBottom()
                , right - getPaddingRight()
                , bottom - top - getPaddingBottom());

        labelX = horizontalLine.left + labelPaddingLeft;
        labelY = horizontalLine.top - labelPaddingBottom;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        this.invalidate();
    }

    public int getLabelSize() {
        return labelSize;
    }

    public void setLabelSize(int labelSize) {
        this.labelSize = labelSize;
        this.invalidate();
    }

    public int getHorizontalLineHeight() {
        return horizontalLineHeight;
    }

    public void setHorizontalLineHeight(int horizontalLineHeight) {
        this.horizontalLineHeight = horizontalLineHeight;
        this.invalidate();
    }

    public int getLabelPaddingLeft() {
        return labelPaddingLeft;
    }

    public void setLabelPaddingLeft(int labelPaddingLeft) {
        this.labelPaddingLeft = labelPaddingLeft;
        this.invalidate();
    }

    public int getLabelPaddingBottom() {
        return labelPaddingBottom;
    }

    public void setLabelPaddingBottom(int labelPaddingBottom) {
        this.labelPaddingBottom = labelPaddingBottom;
        this.invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();

        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        //bundle.putInt(nimi, arvo);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }

        super.onRestoreInstanceState(state);
    }
}