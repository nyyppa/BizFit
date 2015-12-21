package com.bizfit.bizfit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 */
public class TrackableView extends View {
    // Some way to animate the percentage.
    // Propably add "max".

    private TextPaint textPaint;
    private Paint paint;

    // Label of the item being tracked.
    private String label;
    private float labelSize;
    private float labelPaddingBottom;

    // Time left indicator
    private int timeLeft;
    private String timeLeftSuffix;

    // The current progress in percentages.
    private int percentage;
    private float percentageSize;
    private float percentagePaddingRight;

    private float percetangeSuffixSize;
    private float percetangeSuffixPaddingRight;

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
    private final float labelSizeDefault = Utils.sp2px(getResources(), 10);
    private final float labelPaddingBottomDefault = Utils.dp2px(getResources(), 5);
    private final int timeLeftDefault = 0;
    private final String timeLeftSuffixDefault = "Null";
    private final int percentageDefault = 0;
    private final float percentageSizeDefault = Utils.sp2px(getResources(), 21);
    private final float percentagePaddingRightDefault = Utils.dp2px(getResources(), 10);
    private final String percentageSuffix = "%";
    private final float percetangeSuffixSizeDefault = Utils.sp2px(getResources(), 13);
    private final int textColorPrimaryDefault = R.color.colorPrimaryDark;
    private final int textColorSecondaryDefault = R.color.colorPrimary;
    private final int textColorTertiaryDefault = R.color.colorPrimary50;
    private final float barHeightDefault = Utils.dp2px(getResources(), 4);
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
            setLabelPaddingBottom(attributes.getDimension(R.styleable.TrackableView_label_padding_bottom, labelPaddingBottomDefault));
            setTimeLeft(attributes.getInt(R.styleable.TrackableView_time_left, timeLeftDefault));
            setTimeLeftSuffix(attributes.getString(R.styleable.TrackableView_time_left_suffix));
            setPercentage(attributes.getInt(R.styleable.TrackableView_percentage, percentageDefault));
            setPercentageSize(attributes.getDimension(R.styleable.TrackableView_percentage_size, percentageSizeDefault));
            setPercentagePaddingRight(attributes.getDimension(R.styleable.TrackableView_percentage_padding_right, percentagePaddingRightDefault));
            setPercetangeSuffixSize(attributes.getDimension(R.styleable.TrackableView_percentage_suffix_size, percetangeSuffixSizeDefault));
            setTextColorPrimary(attributes.getColor(R.styleable.TrackableView_text_color_primary, getResources().getColor(textColorPrimaryDefault)));
            setTextColorSecondary(attributes.getColor(R.styleable.TrackableView_text_color_seconday, getResources().getColor(textColorSecondaryDefault)));
            setTextColorTertiary(attributes.getColor(R.styleable.TrackableView_trackable_text_color_tertiary, getResources().getColor(textColorTertiaryDefault)));
            setBarHeight(attributes.getDimension(R.styleable.TrackableView_bar_height, barHeightDefault));
            setFinishedColor(attributes.getColor(R.styleable.TrackableView_finished_color, getResources().getColor(finishedColorDefault)));
            setUnfinishedColor(attributes.getColor(R.styleable.TrackableView_unfinished_color, getResources().getColor(unfinishedColorDefault)));
            setIndicatorPaddingRight(attributes.getDimension(R.styleable.TrackableView_indiactor_padding_right, indicatorPaddingRightDefault));
        } finally {
            attributes.recycle();
        }

    }

    /**
     * Prepares neccesary painter for drawing the view.
     *
     * To  be implemented!
     */
    private void initPainters() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Measures View's dimensions.
     *
     * To be implemented.
     *
     * Accounts for positioning of the View's children.
     * (Our job to handle that). It is also our responsibility to account
     * for padding and such. Gotta make sure this is done.
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    }

    /**
     * Prepares the a view for recreating.
     *
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
        // Put attributes in the bundle here.
        return bundle;
    }

    /**
     * Loads View's saved attributes.
     *
     * Loads the View's attributes so it can be recreated. If
     * new attributes are defined for the View, for the love of god remember
     * to load them here!
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            // Set attributes here
            initPainters();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    /**
     * To be implemented!
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        if (label != null) {
            this.label = label;
        } else {
            this.label = labelDefault;
        }

        super.invalidate();
    }

    public float getLabelSize() {
        return labelSize;
    }

    public void setLabelSize(float labelSize) {
        this.labelSize = labelSize;

        super.invalidate();
    }

    public float getLabelPaddingBottom() {
        return labelPaddingBottom;
    }

    public void setLabelPaddingBottom(float labelPaddingBottom) {
        this.labelPaddingBottom = labelPaddingBottom;

        super.invalidate();
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;

        super.invalidate();
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

        super.invalidate();
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
        super.invalidate();
    }

    public float getPercentageSize() {
        return percentageSize;
    }

    public void setPercentageSize(float percentageSize) {
        this.percentageSize = percentageSize;
        super.invalidate();
    }

    public float getPercentagePaddingRight() {
        return percentagePaddingRight;
    }

    public void setPercentagePaddingRight(float percentagePaddingRight) {
        this.percentagePaddingRight = percentagePaddingRight;
        super.invalidate();
    }

    public float getPercetangeSuffixSize() {
        return percetangeSuffixSize;
    }

    public void setPercetangeSuffixSize(float percetangeSuffixSize) {
        this.percetangeSuffixSize = percetangeSuffixSize;
        super.invalidate();
    }

    public float getPercetangeSuffixPaddingRight() {
        return percetangeSuffixPaddingRight;
    }

    public void setPercetangeSuffixPaddingRight(float percetangeSuffixPaddingRight) {
        this.percetangeSuffixPaddingRight = percetangeSuffixPaddingRight;
        super.invalidate();
    }

    public int getTextColorPrimary() {
        return textColorPrimary;
    }

    public void setTextColorPrimary(int textColorPrimary) {
        this.textColorPrimary = textColorPrimary;
        super.invalidate();
    }

    public int getTextColorTertiary() {
        return textColorTertiary;
    }

    public void setTextColorTertiary(int textColorTertiary) {
        this.textColorTertiary = textColorTertiary;
        super.invalidate();
    }

    public int getTextColorSecondary() {
        return textColorSecondary;
    }

    public void setTextColorSecondary(int textColorSecondary) {
        this.textColorSecondary = textColorSecondary;
        super.invalidate();
    }

    public int getFinishedColor() {
        return finishedColor;
    }

    public void setFinishedColor(int finishedColor) {
        this.finishedColor = finishedColor;
        super.invalidate();
    }

    public Drawable getIndicatorNegative() {
        return indicatorNegative;
    }

    public void setIndicatorNegative(Drawable indicatorNegative) {
        this.indicatorNegative = indicatorNegative;
        super.invalidate();
    }

    public Drawable getIndicatorPositive() {
        return indicatorPositive;
    }

    public void setIndicatorPositive(Drawable indicatorPositive) {
        this.indicatorPositive = indicatorPositive;
        super.invalidate();
    }

    public int getUnfinishedColor() {
        return unfinishedColor;
    }

    public void setUnfinishedColor(int unfinishedColor) {
        this.unfinishedColor = unfinishedColor;
        super.invalidate();
    }

    public float getIndicatorPaddingRight() {
        return indicatorPaddingRight;
    }

    public void setIndicatorPaddingRight(float indicatorPaddingRight) {
        this.indicatorPaddingRight = indicatorPaddingRight;
        super.invalidate();
    }

    public float getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
    }
}
