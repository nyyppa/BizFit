package com.bizfit.bizfit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.utils.AssetManagerOur;
import com.bizfit.bizfit.utils.Utils;

/**
 *
 */
public class TrackableView extends View {
    // TODO list:
    // Some way to animate the percentage.
    // Probably add "max" as styleable attribute, or extract percentage from activity being tracked?
    // Add text side as a styleable attribute!
    // Fontfaces as stylable attributes.
    // Add negative / positive indicator as styleable attribute.

    private TextPaint textPaint;
    private Paint paint;

    // Label of the item being tracked.
    private String label;
    private float labelSize;

    // Time left indicator
    private int timeLeft;
    private String timeLeftSuffix;

    // The current progress in percentages.
    private int percentage;
    private float percentageSize;
    private float percentagePaddingRight;

    private float percentageSuffixSize;
    private float percentageSuffixPaddingRight;

    // Text colors.
    private int textColorPrimary;
    private int textColorSecondary;
    private int textColorTertiary;

    // Progressbar colors.
    private float barHeight;
    private int finishedColor;
    private int unfinishedColor;

    // Show show if the progress is on track.
    private Drawable indicatorPositive;
    private Drawable indicatorNegative;
    private float indicatorPaddingRight;

    // Default values for styleable attributes.
    private final String labelDefault = "Null";
    private final float labelSizeDefault = Utils.sp2px(getResources(), 26);
    private final int timeLeftDefault = 0;
    private final String timeLeftSuffixDefault = "Null";
    private final int percentageDefault = 100;
    private final float percentageSizeDefault = Utils.sp2px(getResources(), 50);
    private final float percentagePaddingRightDefault = Utils.dp2px(getResources(), -2);
    private final String percentageSuffix = "%";
    private final float percetangeSuffixSizeDefault = Utils.sp2px(getResources(), 26);
    private final float percentageSuffixPaddingRightDefault = Utils.dp2px(getResources(), 20);
    private final int textColorPrimaryDefault = R.color.colorPrimaryDark;
    private final int textColorSecondaryDefault = R.color.colorPrimary;
    private final int textColorTertiaryDefault = R.color.colorPrimary50;
    private final float barHeightDefault = Utils.dp2px(getResources(), 20);
    private final int finishedColorDefault = R.color.colorAccent;
    private final int unfinishedColorDefault = R.color.colorPrimary50;
    private final float indicatorPaddingRightDefault = Utils.dp2px(getResources(), 10);

    // Styleable names. Used for saving.
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR_PRIMARY = "text_color_primary";
    private static final String INSTANCE_TEXT_COLOR_SECONDAY = "text_color_seconday";
    private static final String INSTANCE_TEXT_COLOR_TERTIARY = "text_color_tertiary";
    private static final String INSTANCE_FINISHED_COLOR = "finished_color";
    private static final String INSTANCE_UNFINISHED_COLOR = "unfinished_color";
    private static final String INSTANCE_BAR_HEIGHT = "bar_height";
    private static final String INSTANCE_LABEL = "label";
    private static final String INSTANCE_LABEL_SIZE = "label_size";
    private static final String INSTANCE_LABEL_PADDING_BOTTOM = "label_padding_bottom";
    private static final String INSTANCE_TIME_LEFT = "time_left";
    private static final String INSTANCE_TIME_LEFT_SUFFIX = "time_left_suffix";
    private static final String INSTANCE_PERCENTAGE = "percentage";
    private static final String INSTANCE_PERCENTAGE_SIZE = "percentage_size";
    private static final String INSTANCE_PERCENTAGE_PADDING_RIGHT = "percentage_padding_right";
    private static final String INSTANCE_PERCENTAGE_SUFFIX_SIZE = "percentage_suffix_size";
    private static final String INSTANCE_PERCENTAGE_SUFFIX_PADDING_RIGHT = "percentage_suffix_padding_right";
    private static final String INSTANCE_INDICATOR_POSITIVE = "indicator_positive";
    private static final String INSTANCE_INDICATOR_NEGATIVE = "indicator_negative";
    private static final String INSTANCE_INDICATOR_PADDING_RIGHT = "indiactor_padding_right";

    // Constraints in which the visible components must reside in.
    private RectF rect = new RectF();

    public TrackableView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TrackableView,
                0, 0);
        initAttributes(a);
    }


    /**
     * @param attributes
     */
    private void initAttributes(TypedArray attributes) {
        try {
            setLabel(attributes.getString(R.styleable.TrackableView_label));
            setLabelSize(attributes.getDimension(R.styleable.TrackableView_label_size, labelSizeDefault));
            setTimeLeft(attributes.getInt(R.styleable.TrackableView_time_left, timeLeftDefault));
            setTimeLeftSuffix(attributes.getString(R.styleable.TrackableView_time_left_suffix));
            setPercentage(attributes.getInt(R.styleable.TrackableView_percentage, percentageDefault));
            setPercentageSize(attributes.getDimension(R.styleable.TrackableView_percentage_size, percentageSizeDefault));
            setPercentagePaddingRight(attributes.getDimension(R.styleable.TrackableView_percentage_padding_right, percentagePaddingRightDefault));
            setPercentageSuffixSize(attributes.getDimension(R.styleable.TrackableView_percentage_suffix_size, percetangeSuffixSizeDefault));
            setPercentageSuffixPaddingRight(attributes.getDimension(R.styleable.TrackableView_percentage_suffix_padding_right, percentageSuffixPaddingRightDefault));
            setTextColorPrimary(attributes.getColor(R.styleable.TrackableView_text_color_primary, getResources().getColor(textColorPrimaryDefault)));
            setTextColorSecondary(attributes.getColor(R.styleable.TrackableView_text_color_seconday, getResources().getColor(textColorSecondaryDefault)));
            setTextColorTertiary(attributes.getColor(R.styleable.TrackableView_text_color_tertiary, getResources().getColor(textColorTertiaryDefault)));
            setBarHeight(attributes.getDimension(R.styleable.TrackableView_bar_height, barHeightDefault));
            setFinishedColor(attributes.getColor(R.styleable.TrackableView_finished_color, getResources().getColor(finishedColorDefault)));
            setUnfinishedColor(attributes.getColor(R.styleable.TrackableView_unfinished_color, getResources().getColor(unfinishedColorDefault)));
            setIndicatorPaddingRight(attributes.getDimension(R.styleable.TrackableView_indiactor_padding_right, indicatorPaddingRightDefault));
        } finally {
            attributes.recycle();
        }

    }

    /**
     * Prepares necessary painter for drawing the view.
     * <p/>
     * Does not set colors nor sizes. This is to be done separately before
     * beginning to draw any shapes or texts.
     */
    private void initPainters() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paintText(canvas);
        paintIcons(canvas);
    }

    /**
     * Paints progress bars.
     *
     * @param canvas
     */
    private void paintBar(Canvas canvas, float x) {
        paint.setColor(unfinishedColor);
        canvas.drawRect(x, rect.bottom - barHeight, rect.right, rect.bottom, paint);

        paint.setColor(finishedColor);
        canvas.drawRect(x, rect.bottom - barHeight, rect.right / 2, rect.bottom, paint);
    }

    /**
     * Paints text elements.
     *
     * @param canvas
     */
    private void paintText(Canvas canvas) {
        textPaint.setColor(textColorPrimary);
        textPaint.setTextSize(percentageSize);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setTypeface(AssetManagerOur.getFont(getContext(), AssetManagerOur.boldCondense));
        float offset = textPaint.measureText("100");
        float x = rect.left + offset;
        canvas.drawText(percentage + "", x, rect.bottom - 2, textPaint);
        float ascent = textPaint.ascent();
        float descent = textPaint.descent();


        textPaint.setColor(textColorTertiary);
        textPaint.setTextSize(percentageSuffixSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(AssetManagerOur.getFont(getContext(), AssetManagerOur.regular));
        x += percentagePaddingRight;
        float width = textPaint.measureText("%");
        float suffixHeight = rect.bottom + ascent - textPaint.ascent();
        canvas.drawText(percentageSuffix, x, suffixHeight, textPaint);

        textPaint.setColor(textColorPrimary);
        textPaint.setTextSize(labelSize);
        x += percentageSuffixPaddingRight + width;
        float height = rect.bottom - barHeight - Utils.dp2px(getResources(), 2);
        canvas.drawText(label, x, height, textPaint);

        paintBar(canvas, x);
    }

    /**
     * Paints icon elements.
     *
     * @param canvas
     */
    private void paintIcons(Canvas canvas) {
        //TODO
    }


    /**
     * Measures View's dimensions.
     * <p/>
     * To be implemented.
     * <p/>
     * Accounts for positioning of the View's children.
     * (Our job to handle that). It is also our responsibility to account
     * for padding and such. Gotta make sure this is done. To prevent
     * the label and the remaining time text's overlapping, some form of
     * trimming should be implemented for the label. Probably in this method.
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0);
        setMeasuredDimension(w, h);
        rect.set(getLeft() + getPaddingLeft()
                , getTop() + getPaddingTop()
                , getRight() - getPaddingRight()
                , getBottom() - getPaddingBottom());

    }

    /**
     * Prepares the a view for recreating.
     * <p/>
     * Saves the View's attributes so it can be recreated at a later time. If
     * new attributes are defined for the View, for the love of god remember
     * to save them here!
     *
     * @return Bundle containing the saved attributes.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        // TODO Put attributes in the bundle here.
        return bundle;
    }

    /**
     * Loads View's saved attributes.
     * <p/>
     * Loads the View's attributes so it can be recreated. If
     * new attributes are defined for the View, for the love of god remember
     * to load them here!
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            // TODO Set attributes here
            initPainters();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        if (label != null && label.length() != 0) {
            // Capitalises the first letter and converts the rest to lower case.
            this.label = label.substring(0, 1).toUpperCase() + label.substring(1);
            ;
        } else {
            this.label = labelDefault;
        }

        this.invalidate();
    }

    public float getLabelSize() {
        return labelSize;
    }

    public void setLabelSize(float labelSize) {
        this.labelSize = labelSize;
        this.invalidate();
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        this.invalidate();
    }

    public String getTimeLeftSuffix() {
        return timeLeftSuffix;

    }

    public void setTimeLeftSuffix(String timeLeftSuffix) {
        if (timeLeftSuffix != null) {
            this.timeLeftSuffix = timeLeftSuffix;
        } else {
            this.timeLeftSuffix = timeLeftSuffixDefault;
        }

        this.invalidate();
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
        this.invalidate();
    }

    public float getPercentageSize() {
        return percentageSize;
    }

    public void setPercentageSize(float percentageSize) {
        this.percentageSize = percentageSize;
        this.invalidate();
    }

    public float getPercentagePaddingRight() {
        return percentagePaddingRight;
    }

    public void setPercentagePaddingRight(float percentagePaddingRight) {
        this.percentagePaddingRight = percentagePaddingRight;
        this.invalidate();
    }

    public float getPercentageSuffixSize() {
        return percentageSuffixSize;
    }

    public void setPercentageSuffixSize(float percentageSuffixSize) {
        this.percentageSuffixSize = percentageSuffixSize;
        this.invalidate();
    }

    public float getPercentageSuffixPaddingRight() {
        return percentageSuffixPaddingRight;
    }

    public void setPercentageSuffixPaddingRight(float percentageSuffixPaddingRight) {
        this.percentageSuffixPaddingRight = percentageSuffixPaddingRight;
        this.invalidate();
    }

    public int getTextColorPrimary() {
        return textColorPrimary;
    }

    public void setTextColorPrimary(int textColorPrimary) {
        this.textColorPrimary = textColorPrimary;
        this.invalidate();
    }

    public int getTextColorTertiary() {
        return textColorTertiary;
    }

    public void setTextColorTertiary(int textColorTertiary) {
        this.textColorTertiary = textColorTertiary;
        this.invalidate();
    }

    public int getTextColorSecondary() {
        return textColorSecondary;
    }

    public void setTextColorSecondary(int textColorSecondary) {
        this.textColorSecondary = textColorSecondary;
        this.invalidate();
    }

    public int getFinishedColor() {
        return finishedColor;
    }

    public void setFinishedColor(int finishedColor) {
        this.finishedColor = finishedColor;
        this.invalidate();
    }

    public Drawable getIndicatorNegative() {
        return indicatorNegative;
    }

    public void setIndicatorNegative(Drawable indicatorNegative) {
        this.indicatorNegative = indicatorNegative;
        this.invalidate();
    }

    public Drawable getIndicatorPositive() {
        return indicatorPositive;
    }

    public void setIndicatorPositive(Drawable indicatorPositive) {
        this.indicatorPositive = indicatorPositive;
        this.invalidate();
    }

    public int getUnfinishedColor() {
        return unfinishedColor;
    }

    public void setUnfinishedColor(int unfinishedColor) {
        this.unfinishedColor = unfinishedColor;
        this.invalidate();
    }

    public float getIndicatorPaddingRight() {
        return indicatorPaddingRight;
    }

    public void setIndicatorPaddingRight(float indicatorPaddingRight) {
        this.indicatorPaddingRight = indicatorPaddingRight;
        this.invalidate();
    }

    public float getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
        this.invalidate();
    }
}
