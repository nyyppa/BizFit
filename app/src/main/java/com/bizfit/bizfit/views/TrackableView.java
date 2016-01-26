package com.bizfit.bizfit.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.utils.AssetManagerOur;

/**
 *
 */
public class TrackableView extends View {

    // TODO list:
    // Make sure all the styleable attributes are saved and loaded properly.

    private TextPaint textPaint;
    private Paint paint;

    // Label of the item being tracked.
    private String label;
    private float labelSize;
    private float labelBarPadding;

    // Time left indicator
    private long timeLeft;
    private String timeLeftSuffix;
    private float timeLeftSize = (int) getResources().getDimension(R.dimen.text_caption);

    // The current progress in percentages.
    private int percentage;
    private float exactPercentage;
    private float percentageSize;
    private float percentagePaddingRight;
    private float percentagePaddingLeft;

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

    // Icon colors
    private int indicatorPositiveColor;
    private int indicatorNegativeColor;
    private int timeLeftIconColor;

    // Icon sizes
    private int indicatorSize = (int) getResources().getDimension(R.dimen.trackableview_icon_size);
    private int timeLeftIconSize = (int) getResources().getDimension(R.dimen.trackableview_icon_size);

    // Show show if the progress is on track.
    private Bitmap indicatorPositive;
    private Bitmap indicatorNegative;
    private Bitmap timeLeftIcon;
    private float indicatorDistanceToMidLine;

    private Tracker host;

    // Default values for styleable attributes.
    private final String labelDefault = "Null";
    private final float labelSizeDefault = (int) getResources().getDimension(R.dimen.text_subheading);
    private final int timeLeftDefault = 0;
    private final String timeLeftSuffixDefault = "Null";
    private final int percentageDefault = 50;
    private final float percentageSizeDefault = (int) getResources().getDimension(R.dimen.text_headline);
    private final float percentagePaddingLeftDefault = (int) getResources().getDimension(R.dimen.trackableview_percentage_padding_left);
    private final String percentageSuffix = "%";
    private final float percetangeSuffixSizeDefault = (int) getResources().getDimension(R.dimen.text_caption);
    private final float percentageSuffixPaddingRightDefault = (int) getResources().getDimension(R.dimen.trackableview_percentage_suffix_padding_right);
    private final int textColorPrimaryDefault = R.color.colorBlack87;
    private final int textColorSecondaryDefault = R.color.colorBlack57;
    private final int textColorTertiaryDefault = R.color.colorBlack57;
    private final float barHeightDefault = (int) getResources().getDimension(R.dimen.trackableview_bar_height);
    private final int finishedColorDefault = R.color.colorAccent;
    private final int unfinishedColorDefault = R.color.colorBlack05;

    // TODO make these into stylebale attributes. Also handle saving and loading.
    private final int indicatorPositiveColorDefault = R.color.colorBlack87;
    private final int indicatorNegativeColorDefault = R.color.colorAccent;
    private final int timeLeftIconColorDefault = R.color.colorBlack57;
    private final float labelBarPaddingDefault = (int) getResources().getDimension(R.dimen.trackableview_lable_bar_padding);

    private final Bitmap indicatorPositiveDefault = BitmapFactory.decodeResource(
            getResources()
            , R.drawable.positive);

    private final Bitmap indicatorNegativeDefault = BitmapFactory.decodeResource(
            getResources()
            , R.drawable.negative);

    private final Bitmap timeLeftIconDefault = BitmapFactory.decodeResource(
            getResources()
            , R.drawable.ic_timelapse);

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
    private static final String INSTANCE_LABEL_BAR_PADDING = "label_bar_padding";
    private static final String INSTANCE_TIME_LEFT = "time_left";
    private static final String INSTANCE_TIME_LEFT_SUFFIX = "time_left_suffix";
    private static final String INSTANCE_PERCENTAGE = "percentage";
    private static final String INSTANCE_PERCENTAGE_SIZE = "percentage_size";
    private static final String INSTANCE_PERCENTAGE_PADDING_RIGHT = "percentage_padding_right";
    private static final String INSTANCE_PERCENTAGE_PADDING_LEFT = "percentage_padding_left";
    private static final String INSTANCE_PERCENTAGE_SUFFIX_SIZE = "percentage_suffix_size";
    private static final String INSTANCE_PERCENTAGE_SUFFIX_PADDING_RIGHT = "percentage_suffix_padding_right";

    // Constraints in which the visible components must reside in.
    private Rect rect;

    // Positioning of the visible elements.
    // Calculated once onMeasure is called.
    private float horizontalCenter;
    private float percentageBaseline;
    private float percentageX;
    private float suffixBaseLine;
    private float suffixX;
    private float labelBaseLine;

    // BarX same as label X.
    private float barX;
    private float barBaseline;
    private float finishedWidth;
    private float timeleftX;
    private float timeLeftSuffixX;
    private float timeLeftIconX;
    private float timeLeftIconY;
    private float indicatorY;

    private RectF unfinished = new RectF();
    private RectF finished = new RectF();

    ValueAnimator animator;

    public TrackableView(Context context, AttributeSet attrs, Tracker host) {
        super(context, attrs);
        setClickable(true);
        rect = new Rect();
        this.host = host;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TrackableView,
                0, 0);
        initAttributes(a, context);

        requestLayout();
    }


    /**
     * @param attributes
     */
    private void initAttributes(TypedArray attributes, Context context) {
        try {
            setLabel(attributes.getString(R.styleable.TrackableView_label));
            setLabelSize(attributes.getDimension(R.styleable.TrackableView_label_size, labelSizeDefault));
            setTimeLeft(attributes.getInt(R.styleable.TrackableView_time_left, timeLeftDefault));
            setTimeLeftSuffix(attributes.getString(R.styleable.TrackableView_time_left_suffix));
            setPercentage(attributes.getInt(R.styleable.TrackableView_percentage, percentageDefault));
            setPercentageSize(attributes.getDimension(R.styleable.TrackableView_percentage_size, percentageSizeDefault));
            setPercentagePaddingLeft(attributes.getDimension(R.styleable.TrackableView_percentage_padding_right, percentagePaddingLeftDefault));
            setPercentageSuffixSize(attributes.getDimension(R.styleable.TrackableView_percentage_suffix_size, percetangeSuffixSizeDefault));
            setPercentageSuffixPaddingRight(attributes.getDimension(R.styleable.TrackableView_percentage_suffix_padding_right, percentageSuffixPaddingRightDefault));
            setTextColorPrimary(attributes.getColor(R.styleable.TrackableView_text_color_primary, getResources().getColor(textColorPrimaryDefault)));
            setTextColorSecondary(attributes.getColor(R.styleable.TrackableView_text_color_seconday, getResources().getColor(textColorSecondaryDefault)));
            setTextColorTertiary(attributes.getColor(R.styleable.TrackableView_text_color_tertiary, getResources().getColor(textColorTertiaryDefault)));
            setBarHeight(attributes.getDimension(R.styleable.TrackableView_bar_height, barHeightDefault));
            setFinishedColor(attributes.getColor(R.styleable.TrackableView_finished_color, getResources().getColor(finishedColorDefault)));
            setUnfinishedColor(attributes.getColor(R.styleable.TrackableView_unfinished_color, getResources().getColor(unfinishedColorDefault)));
            setIndicatorPositiveColor(attributes.getColor(R.styleable.TrackableView_indicator_positive_color, getResources().getColor(indicatorPositiveColorDefault)));
            setIndicatorNegativeColor(attributes.getColor(R.styleable.TrackableView_indicator_negative_color, getResources().getColor(indicatorNegativeColorDefault)));

            // TODO
            // Make this into styleable attribute.
            setIndicatorPositive(indicatorPositiveDefault);
            setIndicatorNegative(indicatorNegativeDefault);
            setTimeLeftIcon(timeLeftIconDefault);
            setTimeLeftIconColor(attributes.getColor(R.styleable.TrackableView_time_left_icon_color, getResources().getColor(timeLeftIconColorDefault)));
            setLabelBarPadding(attributes.getDimension(R.styleable.TrackableView_label_bar_padding, labelBarPaddingDefault));
            setExactPercentage(host.getProgressPercent());

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
        paintBar(canvas);
        paintIcons(canvas);
    }

    /**
     * Paints progress bars.
     *
     * @param canvas
     */
    private void paintBar(Canvas canvas) {
        paint.setColorFilter(null);
        paint.setColor(unfinishedColor);
        canvas.drawRect(unfinished, paint);

        paint.setColor(finishedColor);
        canvas.drawRect(finished, paint);
    }

    /**
     * Paints text elements.
     *
     * @param canvas
     */
    private void paintText(Canvas canvas) {
        prepPercentagePainter();
        canvas.drawText(String.valueOf(percentage), percentageX, percentageBaseline, textPaint);

        prepSuffixPainter();
        canvas.drawText(percentageSuffix, suffixX, suffixBaseLine, textPaint);

        prepLabelPainter();
        canvas.drawText(label, finished.left, labelBaseLine, textPaint);

        prepTimeLeftSuffixPainter();
        canvas.drawText(timeLeftSuffix, timeLeftSuffixX, labelBaseLine, textPaint);

        prepTimeLeftPainter();
        canvas.drawText(String.valueOf(timeLeft), timeleftX, labelBaseLine, textPaint);
    }

    /**
     * Paints icon elements.
     *
     * @param canvas
     */
    private void paintIcons(Canvas canvas) {
        prepTimeLeftIconPainter();
        timeLeftIcon.prepareToDraw();
        canvas.drawBitmap(timeLeftIcon
                , timeLeftIconX
                , timeLeftIconY
                , paint);

        if (host.getProgressOnTrack().equals(Tracker.OnTrack.ahead)) {
            prepIndicatorPositiveIconPainter();
            canvas.drawBitmap(indicatorPositive
                    , rect.left
                    , indicatorY
                    , paint);
        } else if (host.getProgressOnTrack().equals(Tracker.OnTrack.behind)) {
            prepIndicatorNegativeIconPainter();
            canvas.drawBitmap(indicatorNegative
                    , rect.left
                    , indicatorY
                    , paint);
        }

    }

    /**
     * Measures View's dimensions.
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        int minh = MeasureSpec.getSize(w) + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    /**
     * Calculates the positioning of the View's children.
     * <p/>
     * (Our job to handle that). It is also our responsibility to account
     * for padding and such. To prevent the label and the remaining time text's
     * overlapping, some form of trimming should be implemented for the label.
     * Probably in this method.
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        // Do note that the left and right values are what matter in the case
        // of rect. Bottom and top values are not used.
        rect.set(left + getPaddingLeft()
                , top
                , right - getPaddingRight()
                , bottom);

        horizontalCenter = (bottom - getPaddingBottom()) / 2 - (top - getPaddingTop()) / 2;
        prepPercentagePainter();
        float percentageHeight = textPaint.descent() + textPaint.ascent();
        percentageBaseline = horizontalCenter - percentageHeight / 2;
        percentageX = rect.left + getPercentagePaddingLeft() + textPaint.measureText("100");
        indicatorY = percentageBaseline + percentageHeight;

        prepSuffixPainter();
        suffixX = percentageX + percentagePaddingRight;
        Rect tmp = new Rect();
        textPaint.getTextBounds(percentageSuffix, 0, 1, tmp);
        float suffixHeight = tmp.height();
        suffixBaseLine = horizontalCenter + percentageHeight / 2 + suffixHeight;

        barX = suffixX + labelBarPadding +
                textPaint.measureText(percentageSuffix);
        prepLabelPainter();
        float endSectionHeight = (Math.abs(
                textPaint.descent() + textPaint.ascent()))
                + barHeight + labelBarPadding;
        barBaseline = horizontalCenter + endSectionHeight / 2;
        float barTop = barBaseline - barHeight;

        unfinished.set(barX, barTop, rect.right, barBaseline);
        float progress = (exactPercentage > 1) ? unfinished.width() :
                unfinished.width() * ((float) exactPercentage);
        finished.set(unfinished.left
                , unfinished.top
                , unfinished.left + progress
                , unfinished.bottom);
        labelBaseLine = (horizontalCenter - endSectionHeight / 2) -
                (textPaint.ascent() + textPaint.descent());

        timeLeftIconX = unfinished.right - timeLeftIcon.getWidth();
        timeLeftIconY = labelBaseLine - (int) (timeLeftIcon.getHeight() * 0.85);
        timeLeftSuffixX = timeLeftIconX - (int) getResources().getDimension(R.dimen.trackableview_time_left_icon_margin);
        prepTimeLeftSuffixPainter();
        timeleftX = timeLeftSuffixX - textPaint.measureText(timeLeftSuffix)
                - (int) getResources().getDimension(R.dimen.trackableview_time_left_margin);
    }

    private void prepTimeLeftSuffixPainter() {
        textPaint.setColor(textColorTertiary);

        // TODO Make this into a stylebale attribute
        textPaint.setTextSize(timeLeftSize);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
    }

    private void prepLabelPainter() {
        textPaint.setColor(textColorPrimary);
        textPaint.setTextSize(labelSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
    }

    private void prepSuffixPainter() {
        textPaint.setColor(textColorTertiary);
        textPaint.setTextSize(percentageSuffixSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
    }

    /**
     * Prepares textPaint for painting the percentage.
     */
    private void prepPercentagePainter() {
        textPaint.setColor(textColorPrimary);
        textPaint.setTextSize(percentageSize);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
    }


    private void prepTimeLeftPainter() {
        textPaint.setColor(textColorSecondary);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setTextSize(timeLeftSize);
        textPaint.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
    }

    private void prepTimeLeftIconPainter() {
        paint.setColorFilter(
                new PorterDuffColorFilter(
                        timeLeftIconColor, PorterDuff.Mode.SRC_IN
                ));
    }

    private void prepIndicatorPositiveIconPainter() {
        paint.setColorFilter(
                new PorterDuffColorFilter(
                        indicatorPositiveColor, PorterDuff.Mode.SRC_IN
                ));
    }

    private void prepIndicatorNegativeIconPainter() {
        paint.setColorFilter(
                new PorterDuffColorFilter(
                        indicatorNegativeColor, PorterDuff.Mode.SRC_IN
                ));
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
        bundle.putInt(INSTANCE_TEXT_COLOR_PRIMARY, textColorPrimary);
        bundle.putInt(INSTANCE_TEXT_COLOR_SECONDAY, textColorSecondary);
        bundle.putInt(INSTANCE_TEXT_COLOR_TERTIARY, textColorTertiary);
        bundle.putInt(INSTANCE_FINISHED_COLOR, finishedColor);
        bundle.putInt(INSTANCE_UNFINISHED_COLOR, unfinishedColor);
        bundle.putFloat(INSTANCE_BAR_HEIGHT, barHeight);
        bundle.putString(INSTANCE_LABEL, label);
        bundle.putFloat(INSTANCE_LABEL_SIZE, labelSize);
        bundle.putFloat(INSTANCE_LABEL_BAR_PADDING, labelBarPadding);
        bundle.putLong(INSTANCE_TIME_LEFT, timeLeft);
        bundle.putString(INSTANCE_TIME_LEFT_SUFFIX, timeLeftSuffix);
        bundle.putInt(INSTANCE_PERCENTAGE, percentage);
        bundle.putFloat(INSTANCE_PERCENTAGE_SIZE, percentageSize);
        bundle.putFloat(INSTANCE_PERCENTAGE_PADDING_RIGHT, percentagePaddingRight);
        bundle.putFloat(INSTANCE_PERCENTAGE_PADDING_LEFT, percentagePaddingLeft);
        bundle.putFloat(INSTANCE_PERCENTAGE_SUFFIX_SIZE, percentageSuffixSize);
        bundle.putFloat(INSTANCE_PERCENTAGE_SUFFIX_PADDING_RIGHT, percentageSuffixPaddingRight);

        // TODO
        // Convert these into ints for saving. Only the reference int should be saved.
        // bundle.putInt( INSTANCE_TIME_LEFT_ICON, timeLeftIcon);
        // bundle.putInt(INSTANCE_INDICATOR_NEGATIVE, indicatorNegative);
        // bundle.putInt( INSTANCE_INDICATOR_POSITIVE, indicatorPositive);

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
            textColorPrimary = bundle.getInt(INSTANCE_TEXT_COLOR_PRIMARY);
            textColorSecondary = bundle.getInt(INSTANCE_TEXT_COLOR_SECONDAY);
            textColorTertiary = bundle.getInt(INSTANCE_TEXT_COLOR_TERTIARY);
            finishedColor = bundle.getInt(INSTANCE_FINISHED_COLOR);
            unfinishedColor = bundle.getInt(INSTANCE_UNFINISHED_COLOR);
            barHeight = bundle.getFloat(INSTANCE_BAR_HEIGHT);
            label = bundle.getString(INSTANCE_LABEL);
            labelSize = bundle.getFloat(INSTANCE_LABEL_SIZE);
            labelBarPadding = bundle.getFloat(INSTANCE_LABEL_BAR_PADDING);
            timeLeft = bundle.getLong(INSTANCE_TIME_LEFT);
            timeLeftSuffix = bundle.getString(INSTANCE_TIME_LEFT_SUFFIX);
            percentage = bundle.getInt(INSTANCE_PERCENTAGE);
            percentageSize = bundle.getFloat(INSTANCE_PERCENTAGE_SIZE);
            percentagePaddingRight = bundle.getFloat(INSTANCE_PERCENTAGE_PADDING_RIGHT);
            percentagePaddingLeft = bundle.getFloat(INSTANCE_PERCENTAGE_PADDING_LEFT);
            percentageSuffixSize = bundle.getFloat(INSTANCE_PERCENTAGE_SUFFIX_SIZE);
            percentageSuffixPaddingRight = bundle.getFloat(INSTANCE_PERCENTAGE_SUFFIX_SIZE);
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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
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
        this.requestLayout();
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        this.invalidate();
        this.requestLayout();
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
        this.requestLayout();
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = (percentage > 999) ? 999 : percentage;
        this.invalidate();
        this.requestLayout();
    }

    public float getPercentageSize() {
        return percentageSize;
    }

    public void setPercentageSize(float percentageSize) {
        this.percentageSize = percentageSize;
        this.invalidate();
        this.requestLayout();
    }

    public float getPercentagePaddingRight() {
        return percentagePaddingRight;
    }

    public void setPercentagePaddingRight(float percentagePaddingRight) {
        this.percentagePaddingRight = percentagePaddingRight;
        this.invalidate();
        this.requestLayout();
    }

    public float getPercentageSuffixSize() {
        return percentageSuffixSize;
    }

    public void setPercentageSuffixSize(float percentageSuffixSize) {
        this.percentageSuffixSize = percentageSuffixSize;
        this.invalidate();
        this.requestLayout();
    }

    public float getPercentageSuffixPaddingRight() {
        return percentageSuffixPaddingRight;
    }

    public void setPercentageSuffixPaddingRight(float percentageSuffixPaddingRight) {
        this.percentageSuffixPaddingRight = percentageSuffixPaddingRight;
        this.invalidate();
        this.requestLayout();
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

    public Bitmap getIndicatorNegative() {
        return indicatorNegative;
    }

    public void setIndicatorNegative(Bitmap indicatorNegative) {
        this.indicatorNegative = Bitmap.createScaledBitmap(indicatorNegative
                , indicatorSize
                , indicatorSize
                , true);
        this.invalidate();
        this.requestLayout();
    }

    public Bitmap getIndicatorPositive() {
        return indicatorPositive;
    }

    public void setIndicatorPositive(Bitmap indicatorPositive) {
        this.indicatorPositive = Bitmap.createScaledBitmap(indicatorPositive
                , indicatorSize
                , indicatorSize
                , true);
        this.invalidate();
        this.requestLayout();
    }

    public int getUnfinishedColor() {
        return unfinishedColor;
    }

    public void setUnfinishedColor(int unfinishedColor) {
        this.unfinishedColor = unfinishedColor;
        this.invalidate();
    }

    public float getIndicatorDistanceToMidLine() {
        return indicatorDistanceToMidLine;
    }

    public void setIndicatorDistanceToMidLine(float indicatorDistanceToMidLine) {
        this.indicatorDistanceToMidLine = indicatorDistanceToMidLine;
        this.invalidate();
        this.requestLayout();
    }

    public float getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
        this.invalidate();
        this.requestLayout();
    }

    public float getPercentagePaddingLeft() {
        return percentagePaddingLeft;
    }

    public void setPercentagePaddingLeft(float percentagePaddingLeft) {
        this.percentagePaddingLeft = percentagePaddingLeft;
        this.invalidate();
        this.requestLayout();
    }

    public int getIndicatorPositiveColor() {
        return indicatorPositiveColor;
    }

    public void setIndicatorPositiveColor(int indicatorPositiveColor) {
        this.indicatorPositiveColor = indicatorPositiveColor;
        this.invalidate();
    }

    public int getIndicatorNegativeColor() {
        return indicatorNegativeColor;
    }

    public void setIndicatorNegativeColor(int indicatorNegativeColor) {
        this.indicatorNegativeColor = indicatorNegativeColor;
        this.invalidate();
    }

    public int getTimeLeftIconColor() {
        return timeLeftIconColor;
    }

    public void setTimeLeftIconColor(int timeLeftIconColor) {
        this.timeLeftIconColor = timeLeftIconColor;
        this.invalidate();
    }

    public Bitmap getTimeLeftIcon() {
        return timeLeftIcon;
    }

    public void setTimeLeftIcon(Bitmap timeLeftIcon) {
        this.timeLeftIcon = Bitmap.createScaledBitmap(timeLeftIcon
                , timeLeftIconSize
                , timeLeftIconSize
                , true);

        this.invalidate();
    }

    public float getLabelBarPadding() {
        return labelBarPadding;
    }

    public void setLabelBarPadding(float labelBarPadding) {
        this.labelBarPadding = labelBarPadding;
        this.invalidate();
        requestLayout();
    }

    public float getExactPercentage() {
        return exactPercentage;
    }

    public void setExactPercentage(float exactPercentage) {
        this.exactPercentage = exactPercentage;
    }

    public void animateFromZero() {
        animator = ValueAnimator.ofFloat(0, host.getProgressPercent());
        animator.setDuration(getResources().getInteger(R.integer.from_zero_anim_duration));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                setExactPercentage(value);
                setPercentage((int) (Math.floor(value * 100)));
            }
        });

        animator.start();
    }

    public void animateProgressAdded() {
        animator = ValueAnimator.ofFloat(percentage, host.getProgressPercent());
        animator.setDuration(getResources().getInteger(R.integer.progress_added_anim_duration));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                setExactPercentage(value);
                setPercentage((int) (Math.floor(value * 100)));
            }
        });

        animator.start();
    }
}
