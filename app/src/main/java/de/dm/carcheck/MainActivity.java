package de.dm.carcheck;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Auto Check - Startseite
 *
 * 6 Hauptbuttons:
 * 1. Mein Auto
 * 2. Reparaturen
 * 3. Pickerl/TÜV
 * 4. Wartungen
 * 5. Gesamtblick
 * 6. Reifen
 */
public class MainActivity extends Activity {

    private AutoCheckView autoCheckView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        autoCheckView = new AutoCheckView();
        setContentView(autoCheckView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoCheckView != null) {
            autoCheckView.clearPressedState();
        }
    }

    class AutoCheckView extends View {

        private Bitmap background;
        private Bitmap logo;

        private final Paint paint = new Paint(
                Paint.ANTI_ALIAS_FLAG
                        | Paint.FILTER_BITMAP_FLAG
                        | Paint.DITHER_FLAG
        );

        private final Rect sourceRect = new Rect();
        private final RectF imageRect = new RectF();
        private final RectF logoRect = new RectF();
        private final RectF[] hitRects = new RectF[6];

        private final float[] buttonOffset = new float[6];

        private float density = 1f;
        private float imageLeft;
        private float imageRight;
        private float imageBottom;
        private float cardWidth;
        private float cardHeight;
        private float horizontalGap;
        private float verticalGap;
        private float firstRowY;
        private float secondRowY;

        private float logoOffset = -250f;
        private int pressedIndex = -1;
        private boolean introStarted = false;

        private final String[] labels = {
                "Mein Auto",
                "Reparaturen",
                "Pickerl/TÜV",
                "Wartungen",
                "Gesamtblick",
                "Reifen"
        };

        private final String[] subtitles = {
                "Fahrzeug & Details",
                "Reparaturen verwalten",
                "Termine & Fristen",
                "Verschiedenes",
                "Die wichtigsten Infos",
                "Größen, Dimensionen und Alter"
        };

        AutoCheckView() {
            super(MainActivity.this);

            density = getResources().getDisplayMetrics().density;

            background = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.home_background_centered
            );

            logo = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.auto_check_logo
            );

            for (int i = 0; i < hitRects.length; i++) {
                hitRects[i] = new RectF();
            }

            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            setFocusable(true);
            setClickable(true);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startIntro();
                }
            }, 180);
        }

        private float dp(float value) {
            return value * density;
        }

        private float clamp(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }

        @Override
        protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
            super.onSizeChanged(width, height, oldWidth, oldHeight);
            calculateLayout(width, height);

            if (!introStarted && width > 0 && height > 0) {
                prepareIntroPositions();
            }
        }

        private void calculateLayout(int width, int height) {
            if (width <= 0 || height <= 0) {
                return;
            }

            float outerBottomMargin = dp(6f);
            float targetImageWidth = width * 0.825f;
            float maxImageHeight = height * 0.735f;
            float drawWidth = targetImageWidth;
            float drawHeight;

            if (background != null
                    && background.getWidth() > 0
                    && background.getHeight() > 0) {

                float bitmapRatio =
                        (float) background.getHeight()
                                / (float) background.getWidth();

                drawHeight = drawWidth * bitmapRatio;

                if (drawHeight > maxImageHeight) {
                    drawHeight = maxImageHeight;
                    drawWidth = drawHeight / bitmapRatio;
                }
            } else {
                drawHeight = maxImageHeight;
            }

            imageLeft = (width - drawWidth) / 2f;
            imageRight = imageLeft + drawWidth;
            imageBottom = drawHeight;

            imageRect.set(
                    imageLeft,
                    0f,
                    imageRight,
                    imageBottom
            );

            if (logo != null
                    && logo.getWidth() > 0
                    && logo.getHeight() > 0) {

                float logoWidth = drawWidth * 0.965f;
                float logoRatio =
                        (float) logo.getHeight()
                                / (float) logo.getWidth();
                float logoHeight = logoWidth * logoRatio;
                float maxLogoHeight = drawHeight * 0.31f;

                if (logoHeight > maxLogoHeight) {
                    logoHeight = maxLogoHeight;
                    logoWidth = logoHeight / logoRatio;
                }

                float logoLeft = imageLeft + (drawWidth - logoWidth) / 2f;

                logoRect.set(
                        logoLeft,
                        dp(4f),
                        logoLeft + logoWidth,
                        dp(4f) + logoHeight
                );
            } else {
                logoRect.setEmpty();
            }

            float cardAreaTop = imageBottom + dp(2f);
            float cardAreaBottom = height - outerBottomMargin;
            float availableHeight =
                    Math.max(dp(120f), cardAreaBottom - cardAreaTop);

            horizontalGap = dp(11f);
            verticalGap = dp(8f);

            cardWidth =
                    (drawWidth - horizontalGap * 2f) / 3f;

            float calculatedCardHeight =
                    (availableHeight - verticalGap) / 2f;

            cardHeight = clamp(
                    calculatedCardHeight,
                    dp(130f),
                    dp(190f)
            );

            float maxCardHeight =
                    (availableHeight - verticalGap) / 2f;

            if (cardHeight > maxCardHeight) {
                cardHeight = maxCardHeight;
            }

            firstRowY = cardAreaTop;
            secondRowY = firstRowY + cardHeight + verticalGap;

            float requiredBottom = secondRowY + cardHeight;
            if (requiredBottom > cardAreaBottom) {
                float correction = requiredBottom - cardAreaBottom;
                firstRowY -= correction;
                secondRowY -= correction;
            }
        }

        private void prepareIntroPositions() {
            float width = getWidth();

            buttonOffset[0] = -width;
            buttonOffset[1] = -width;
            buttonOffset[2] = -width;

            buttonOffset[3] = width;
            buttonOffset[4] = width;
            buttonOffset[5] = width;

            logoOffset = -Math.max(
                    180f,
                    logoRect.height() + dp(80f)
            );

            invalidate();
        }

        private void startIntro() {
            if (introStarted) {
                return;
            }

            if (getWidth() <= 0 || getHeight() <= 0) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startIntro();
                    }
                }, 60);
                return;
            }

            introStarted = true;
            prepareIntroPositions();

            ObjectAnimator logoAnimation = ObjectAnimator.ofFloat(
                    this,
                    "logoOffset",
                    logoOffset,
                    0f
            );
            logoAnimation.setDuration(2500);
            logoAnimation.setStartDelay(0);

            ArrayList<Animator> cardAnimations = new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(
                        this,
                        "buttonOffset" + i,
                        buttonOffset[i],
                        0f
                );

                // Alle Karten fahren gleichzeitig ein.
                // Linke 3 von links, rechte 3 von rechts.
                animation.setDuration(2500);
                animation.setStartDelay(0);

                cardAnimations.add(animation);
            }

            AnimatorSet cards = new AnimatorSet();
            cards.playTogether(cardAnimations);

            AnimatorSet all = new AnimatorSet();
            all.playTogether(logoAnimation, cards);
            all.start();
        }

        public float getLogoOffset() {
            return logoOffset;
        }

        public void setLogoOffset(float value) {
            logoOffset = value;
            invalidate();
        }

        public float getButtonOffset0() { return buttonOffset[0]; }
        public void setButtonOffset0(float value) { buttonOffset[0] = value; invalidate(); }
        public float getButtonOffset1() { return buttonOffset[1]; }
        public void setButtonOffset1(float value) { buttonOffset[1] = value; invalidate(); }
        public float getButtonOffset2() { return buttonOffset[2]; }
        public void setButtonOffset2(float value) { buttonOffset[2] = value; invalidate(); }
        public float getButtonOffset3() { return buttonOffset[3]; }
        public void setButtonOffset3(float value) { buttonOffset[3] = value; invalidate(); }
        public float getButtonOffset4() { return buttonOffset[4]; }
        public void setButtonOffset4(float value) { buttonOffset[4] = value; invalidate(); }
        public float getButtonOffset5() { return buttonOffset[5]; }
        public void setButtonOffset5(float value) { buttonOffset[5] = value; invalidate(); }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (getWidth() <= 0 || getHeight() <= 0) {
                return;
            }

            canvas.drawColor(Color.BLACK);
            drawBackground(canvas);
            drawLogo(canvas);
            drawCards(canvas);
        }

        private void drawBackground(Canvas canvas) {
            if (background == null || imageRect.isEmpty()) {
                return;
            }

            sourceRect.set(
                    0,
                    0,
                    background.getWidth(),
                    background.getHeight()
            );

            paint.setShader(null);
            paint.setAlpha(255);
            paint.setStyle(Paint.Style.FILL);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            canvas.drawBitmap(
                    background,
                    sourceRect,
                    imageRect,
                    paint
            );
        }

        private void drawLogo(Canvas canvas) {
            if (logo == null || logoRect.isEmpty()) {
                return;
            }

            canvas.save();
            canvas.translate(0f, logoOffset);

            sourceRect.set(
                    0,
                    0,
                    logo.getWidth(),
                    logo.getHeight()
            );

            paint.setShader(null);
            paint.setAlpha(255);
            paint.setStyle(Paint.Style.FILL);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            canvas.drawBitmap(
                    logo,
                    sourceRect,
                    logoRect,
                    paint
            );

            canvas.restore();
        }

        private void drawCards(Canvas canvas) {
            for (int i = 0; i < 6; i++) {
                int column = i % 3;
                int row = i / 3;

                float x = imageLeft + column * (cardWidth + horizontalGap);
                float y = row == 0 ? firstRowY : secondRowY;

                float offset = buttonOffset[i];

                RectF card = new RectF(
                        x + offset,
                        y,
                        x + cardWidth + offset,
                        y + cardHeight
                );

                hitRects[i].set(card);

                drawCard(canvas, card, i);
            }
        }

        private void drawCard(Canvas canvas, RectF card, int index) {
            boolean pressed = pressedIndex == index;
            int accent = getAccentColor(index);

            canvas.save();

            if (pressed) {
                canvas.translate(0f, dp(3f));
            }

            // Schatten
            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(150, 0, 0, 0));
            paint.setShadowLayer(dp(10f), 0f, dp(5f), Color.argb(160, 0, 0, 0));
            canvas.drawRoundRect(
                    new RectF(
                            card.left,
                            card.top + dp(2f),
                            card.right,
                            card.bottom + dp(2f)
                    ),
                    dp(16f),
                    dp(16f),
                    paint
            );
            paint.clearShadowLayer();

            // Kartenfläche
            int topColor;
            int bottomColor;

            if (index == 0) {
                topColor = Color.rgb(105, 20, 28);
                bottomColor = Color.rgb(42, 12, 17);
            } else {
                topColor = Color.rgb(34, 40, 50);
                bottomColor = Color.rgb(16, 20, 27);
            }

            paint.setShader(new LinearGradient(
                    0f,
                    card.top,
                    0f,
                    card.bottom,
                    topColor,
                    bottomColor,
                    Shader.TileMode.CLAMP
            ));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(
                    card,
                    dp(16f),
                    dp(16f),
                    paint
            );
            paint.setShader(null);

            // Rahmen
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(index == 0 ? dp(1.8f) : dp(1f));
            paint.setColor(Color.argb(
                    index == 0 ? 230 : 85,
                    Color.red(accent),
                    Color.green(accent),
                    Color.blue(accent)
            ));
            canvas.drawRoundRect(
                    card,
                    dp(16f),
                    dp(16f),
                    paint
            );

            // Icon
            float iconRadius = Math.min(
                    dp(27f),
                    card.height() * 0.18f
            );

            float iconCenterX =
                    card.left + card.width() * 0.20f;

            float iconCenterY =
                    card.top + card.height() * 0.42f;

            drawIconCircle(
                    canvas,
                    iconCenterX,
                    iconCenterY,
                    iconRadius,
                    index
            );

            // Text
            float textLeft =
                    card.left + card.width() * 0.39f;

            float titleY =
                    card.top + card.height() * 0.43f;

            float subtitleY =
                    card.top + card.height() * 0.60f;

            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(
                    Math.max(
                            dp(13f),
                            card.width() * 0.092f
                    )
            );
            paint.setColor(Color.WHITE);

            drawTextFitted(
                    canvas,
                    labels[index],
                    textLeft,
                    titleY,
                    card.width() * 0.52f,
                    paint
            );

            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(
                    Math.max(
                            dp(8.5f),
                            card.width() * 0.055f
                    )
            );
            paint.setColor(Color.rgb(180, 187, 198));

            drawTextFitted(
                    canvas,
                    subtitles[index],
                    textLeft,
                    subtitleY,
                    card.width() * 0.68f,
                    paint
            );

            // Pfeil
            float arrowRadius = Math.min(
                    dp(23f),
                    card.height() * 0.135f
            );

            float arrowCenterX =
                    card.right - card.width() * 0.115f;

            float arrowCenterY =
                    card.top + card.height() * 0.57f;

            drawArrowButton(
                    canvas,
                    arrowCenterX,
                    arrowCenterY,
                    arrowRadius,
                    index
            );

            canvas.restore();
        }

        private void drawTextFitted(
                Canvas canvas,
                String text,
                float x,
                float baseline,
                float maxWidth,
                Paint textPaint
        ) {
            if (text == null || text.length() == 0) {
                return;
            }

            float originalSize = textPaint.getTextSize();

            if (textPaint.measureText(text) <= maxWidth) {
                canvas.drawText(text, x, baseline, textPaint);
                return;
            }

            float scale = maxWidth / textPaint.measureText(text);
            float newSize = originalSize * scale;

            textPaint.setTextSize(
                    Math.max(dp(7.5f), newSize)
            );

            canvas.drawText(text, x, baseline, textPaint);
            textPaint.setTextSize(originalSize);
        }

        private void drawIconCircle(
                Canvas canvas,
                float cx,
                float cy,
                float radius,
                int index
        ) {
            int accent = getAccentColor(index);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(
                    index == 0 ? 42 : 35,
                    Color.red(accent),
                    Color.green(accent),
                    Color.blue(accent)
            ));
            canvas.drawCircle(cx, cy, radius, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1f));
            paint.setColor(Color.argb(
                    170,
                    Color.red(accent),
                    Color.green(accent),
                    Color.blue(accent)
            ));
            canvas.drawCircle(cx, cy, radius, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2.7f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(accent);

            float s = radius * 0.53f;

            switch (index) {
                case 0:
                    drawCarIcon(canvas, cx, cy, s);
                    break;
                case 1:
                    drawWrenchIcon(canvas, cx, cy, s);
                    break;
                case 2:
                    drawClockIcon(canvas, cx, cy, s);
                    break;
                case 3:
                    drawToolsIcon(canvas, cx, cy, s);
                    break;
                case 4:
                    drawDocumentIcon(canvas, cx, cy, s);
                    break;
                case 5:
                    drawTireIcon(canvas, cx, cy, s);
                    break;
            }

            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStrokeJoin(Paint.Join.MITER);
            paint.setStyle(Paint.Style.FILL);
        }

        private int getAccentColor(int index) {
            switch (index) {
                case 0:
                    return Color.rgb(255, 70, 80);
                case 1:
                    return Color.rgb(0, 150, 255);
                case 2:
                    return Color.rgb(55, 235, 105);
                case 3:
                    return Color.rgb(255, 170, 35);
                case 4:
                    return Color.rgb(190, 75, 255);
                case 5:
                    return Color.rgb(50, 225, 205);
                default:
                    return Color.rgb(225, 230, 238);
            }
        }

        // -------------------------------------------------------------
        // Mein Auto - Auto
        // -------------------------------------------------------------

        private void drawCarIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            RectF body = new RectF(
                    cx - s,
                    cy - s * 0.18f,
                    cx + s,
                    cy + s * 0.38f
            );

            canvas.drawRoundRect(
                    body,
                    s * 0.18f,
                    s * 0.18f,
                    paint
            );

            Path roof = new Path();
            roof.moveTo(cx - s * 0.68f, cy - s * 0.18f);
            roof.lineTo(cx - s * 0.42f, cy - s * 0.66f);
            roof.lineTo(cx + s * 0.40f, cy - s * 0.66f);
            roof.lineTo(cx + s * 0.68f, cy - s * 0.18f);
            canvas.drawPath(roof, paint);

            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(
                    cx - s * 0.57f,
                    cy + s * 0.40f,
                    s * 0.16f,
                    paint
            );
            canvas.drawCircle(
                    cx + s * 0.57f,
                    cy + s * 0.40f,
                    s * 0.16f,
                    paint
            );
            paint.setStyle(Paint.Style.STROKE);
        }

        // -------------------------------------------------------------
        // Reparaturen - Schraubenschlüssel
        // -------------------------------------------------------------

        private void drawWrenchIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            canvas.drawLine(
                    cx - s * 0.55f,
                    cy + s * 0.55f,
                    cx + s * 0.45f,
                    cy - s * 0.45f,
                    paint
            );

            canvas.drawCircle(
                    cx - s * 0.55f,
                    cy + s * 0.55f,
                    s * 0.20f,
                    paint
            );

            Path wrench = new Path();
            wrench.moveTo(cx + s * 0.22f, cy - s * 0.42f);
            wrench.lineTo(cx + s * 0.45f, cy - s * 0.68f);
            wrench.lineTo(cx + s * 0.70f, cy - s * 0.48f);
            wrench.lineTo(cx + s * 0.47f, cy - s * 0.26f);
            canvas.drawPath(wrench, paint);
        }

        // -------------------------------------------------------------
        // Pickerl/TÜV - Uhr
        // -------------------------------------------------------------

        private void drawClockIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2.6f));

            canvas.drawCircle(cx, cy, s * 0.72f, paint);

            for (int i = 0; i < 12; i++) {
                double a = i * Math.PI / 6.0;
                float inner = (i % 3 == 0) ? s * 0.52f : s * 0.60f;
                float outer = s * 0.70f;

                canvas.drawLine(
                        cx + (float) Math.cos(a) * inner,
                        cy + (float) Math.sin(a) * inner,
                        cx + (float) Math.cos(a) * outer,
                        cy + (float) Math.sin(a) * outer,
                        paint
                );
            }

            canvas.drawLine(
                    cx,
                    cy,
                    cx,
                    cy - s * 0.40f,
                    paint
            );

            canvas.drawLine(
                    cx,
                    cy,
                    cx + s * 0.32f,
                    cy,
                    paint
            );
        }

        // -------------------------------------------------------------
        // Wartungen - gekreuzte Werkzeuge
        // -------------------------------------------------------------

        private void drawToolsIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2.8f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);

            canvas.drawLine(
                    cx - s * 0.58f,
                    cy + s * 0.58f,
                    cx + s * 0.58f,
                    cy - s * 0.58f,
                    paint
            );

            canvas.drawCircle(
                    cx - s * 0.58f,
                    cy + s * 0.58f,
                    s * 0.16f,
                    paint
            );

            Path wrenchHead = new Path();
            wrenchHead.moveTo(cx + s * 0.25f, cy - s * 0.42f);
            wrenchHead.lineTo(cx + s * 0.58f, cy - s * 0.72f);
            wrenchHead.lineTo(cx + s * 0.78f, cy - s * 0.52f);
            wrenchHead.lineTo(cx + s * 0.47f, cy - s * 0.22f);
            canvas.drawPath(wrenchHead, paint);

            canvas.drawLine(
                    cx - s * 0.58f,
                    cy - s * 0.58f,
                    cx + s * 0.58f,
                    cy + s * 0.58f,
                    paint
            );

            canvas.drawCircle(
                    cx - s * 0.58f,
                    cy - s * 0.58f,
                    s * 0.14f,
                    paint
            );

            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStrokeJoin(Paint.Join.MITER);
        }

        // -------------------------------------------------------------
        // Gesamtblick - Dokument
        // -------------------------------------------------------------

        private void drawDocumentIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2.8f));
            paint.setStrokeJoin(Paint.Join.ROUND);

            RectF page = new RectF(
                    cx - s * 0.58f,
                    cy - s * 0.78f,
                    cx + s * 0.58f,
                    cy + s * 0.78f
            );

            canvas.drawRoundRect(
                    page,
                    s * 0.08f,
                    s * 0.08f,
                    paint
            );

            canvas.drawLine(
                    cx - s * 0.32f,
                    cy - s * 0.28f,
                    cx + s * 0.32f,
                    cy - s * 0.28f,
                    paint
            );

            canvas.drawLine(
                    cx - s * 0.32f,
                    cy,
                    cx + s * 0.32f,
                    cy,
                    paint
            );

            canvas.drawLine(
                    cx - s * 0.32f,
                    cy + s * 0.28f,
                    cx + s * 0.18f,
                    cy + s * 0.28f,
                    paint
            );
        }

        // -------------------------------------------------------------
        // Reifen
        // -------------------------------------------------------------

        private void drawTireIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2.8f));

            canvas.drawOval(
                    new RectF(
                            cx - s * 0.70f,
                            cy - s * 0.78f,
                            cx + s * 0.70f,
                            cy + s * 0.78f
                    ),
                    paint
            );

            canvas.drawCircle(
                    cx,
                    cy,
                    s * 0.34f,
                    paint
            );

            paint.setStrokeWidth(dp(2.2f));

            for (int i = 0; i < 8; i++) {
                double a = i * Math.PI / 4.0;

                float x1 = cx + (float) Math.cos(a) * s * 0.36f;
                float y1 = cy + (float) Math.sin(a) * s * 0.36f;
                float x2 = cx + (float) Math.cos(a) * s * 0.62f;
                float y2 = cy + (float) Math.sin(a) * s * 0.62f;

                canvas.drawLine(x1, y1, x2, y2, paint);
            }
        }

        // -------------------------------------------------------------
        // Pfeil
        // -------------------------------------------------------------

        private void drawArrowButton(
                Canvas canvas,
                float cx,
                float cy,
                float radius,
                int index
        ) {
            int accent = getAccentColor(index);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(
                    42,
                    Color.red(accent),
                    Color.green(accent),
                    Color.blue(accent)
            ));
            canvas.drawCircle(cx, cy, radius, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1f));
            paint.setColor(Color.rgb(61, 70, 82));
            canvas.drawCircle(cx, cy, radius, paint);

            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(dp(2.6f));
            paint.setStrokeCap(Paint.Cap.ROUND);

            canvas.drawLine(
                    cx - radius * 0.25f,
                    cy,
                    cx + radius * 0.25f,
                    cy,
                    paint
            );

            canvas.drawLine(
                    cx + radius * 0.25f,
                    cy,
                    cx,
                    cy - radius * 0.27f,
                    paint
            );

            canvas.drawLine(
                    cx + radius * 0.25f,
                    cy,
                    cx,
                    cy + radius * 0.27f,
                    paint
            );

            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStyle(Paint.Style.FILL);
        }

        // -------------------------------------------------------------
        // Touch
        // -------------------------------------------------------------

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    pressedIndex = findCardAt(x, y);
                    invalidate();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (pressedIndex >= 0
                            && !hitRects[pressedIndex].contains(x, y)) {
                        pressedIndex = -1;
                        invalidate();
                    }
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    pressedIndex = -1;
                    invalidate();
                    return true;

                case MotionEvent.ACTION_UP:
                    int selected = pressedIndex;
                    pressedIndex = -1;
                    invalidate();

                    if (selected >= 0
                            && selected < 6
                            && hitRects[selected].contains(x, y)) {
                        openSubmenu(selected);
                    }
                    return true;

                default:
                    return true;
            }
        }

        private int findCardAt(float x, float y) {
            for (int i = 0; i < hitRects.length; i++) {
                if (hitRects[i] != null
                        && hitRects[i].contains(x, y)
                        && Math.abs(buttonOffset[i]) < getWidth() * 0.45f) {
                    return i;
                }
            }
            return -1;
        }

        private void openSubmenu(int selected) {
            Intent intent = new Intent(
                    MainActivity.this,
                    SubmenuActivity.class
            );

            intent.putExtra("title", labels[selected]);
            intent.putExtra("subtitle", subtitles[selected]);

            startActivity(intent);
        }

        public void clearPressedState() {
            pressedIndex = -1;
            invalidate();
        }
    }
}
