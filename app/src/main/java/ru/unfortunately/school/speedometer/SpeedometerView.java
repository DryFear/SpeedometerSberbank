package ru.unfortunately.school.speedometer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpeedometerView extends View {

    private final int MIN_SPEED = 0;
    private final int START_SPEED = 0;
    private final int DEFAULT_MAX_SPEED = 200;
    private final int DEFAULT_LOW_SPEED_COLOR = Color.GREEN;
    private final int DEFAULT_MEDIUM_SPEED_COLOR = Color.YELLOW;
    private final int DEFAULT_HIGH_SPEED_COLOR = Color.RED;
    private final int DEFAULT_ARROW_COLOR = Color.BLACK;
    private final int BACKGROUND_COLOR = Color.GRAY;
    private final int CIRCLE_STROKE_SIZE = 24;
    private final int BACKGROUND_STROKE_SIZE = 36;
    private final int ARROW_STROKE_SIZE = 12;
    private final int TEXT_COLOR = Color.BLACK;
    private final float DEFAULT_TEXT_SIZE = getResources().getDimension(R.dimen.default_text_size);
    private final float DEFAULT_COUNTER_TEXT_SIZE = getResources().getDimension(R.dimen.default_counter_text_size);
    private final int WIDTH = 300;
    private final int HEIGHT = 300;
    private final int START_ANGLE = -225;
    private final int END_ANGLE = 45;
    private final int COUNTER_FRAGMENTS_COUNT = 11;

    private int mLowMediumBorder;
    private int mMediumHightBorder;

    private int mLowSpeedColor;
    private int mMediumSpeedColor;
    private int mHighSpeedColor;
    private int mArrowColor;

    private int mCurrentSpeed;
    private int mMaxSpeed;

    private float mTextSize;
    private float mCounterTextSize;

    private Paint mTextPaint = new Paint();
    private Paint mGraphicsPaint = new Paint();

    private RectF mBounceRect = new RectF(0, 0, WIDTH, HEIGHT);
    private Rect mTextBounce = new Rect();

    public SpeedometerView(Context context) {
        this(context, null);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = Math.max(
                WIDTH + 2 * BACKGROUND_STROKE_SIZE,
                HEIGHT + 2 * BACKGROUND_STROKE_SIZE);
        final int width = resolveSize(size, widthMeasureSpec);
        final int height = resolveSize(size, heightMeasureSpec);
        mBounceRect.right = width - BACKGROUND_STROKE_SIZE;
        mBounceRect.bottom = height - BACKGROUND_STROKE_SIZE;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(BACKGROUND_STROKE_SIZE/2, BACKGROUND_STROKE_SIZE/2);
        drawCircle(canvas);
        drawText(canvas);
        drawArrrow(canvas);
        drawSpeedValueText(canvas);
    }

    private void drawSpeedValueText(Canvas canvas) {
        mTextPaint.setTextSize(mCounterTextSize);
        final String speedValue = String.format(getResources().getString(R.string.speed_value), mCurrentSpeed);
        mTextPaint.getTextBounds(speedValue, 0, speedValue.length(), mTextBounce);
        float x = mBounceRect.width()/2f - mTextBounce.width()/2f;
        float y = mBounceRect.height()*0.75f - mTextBounce.height()/2f;
        canvas.drawText(speedValue, x, y, mTextPaint);
    }

    private void drawArrrow(Canvas canvas) {
        mGraphicsPaint.setColor(mArrowColor);
        mGraphicsPaint.setStrokeWidth(ARROW_STROKE_SIZE);
        int speedInDegrees = (int)((float) (mCurrentSpeed) * (END_ANGLE - START_ANGLE) / mMaxSpeed);
        canvas.drawArc(mBounceRect, START_ANGLE + speedInDegrees, 0, true, mGraphicsPaint);
    }

    private void drawText(Canvas canvas) {
        mTextPaint.setTextSize(mTextSize);
//
//        Тут я хотел сделать числа по всему спидометру, но пока не придумал как
//
//        List<String> numbers = new ArrayList<>();
//        float step = mMaxSpeed / (COUNTER_FRAGMENTS_COUNT - 1);
//        for (int i = 0; i < COUNTER_FRAGMENTS_COUNT; i++) {
//            int item = (int)(step*i);
//            numbers.add(String.valueOf(item));
//        }
        final String minSpeed = String.format(getResources().getString(R.string.speed_value), MIN_SPEED);
        final String maxSpeed = String.format(getResources().getString(R.string.speed_value), mMaxSpeed);

        mTextPaint.getTextBounds(minSpeed, 0, minSpeed.length(), mTextBounce);
        float x = mBounceRect.width()/4f - mTextBounce.width()/2f;
        float y = mBounceRect.height()*0.8f - mTextBounce.height()/2f;
        canvas.drawText(minSpeed, x, y, mTextPaint);

        mTextPaint.getTextBounds(maxSpeed, 0, minSpeed.length(), mTextBounce);
        x = mBounceRect.width()/4f*3f - mTextBounce.width()/2f;
        y = mBounceRect.height()*0.8f - mTextBounce.height()/2f;
        canvas.drawText(maxSpeed, x, y, mTextPaint);
    }

    private void drawCircle(Canvas canvas) {
        int lowMediumBorderInDegrees = (int) ((float)(mLowMediumBorder) * (END_ANGLE - START_ANGLE) / mMaxSpeed);
        int mediumHighBorderInDegrees = (int) ((float) (mMediumHightBorder) * (END_ANGLE - START_ANGLE) / mMaxSpeed);

        mGraphicsPaint.setStrokeWidth(BACKGROUND_STROKE_SIZE);
        mGraphicsPaint.setColor(BACKGROUND_COLOR);
        canvas.drawArc(mBounceRect, START_ANGLE, END_ANGLE-START_ANGLE, false, mGraphicsPaint);

        mGraphicsPaint.setStrokeWidth(CIRCLE_STROKE_SIZE);
        int currentAngle = START_ANGLE;
        mGraphicsPaint.setColor(mLowSpeedColor);
        canvas.drawArc(mBounceRect, currentAngle, lowMediumBorderInDegrees, false, mGraphicsPaint);

        currentAngle+=lowMediumBorderInDegrees;
        mGraphicsPaint.setColor(mMediumSpeedColor);
        canvas.drawArc(mBounceRect, currentAngle, mediumHighBorderInDegrees-lowMediumBorderInDegrees, false, mGraphicsPaint);

        currentAngle+=(mediumHighBorderInDegrees-lowMediumBorderInDegrees);
        mGraphicsPaint.setColor(mHighSpeedColor);
        canvas.drawArc(mBounceRect, currentAngle, END_ANGLE-currentAngle, false, mGraphicsPaint);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        extractAttributes(context, attrs);
        configureTextPaint();
        configureGraphicsPaint();
    }

    private void extractAttributes(@NonNull Context context, @Nullable AttributeSet attrs){
        final Resources.Theme theme = context.getTheme();
        final TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.SpeedometerView, R.attr.speedometerStyle, 0);
        try {
            mCurrentSpeed = typedArray.getInt(R.styleable.SpeedometerView_speed, START_SPEED);
            mMaxSpeed = typedArray.getInt(R.styleable.SpeedometerView_max_speed, DEFAULT_MAX_SPEED);
            mLowMediumBorder = typedArray.getInt(R.styleable.SpeedometerView_low_medium_border, START_SPEED);
            mMediumHightBorder = typedArray.getInt(R.styleable.SpeedometerView_medium_high_border, mMaxSpeed);
            mArrowColor = typedArray.getColor(R.styleable.SpeedometerView_arrow_color, DEFAULT_ARROW_COLOR);
            mLowSpeedColor = typedArray.getColor(R.styleable.SpeedometerView_low_speed_color, DEFAULT_LOW_SPEED_COLOR);
            mMediumSpeedColor = typedArray.getColor(R.styleable.SpeedometerView_medium_speed_color, DEFAULT_MEDIUM_SPEED_COLOR);
            mHighSpeedColor = typedArray.getColor(R.styleable.SpeedometerView_high_speed_color, DEFAULT_HIGH_SPEED_COLOR);
            mTextSize = typedArray.getDimension(R.styleable.SpeedometerView_text_size, DEFAULT_TEXT_SIZE);
            mCounterTextSize = typedArray.getDimension(R.styleable.SpeedometerView_counter_text_size, DEFAULT_COUNTER_TEXT_SIZE);
        }finally {
            typedArray.recycle();
        }
    }

    private void configureTextPaint() {
        mTextPaint.setColor(TEXT_COLOR);
    }


    private void configureGraphicsPaint() {
        mGraphicsPaint.setStyle(Style.STROKE);
    }

    public void setSpeed(int speed){
        mCurrentSpeed = speed;
        invalidate();
    }

    public int getSpeed(){
        return mCurrentSpeed;
    }
}
