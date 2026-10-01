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
import android.view.WindowManager;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private AutoCheckView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        view = new AutoCheckView();
        setContentView(view);
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
        private final RectF[] hit = new RectF[6];
        private final float[] buttonOffset = new float[6];

        private float logoOffset = -700f;
        private int pressedIndex = -1;

        private final String[] labels = {
                "Mein Auto",
                "Reparaturen",
                "Wartungen",
                "Kosten",
                "Statistik",
                "Einstellungen"
        };

        private final String[] subtitles = {
                "Fahrzeug & Details",
                "Reparaturen verwalten",
                "Service & Intervalle",
                "Ausgaben im Überblick",
                "Fahrzeugdaten analysieren",
                "App konfigurieren"
        };

        AutoCheckView() {
            super(MainActivity.this);

            background = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.home_background_centered
            );

            logo = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.auto_check_logo
            );

            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            postDelayed(this::startIntro, 250);
        }

        private void startIntro() {
            float width = getWidth();

            if (width <= 0) {
                postDelayed(this::startIntro, 80);
                return;
            }

            buttonOffset[0] = -width;
            buttonOffset[1] = -width;
            buttonOffset[2] = -width;

            buttonOffset[3] = width;
            buttonOffset[4] = width;
            buttonOffset[5] = width;

            ObjectAnimator logoAnimation = ObjectAnimator.ofFloat(
                    this, "logoOffset", -700f, 0f
            );
            logoAnimation.setDuration(1200);

            ArrayList<Animator> animations = new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(
                        this,
                        "buttonOffset" + i,
                        buttonOffset[i],
                        0f
                );
                animation.setDuration(850);
                animation.setStartDelay((i % 3) * 100);
                animations.add(animation);
            }

            AnimatorSet buttons = new AnimatorSet();
            buttons.playTogether(animations);

            AnimatorSet all = new AnimatorSet();
            all.playTogether(logoAnimation, buttons);
            all.start();
        }

        public void setLogoOffset(float value) {
            logoOffset = value;
            invalidate();
        }

        public float getLogoOffset() {
            return logoOffset;
        }

        public void setButtonOffset0(float value) {
            buttonOffset[0] = value;
            invalidate();
        }

        public float getButtonOffset0() {
            return buttonOffset[0];
        }

        public void setButtonOffset1(float value) {
            buttonOffset[1] = value;
            invalidate();
        }

        public float getButtonOffset1() {
            return buttonOffset[1];
        }

        public void setButtonOffset2(float value) {
            buttonOffset[2] = value;
            invalidate();
        }

        public float getButtonOffset2() {
            return buttonOffset[2];
        }

        public void setButtonOffset3(float value) {
            buttonOffset[3] = value;
            invalidate();
        }

        public float getButtonOffset3() {
            return buttonOffset[3];
        }

        public void setButtonOffset4(float value) {
            buttonOffset[4] = value;
            invalidate();
        }

        public float getButtonOffset4() {
            return buttonOffset[4];
        }

        public void setButtonOffset5(float value) {
            buttonOffset[5] = value;
            invalidate();
        }

        public float getButtonOffset5() {
            return buttonOffset[5];
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            canvas.drawColor(Color.BLACK);

            drawCarImage(canvas, width, height);
            drawLogo(canvas, width, height);
            drawBlackArea(canvas, width, height);
            drawModernButtons(canvas, width, height);
        }

        private void drawLogo(Canvas canvas, int width, int height) {
            if (logo == null) return;

            canvas.save();
            canvas.translate(0, logoOffset);

            float logoWidth = width * 0.97f;
            float ratio = (float) logo.getHeight() / logo.getWidth();
            float logoHeight = logoWidth * ratio;

            float maxHeight = height * 0.245f;

            if (logoHeight > maxHeight) {
                logoHeight = maxHeight;
                logoWidth = logoHeight / ratio;
            }

            float left = (width - logoWidth) / 2f;

            // Logo bewusst weiter unten.
            float top = height * 0.055f;

            RectF destination = new RectF(
                    left,
                    top,
                    left + logoWidth,
                    top + logoHeight
            );

            sourceRect.set(
                    0,
                    0,
                    logo.getWidth(),
                    logo.getHeight()
            );

            paint.setShader(null);
            paint.setAlpha(255);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            canvas.drawBitmap(
                    logo,
                    sourceRect,
                    destination,
                    paint
            );

            canvas.restore();
        }

        private void drawCarImage(Canvas canvas, int width, int height) {
            if (background == null) return;

            int imageWidth = background.getWidth();
            int imageHeight = background.getHeight();

            sourceRect.set(
                    0,
                    0,
                    imageWidth,
                    imageHeight
            );

            /*
             * Der komplette Inhalt von home_background_centered
             * bleibt sichtbar. Das Bild wird proportional skaliert
             * und exakt horizontal zentriert.
             */
            float imageTop = 0f;
            float imageBottom = height * 0.685f;
            float availableHeight = imageBottom - imageTop;

            float scale = Math.min(
                    (float) width / imageWidth,
                    availableHeight / imageHeight
            );

            float drawWidth = imageWidth * scale;
            float drawHeight = imageHeight * scale;

            float left = (width - drawWidth) / 2f;
            float top = imageTop + (availableHeight - drawHeight) / 2f;

            RectF destination = new RectF(
                    left,
                    top,
                    left + drawWidth,
                    top + drawHeight
            );

            paint.setShader(null);
            paint.setAlpha(255);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            canvas.drawBitmap(
                    background,
                    sourceRect,
                    destination,
                    paint
            );
        }

        private void drawBlackArea(Canvas canvas, int width, int height) {
            /*
             * Schwarzer Bereich endet direkt vor dem Buttonbereich.
             */
            float top = height * 0.685f;

            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setAlpha(255);

            canvas.drawRect(
                    0,
                    top,
                    width,
                    height,
                    paint
            );
        }

        private void drawModernButtons(Canvas canvas, int width, int height) {
            float side = width * 0.018f;
            float gap = width * 0.014f;

            float cardWidth =
                    (width - side * 2f - gap * 2f) / 3f;

            float cardHeight = height * 0.112f;

            // Buttonposition wie im gewünschten bisherigen Layout.
            float firstY = height * 0.690f;
            float secondY = height * 0.815f;

            for (int i = 0; i < 6; i++) {
                int column = i % 3;
                int row = i / 3;

                float x =
                        side
                                + column * (cardWidth + gap)
                                + buttonOffset[i];

                float y = row == 0 ? firstY : secondY;

                hit[i] = new RectF(
                        x,
                        y,
                        x + cardWidth,
                        y + cardHeight
                );

                drawModernCard(
                        canvas,
                        x,
                        y,
                        cardWidth,
                        cardHeight,
                        i,
                        pressedIndex == i
                );
            }
        }

        private void drawModernCard(
                Canvas canvas,
                float x,
                float y,
                float width,
                float height,
                int index,
                boolean pressed
        ) {
            RectF card = new RectF(
                    x,
                    y,
                    x + width,
                    y + height
            );

            int baseColor = Color.rgb(13, 17, 23);
            int topColor = Color.rgb(25, 30, 38);

            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(
                    x,
                    y,
                    x + width,
                    y + height,
                    topColor,
                    baseColor,
                    Shader.TileMode.CLAMP
            ));

            paint.setShadowLayer(
                    14,
                    0,
                    5,
                    Color.BLACK
            );

            canvas.drawRoundRect(
                    card,
                    18,
                    18,
                    paint
            );

            paint.clearShadowLayer();
            paint.setShader(null);

            if (index == 0) {
                paint.setShader(new LinearGradient(
                        x,
                        y,
                        x + width,
                        y + height,
                        Color.rgb(110, 8, 14),
                        Color.rgb(55, 5, 9),
                        Shader.TileMode.CLAMP
                ));

                canvas.drawRoundRect(
                        card,
                        18,
                        18,
                        paint
                );

                paint.setShader(null);
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(pressed ? 4f : 2f);

            paint.setColor(
                    index == 0
                            ? Color.rgb(255, 45, 55)
                            : Color.rgb(65, 73, 85)
            );

            canvas.drawRoundRect(
                    new RectF(
                            x + 1.5f,
                            y + 1.5f,
                            x + width - 1.5f,
                            y + height - 1.5f
                    ),
                    18,
                    18,
                    paint
            );

            paint.setStyle(Paint.Style.FILL);

            float iconCenterX = x + width * 0.275f;
            float iconCenterY = y + height * 0.38f;
            float iconRadius = height * 0.235f;

            drawIconCircle(
                    canvas,
                    iconCenterX,
                    iconCenterY,
                    iconRadius,
                    index
            );

            /*
             * Titel bewusst klein genug, damit auf allen drei
             * Karten nichts abgeschnitten wird.
             */
            paint.setShader(null);
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.create(
                    "sans-serif",
                    Typeface.BOLD
            ));
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(Math.min(
                    width * 0.075f,
                    26f
            ));

            canvas.drawText(
                    labels[index],
                    x + width * 0.12f,
                    y + height * 0.70f,
                    paint
            );

            paint.setTypeface(Typeface.create(
                    "sans-serif",
                    Typeface.NORMAL
            ));

            paint.setColor(Color.rgb(
                    155,
                    165,
                    180
            ));

            paint.setTextSize(Math.min(
                    width * 0.047f,
                    17f
            ));

            canvas.drawText(
                    subtitles[index],
                    x + width * 0.12f,
                    y + height * 0.87f,
                    paint
            );

            float arrowX = x + width * 0.88f;
            float arrowY = y + height * 0.57f;
            float arrowRadius = height * 0.135f;

            drawArrowButton(
                    canvas,
                    arrowX,
                    arrowY,
                    arrowRadius,
                    index
            );
        }

        private void drawIconCircle(
                Canvas canvas,
                float cx,
                float cy,
                float radius,
                int index
        ) {
            int color = getAccentColor(index);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(
                    45,
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
            ));

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.5f);
            paint.setColor(Color.argb(
                    150,
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
            ));

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

            paint.setColor(color);
            paint.setStrokeWidth(4f);
            paint.setStrokeCap(Paint.Cap.ROUND);

            float s = radius * 0.48f;

            switch (index) {
                case 0:
                    drawCarIcon(canvas, cx, cy, s);
                    break;
                case 1:
                    drawWrenchIcon(canvas, cx, cy, s);
                    break;
                case 2:
                    drawCalendarIcon(canvas, cx, cy, s);
                    break;
                case 3:
                    drawMoneyIcon(canvas, cx, cy, s);
                    break;
                case 4:
                    drawStatisticsIcon(canvas, cx, cy, s);
                    break;
                case 5:
                    drawGearIcon(canvas, cx, cy, s);
                    break;
            }

            paint.setStyle(Paint.Style.FILL);
        }

        private int getAccentColor(int index) {
            switch (index) {
                case 0:
                    return Color.rgb(255, 70, 80);
                case 1:
                    return Color.rgb(0, 150, 255);
                case 2:
                    return Color.rgb(255, 190, 45);
                case 3:
                    return Color.rgb(65, 230, 145);
                case 4:
                    return Color.rgb(175, 70, 255);
                default:
                    return Color.rgb(225, 230, 238);
            }
        }

        private void drawCarIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            RectF body = new RectF(
                    cx - s,
                    cy - s * 0.25f,
                    cx + s,
                    cy + s * 0.40f
            );

            canvas.drawRoundRect(
                    body,
                    s * 0.18f,
                    s * 0.18f,
                    paint
            );

            Path roof = new Path();

            roof.moveTo(
                    cx - s * 0.68f,
                    cy - s * 0.25f
            );

            roof.lineTo(
                    cx - s * 0.42f,
                    cy - s * 0.72f
            );

            roof.lineTo(
                    cx + s * 0.42f,
                    cy - s * 0.72f
            );

            roof.lineTo(
                    cx + s * 0.68f,
                    cy - s * 0.25f
            );

            canvas.drawPath(roof, paint);

            canvas.drawCircle(
                    cx - s * 0.58f,
                    cy + s * 0.43f,
                    s * 0.16f,
                    paint
            );

            canvas.drawCircle(
                    cx + s * 0.58f,
                    cy + s * 0.43f,
                    s * 0.16f,
                    paint
            );
        }

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

            wrench.moveTo(
                    cx + s * 0.25f,
                    cy - s * 0.45f
            );

            wrench.lineTo(
                    cx + s * 0.48f,
                    cy - s * 0.70f
            );

            wrench.lineTo(
                    cx + s * 0.72f,
                    cy - s * 0.50f
            );

            wrench.lineTo(
                    cx + s * 0.48f,
                    cy - s * 0.30f
            );

            canvas.drawPath(
                    wrench,
                    paint
            );
        }

        private void drawCalendarIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            RectF calendar = new RectF(
                    cx - s * 0.78f,
                    cy - s * 0.65f,
                    cx + s * 0.78f,
                    cy + s * 0.70f
            );

            canvas.drawRoundRect(
                    calendar,
                    s * 0.15f,
                    s * 0.15f,
                    paint
            );

            canvas.drawLine(
                    cx - s * 0.78f,
                    cy - s * 0.25f,
                    cx + s * 0.78f,
                    cy - s * 0.25f,
                    paint
            );

            canvas.drawLine(
                    cx - s * 0.40f,
                    cy - s * 0.88f,
                    cx - s * 0.40f,
                    cy - s * 0.45f,
                    paint
            );

            canvas.drawLine(
                    cx + s * 0.40f,
                    cy - s * 0.88f,
                    cx + s * 0.40f,
                    cy - s * 0.45f,
                    paint
            );

            paint.setStyle(Paint.Style.FILL);

            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 3; col++) {
                    canvas.drawCircle(
                            cx - s * 0.40f
                                    + col * s * 0.40f,
                            cy + s * 0.05f
                                    + row * s * 0.32f,
                            s * 0.055f,
                            paint
                    );
                }
            }

            paint.setStyle(Paint.Style.STROKE);
        }

        private void drawMoneyIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            for (int i = 0; i < 3; i++) {
                canvas.drawOval(
                        new RectF(
                                cx - s * 0.72f,
                                cy - s * 0.45f
                                        + i * s * 0.35f,
                                cx + s * 0.25f,
                                cy - s * 0.10f
                                        + i * s * 0.35f
                        ),
                        paint
                );
            }

            canvas.drawCircle(
                    cx + s * 0.48f,
                    cy + s * 0.28f,
                    s * 0.38f,
                    paint
            );

            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(s * 0.55f);
            paint.setTypeface(Typeface.DEFAULT_BOLD);

            canvas.drawText(
                    "€",
                    cx + s * 0.48f,
                    cy + s * 0.48f,
                    paint
            );

            paint.setStyle(Paint.Style.STROKE);
        }

        private void drawStatisticsIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            paint.setStrokeWidth(5f);

            canvas.drawRoundRect(
                    new RectF(
                            cx - s * 0.72f,
                            cy + s * 0.05f,
                            cx - s * 0.35f,
                            cy + s * 0.70f
                    ),
                    5,
                    5,
                    paint
            );

            canvas.drawRoundRect(
                    new RectF(
                            cx - s * 0.15f,
                            cy - s * 0.30f,
                            cx + s * 0.22f,
                            cy + s * 0.70f
                    ),
                    5,
                    5,
                    paint
            );

            canvas.drawRoundRect(
                    new RectF(
                            cx + s * 0.42f,
                            cy - s * 0.70f,
                            cx + s * 0.79f,
                            cy + s * 0.70f
                    ),
                    5,
                    5,
                    paint
            );
        }

        private void drawGearIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            canvas.drawCircle(
                    cx,
                    cy,
                    s * 0.60f,
                    paint
            );

            canvas.drawCircle(
                    cx,
                    cy,
                    s * 0.23f,
                    paint
            );

            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4.0;

                float x1 =
                        cx
                                + (float) Math.cos(angle)
                                * s * 0.72f;

                float y1 =
                        cy
                                + (float) Math.sin(angle)
                                * s * 0.72f;

                float x2 =
                        cx
                                + (float) Math.cos(angle)
                                * s * 0.95f;

                float y2 =
                        cy
                                + (float) Math.sin(angle)
                                * s * 0.95f;

                canvas.drawLine(
                        x1,
                        y1,
                        x2,
                        y2,
                        paint
                );
            }
        }

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
                    45,
                    Color.red(accent),
                    Color.green(accent),
                    Color.blue(accent)
            ));

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.5f);
            paint.setColor(Color.rgb(
                    55,
                    65,
                    78
            ));

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(3.5f);
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

            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                pressedIndex = -1;

                for (int i = 0; i < 6; i++) {
                    if (
                            hit[i] != null
                                    && hit[i].contains(
                                    event.getX(),
                                    event.getY()
                            )
                                    && Math.abs(
                                    buttonOffset[i]
                            ) < getWidth() / 2f
                    ) {
                        pressedIndex = i;
                        break;
                    }
                }

                invalidate();
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {

                int selected = pressedIndex;
                pressedIndex = -1;
                invalidate();

                if (
                        selected >= 0
                                && hit[selected] != null
                                && hit[selected].contains(
                                event.getX(),
                                event.getY()
                        )
                ) {
                    Intent intent = new Intent(
                            MainActivity.this,
                            SubmenuActivity.class
                    );

                    intent.putExtra(
                            "title",
                            labels[selected]
                    );

                    intent.putExtra(
                            "subtitle",
                            subtitles[selected]
                    );

                    startActivity(intent);
                }

                return true;
            }

            return true;
        }
    }
}
