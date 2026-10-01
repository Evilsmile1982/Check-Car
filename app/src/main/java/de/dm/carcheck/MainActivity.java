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
 * Layout-Ziel:
 *  - Das Hochformat-Hintergrundbild wird proportional skaliert.
 *  - Das Bild bleibt vollständig sichtbar und exakt horizontal zentriert.
 *  - Logo liegt oben innerhalb der Bildkante.
 *  - Der Buttonbereich verwendet exakt dieselbe linke/rechte Kante
 *    wie das Hintergrundbild.
 *  - Drei Karten pro Reihe, zwei Reihen.
 *  - Die Karten füllen den verfügbaren Bereich unter dem Bild sauber aus.
 *  - Touch, Animation und Navigation bleiben erhalten.
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

    /**
     * Eigene Canvas-Oberfläche, damit das Layout unabhängig von XML
     * exakt an die Bildkante angepasst werden kann.
     */
    class AutoCheckView extends View {

        // -----------------------------------------------------------------
        // Ressourcen
        // -----------------------------------------------------------------

        private Bitmap background;
        private Bitmap logo;

        // -----------------------------------------------------------------
        // Zeichenobjekte
        // -----------------------------------------------------------------

        private final Paint paint = new Paint(
                Paint.ANTI_ALIAS_FLAG
                        | Paint.FILTER_BITMAP_FLAG
                        | Paint.DITHER_FLAG
        );

        private final Rect sourceRect = new Rect();

        // -----------------------------------------------------------------
        // Layout
        // -----------------------------------------------------------------

        private final RectF imageRect = new RectF();
        private final RectF logoRect = new RectF();

        private final RectF[] hitRects = new RectF[6];

        private float[] buttonOffset = new float[6];

        private float density = 1f;

        /*
         * Diese Werte werden in onSizeChanged() berechnet.
         * Dadurch bleibt das Layout auf verschiedenen Auflösungen
         * proportional.
         */
        private float imageLeft;
        private float imageTop;
        private float imageRight;
        private float imageBottom;

        private float cardWidth;
        private float cardHeight;
        private float horizontalGap;
        private float verticalGap;

        private float firstRowY;
        private float secondRowY;

        // -----------------------------------------------------------------
        // Animation
        // -----------------------------------------------------------------

        private float logoOffset = -250f;

        private int pressedIndex = -1;

        private boolean introStarted = false;

        // -----------------------------------------------------------------
        // Inhalte
        // -----------------------------------------------------------------

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

        // -----------------------------------------------------------------
        // Konstruktor
        // -----------------------------------------------------------------

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

        // -----------------------------------------------------------------
        // Hilfswerte
        // -----------------------------------------------------------------

        private float dp(float value) {
            return value * density;
        }

        private float clamp(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }

        private float screenWidth() {
            return getWidth();
        }

        private float screenHeight() {
            return getHeight();
        }

        // -----------------------------------------------------------------
        // Layoutberechnung
        // -----------------------------------------------------------------

        @Override
        protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
            super.onSizeChanged(width, height, oldWidth, oldHeight);

            calculateLayout(width, height);

            if (!introStarted && width > 0 && height > 0) {
                prepareIntroPositions();
            }
        }

        /**
         * Wichtigster Teil des Layouts:
         *
         * Das Hintergrundbild wird NICHT auf die gesamte Bildschirmbreite
         * gestreckt. Stattdessen wird es proportional skaliert und innerhalb
         * des verfügbaren Hochformatbereiches zentriert.
         *
         * Bei einem 9:16-Bild ergibt sich dadurch genau die gewünschte
         * schmale, mittige Bildfläche mit schwarzen Seitenbereichen.
         *
         * Die Buttons bekommen danach exakt imageLeft/imageRight als Grenze.
         */
        private void calculateLayout(int width, int height) {
            if (width <= 0 || height <= 0) {
                return;
            }

            float outerBottomMargin = dp(10f);

            // -------------------------------------------------------------
            // 1. Bildfläche
            // -------------------------------------------------------------

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

                // Das Bild darf niemals in den Buttonbereich hineinragen.
                if (drawHeight > maxImageHeight) {
                    drawHeight = maxImageHeight;
                    drawWidth = drawHeight / bitmapRatio;
                }
            } else {
                drawHeight = maxImageHeight;
            }

            // Bild exakt horizontal zentrieren.
            imageLeft = (width - drawWidth) / 2f;
            imageRight = imageLeft + drawWidth;

            /*
             * Oben bündig.
             *
             * Dadurch sitzt das Logo ebenfalls direkt am oberen Rand
             * derselben Bildfläche.
             */
            imageTop = 0f;
            imageBottom = imageTop + drawHeight;

            imageRect.set(
                    imageLeft,
                    imageTop,
                    imageRight,
                    imageBottom
            );

            // -------------------------------------------------------------
            // 2. Logo
            // -------------------------------------------------------------

            if (logo != null
                    && logo.getWidth() > 0
                    && logo.getHeight() > 0) {

                float logoWidth = drawWidth * 0.965f;

                float logoRatio =
                        (float) logo.getHeight()
                                / (float) logo.getWidth();

                float logoHeight = logoWidth * logoRatio;

                /*
                 * Das Logo sitzt innerhalb der Bildkante und beginnt oben
                 * exakt an der oberen Bildkante. Ein kleiner Abstand verhindert
                 * nur, dass transparente Pixel optisch abgeschnitten werden.
                 */
                float logoTop = imageTop + dp(4f);

                // Logo darf nicht mehr als ca. 31 % der Bildhöhe beanspruchen.
                float maxLogoHeight = drawHeight * 0.31f;

                if (logoHeight > maxLogoHeight) {
                    logoHeight = maxLogoHeight;
                    logoWidth = logoHeight / logoRatio;
                }

                float logoLeft = imageLeft + (drawWidth - logoWidth) / 2f;

                logoRect.set(
                        logoLeft,
                        logoTop,
                        logoLeft + logoWidth,
                        logoTop + logoHeight
                );
            } else {
                logoRect.setEmpty();
            }

            // -------------------------------------------------------------
            // 3. Kartenbereich
            // -------------------------------------------------------------

            /*
             * Die Karten starten unmittelbar nach der Bildfläche.
             *
             * Es gibt absichtlich keinen zusätzlichen breiten Außenrand:
             * imageLeft und imageRight sind die äußeren Kanten.
             */
            float cardAreaTop = imageBottom + dp(7f);

            float cardAreaBottom = height - outerBottomMargin;

            float availableHeight =
                    Math.max(dp(120f), cardAreaBottom - cardAreaTop);

            horizontalGap = dp(11f);
            verticalGap = dp(20f);

            float contentWidth = drawWidth;

            cardWidth =
                    (contentWidth - horizontalGap * 2f) / 3f;

            float calculatedCardHeight =
                    (availableHeight - verticalGap) / 2f;

            /*
             * Auf normalen 16:9/20:9-Telefonen liegt die Kartenhöhe
             * ungefähr in dem Bereich des Referenzscreenshots.
             */
            cardHeight = clamp(
                    calculatedCardHeight,
                    dp(130f),
                    dp(190f)
            );

            /*
             * Wenn wegen einer ungewöhnlichen Bildschirmhöhe die Karten
             * nicht in den verfügbaren Bereich passen, werden sie wieder
             * exakt auf den Bereich eingepasst.
             */
            float maxCardHeight =
                    (availableHeight - verticalGap) / 2f;

            if (cardHeight > maxCardHeight) {
                cardHeight = maxCardHeight;
            }

            firstRowY = cardAreaTop;

            secondRowY =
                    firstRowY
                            + cardHeight
                            + verticalGap;

            // Sicherheitskorrektur für sehr kleine Displays.
            float requiredBottom =
                    secondRowY + cardHeight;

            if (requiredBottom > cardAreaBottom) {
                float correction =
                        requiredBottom - cardAreaBottom;

                firstRowY -= correction;
                secondRowY -= correction;
            }
        }

        // -----------------------------------------------------------------
        // Introanimation
        // -----------------------------------------------------------------

        private void prepareIntroPositions() {
            float width = getWidth();

            /*
             * Linke drei Karten kommen von links,
             * rechte drei Karten kommen von rechts.
             */
            buttonOffset[0] = -width;
            buttonOffset[1] = -width;
            buttonOffset[2] = -width;

            buttonOffset[3] = width;
            buttonOffset[4] = width;
            buttonOffset[5] = width;

            logoOffset = -Math.max(180f, logoRect.height() + dp(80f));

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

            ObjectAnimator logoAnimation =
                    ObjectAnimator.ofFloat(
                            this,
                            "logoOffset",
                            logoOffset,
                            0f
                    );

            logoAnimation.setDuration(850);
            logoAnimation.setStartDelay(80);

            ArrayList<Animator> cardAnimations =
                    new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                float start = buttonOffset[i];

                ObjectAnimator animation =
                        ObjectAnimator.ofFloat(
                                this,
                                "buttonOffset" + i,
                                start,
                                0f
                        );

                animation.setDuration(650);
                animation.setStartDelay(
                        120L + (i % 3) * 85L + (i / 3) * 90L
                );

                cardAnimations.add(animation);
            }

            AnimatorSet cards = new AnimatorSet();
            cards.playTogether(cardAnimations);

            AnimatorSet all = new AnimatorSet();
            all.playTogether(
                    logoAnimation,
                    cards
            );

            all.start();
        }

        // -----------------------------------------------------------------
        // Animator-Properties
        // -----------------------------------------------------------------

        public float getLogoOffset() {
            return logoOffset;
        }

        public void setLogoOffset(float value) {
            logoOffset = value;
            invalidate();
        }

        public float getButtonOffset0() {
            return buttonOffset[0];
        }

        public void setButtonOffset0(float value) {
            buttonOffset[0] = value;
            invalidate();
        }

        public float getButtonOffset1() {
            return buttonOffset[1];
        }

        public void setButtonOffset1(float value) {
            buttonOffset[1] = value;
            invalidate();
        }

        public float getButtonOffset2() {
            return buttonOffset[2];
        }

        public void setButtonOffset2(float value) {
            buttonOffset[2] = value;
            invalidate();
        }

        public float getButtonOffset3() {
            return buttonOffset[3];
        }

        public void setButtonOffset3(float value) {
            buttonOffset[3] = value;
            invalidate();
        }

        public float getButtonOffset4() {
            return buttonOffset[4];
        }

        public void setButtonOffset4(float value) {
            buttonOffset[4] = value;
            invalidate();
        }

        public float getButtonOffset5() {
            return buttonOffset[5];
        }

        public void setButtonOffset5(float value) {
            buttonOffset[5] = value;
            invalidate();
        }

        // -----------------------------------------------------------------
        // Zeichnen
        // -----------------------------------------------------------------

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();

            if (width <= 0 || height <= 0) {
                return;
            }

            canvas.drawColor(Color.BLACK);

            drawBackground(canvas);
            drawLogo(canvas);
            drawCards(canvas);
        }

        /**
         * Hintergrund:
         * proportional, vollständig sichtbar und horizontal zentriert.
         */
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

        /**
         * Logo:
         * innerhalb der Bildkante, oben bündig.
         */
        private void drawLogo(Canvas canvas) {
            if (logo == null || logoRect.isEmpty()) {
                return;
            }

            canvas.save();

            canvas.translate(
                    0f,
                    logoOffset
            );

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

        // -----------------------------------------------------------------
        // Karten
        // -----------------------------------------------------------------

        private void drawCards(Canvas canvas) {
            for (int i = 0; i < 6; i++) {

                int column = i % 3;
                int row = i / 3;

                float x =
                        imageLeft
                                + column * (cardWidth + horizontalGap)
                                + buttonOffset[i];

                float y =
                        row == 0
                                ? firstRowY
                                : secondRowY;

                RectF cardRect =
                        new RectF(
                                x,
                                y,
                                x + cardWidth,
                                y + cardHeight
                        );

                hitRects[i].set(cardRect);

                drawCard(
                        canvas,
                        cardRect,
                        i,
                        pressedIndex == i
                );
            }
        }

        private void drawCard(
                Canvas canvas,
                RectF card,
                int index,
                boolean pressed
        ) {
            float radius = dp(14f);

            int baseColor = Color.rgb(
                    13,
                    18,
                    24
            );

            int topColor = Color.rgb(
                    25,
                    31,
                    39
            );

            if (pressed) {
                topColor = Color.rgb(
                        31,
                        38,
                        47
                );
            }

            // -------------------------------------------------------------
            // Kartenfläche
            // -------------------------------------------------------------

            paint.setStyle(Paint.Style.FILL);

            paint.setShader(
                    new LinearGradient(
                            card.left,
                            card.top,
                            card.right,
                            card.bottom,
                            topColor,
                            baseColor,
                            Shader.TileMode.CLAMP
                    )
            );

            paint.setShadowLayer(
                    dp(9f),
                    0f,
                    dp(4f),
                    Color.argb(
                            185,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawRoundRect(
                    card,
                    radius,
                    radius,
                    paint
            );

            paint.clearShadowLayer();
            paint.setShader(null);

            // -------------------------------------------------------------
            // Aktive Karte Mein Auto
            // -------------------------------------------------------------

            if (index == 0) {

                paint.setShader(
                        new LinearGradient(
                                card.left,
                                card.top,
                                card.right,
                                card.bottom,
                                Color.rgb(125, 8, 15),
                                Color.rgb(48, 4, 8),
                                Shader.TileMode.CLAMP
                        )
                );

                canvas.drawRoundRect(
                        card,
                        radius,
                        radius,
                        paint
                );

                paint.setShader(null);
            }

            // -------------------------------------------------------------
            // Rahmen
            // -------------------------------------------------------------

            paint.setStyle(Paint.Style.STROKE);

            paint.setStrokeWidth(
                    pressed
                            ? dp(2.6f)
                            : dp(1.25f)
            );

            paint.setColor(
                    index == 0
                            ? Color.rgb(255, 40, 52)
                            : Color.rgb(61, 71, 84)
            );

            RectF border =
                    new RectF(
                            card.left + dp(1f),
                            card.top + dp(1f),
                            card.right - dp(1f),
                            card.bottom - dp(1f)
                    );

            canvas.drawRoundRect(
                    border,
                    radius,
                    radius,
                    paint
            );

            paint.setStyle(Paint.Style.FILL);

            // -------------------------------------------------------------
            // Icon
            // -------------------------------------------------------------

            float iconCenterX =
                    card.left + card.width() * 0.275f;

            float iconCenterY =
                    card.top + card.height() * 0.385f;

            float iconRadius =
                    Math.min(
                            dp(31f),
                            card.height() * 0.235f
                    );

            drawIconCircle(
                    canvas,
                    iconCenterX,
                    iconCenterY,
                    iconRadius,
                    index
            );

            // -------------------------------------------------------------
            // Text
            // -------------------------------------------------------------

            float textLeft =
                    card.left + card.width() * 0.115f;

            float titleY =
                    card.top + card.height() * 0.705f;

            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.BOLD
                    )
            );
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(
                    Math.min(
                            dp(16f),
                            card.width() * 0.078f
                    )
            );

            drawTextFitted(
                    canvas,
                    labels[index],
                    textLeft,
                    titleY,
                    card.width() * 0.62f,
                    paint
            );

            // -------------------------------------------------------------
            // Untertitel
            // -------------------------------------------------------------

            float subtitleY =
                    card.top + card.height() * 0.865f;

            paint.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.NORMAL
                    )
            );

            paint.setColor(
                    Color.rgb(
                            153,
                            163,
                            176
                    )
            );

            paint.setTextSize(
                    Math.min(
                            dp(10.5f),
                            card.width() * 0.048f
                    )
            );

            drawTextFitted(
                    canvas,
                    subtitles[index],
                    textLeft,
                    subtitleY,
                    card.width() * 0.68f,
                    paint
            );

            // -------------------------------------------------------------
            // Pfeil
            // -------------------------------------------------------------

            float arrowRadius =
                    Math.min(
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
        }

        /**
         * Verhindert abgeschnittene Titel/Untertitel auf kleinen Geräten.
         */
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
                canvas.drawText(
                        text,
                        x,
                        baseline,
                        textPaint
                );
                return;
            }

            float scale =
                    maxWidth / textPaint.measureText(text);

            float newSize =
                    originalSize * scale;

            textPaint.setTextSize(
                    Math.max(
                            dp(8f),
                            newSize
                    )
            );

            canvas.drawText(
                    text,
                    x,
                    baseline,
                    textPaint
            );

            textPaint.setTextSize(originalSize);
        }

        // -----------------------------------------------------------------
        // Icon-Kreis
        // -----------------------------------------------------------------

        private void drawIconCircle(
                Canvas canvas,
                float cx,
                float cy,
                float radius,
                int index
        ) {
            int accent = getAccentColor(index);

            // Transparente Akzentfläche
            paint.setStyle(Paint.Style.FILL);

            paint.setColor(
                    Color.argb(
                            index == 0 ? 42 : 35,
                            Color.red(accent),
                            Color.green(accent),
                            Color.blue(accent)
                    )
            );

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

            // Kreisrahmen
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1f));

            paint.setColor(
                    Color.argb(
                            170,
                            Color.red(accent),
                            Color.green(accent),
                            Color.blue(accent)
                    )
            );

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

            // Icon
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
                    return Color.rgb(255, 190, 45);

                case 3:
                    return Color.rgb(65, 230, 145);

                case 4:
                    return Color.rgb(175, 70, 255);

                default:
                    return Color.rgb(225, 230, 238);
            }
        }

        // -----------------------------------------------------------------
        // Auto-Icon
        // -----------------------------------------------------------------

        private void drawCarIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            RectF body =
                    new RectF(
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

            roof.moveTo(
                    cx - s * 0.68f,
                    cy - s * 0.18f
            );

            roof.lineTo(
                    cx - s * 0.42f,
                    cy - s * 0.66f
            );

            roof.lineTo(
                    cx + s * 0.40f,
                    cy - s * 0.66f
            );

            roof.lineTo(
                    cx + s * 0.68f,
                    cy - s * 0.18f
            );

            canvas.drawPath(
                    roof,
                    paint
            );

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

        // -----------------------------------------------------------------
        // Schraubenschlüssel
        // -----------------------------------------------------------------

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
                    cx + s * 0.22f,
                    cy - s * 0.42f
            );

            wrench.lineTo(
                    cx + s * 0.45f,
                    cy - s * 0.68f
            );

            wrench.lineTo(
                    cx + s * 0.70f,
                    cy - s * 0.48f
            );

            wrench.lineTo(
                    cx + s * 0.47f,
                    cy - s * 0.26f
            );

            canvas.drawPath(
                    wrench,
                    paint
            );
        }

        // -----------------------------------------------------------------
        // Kalender
        // -----------------------------------------------------------------

        private void drawCalendarIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            RectF calendar =
                    new RectF(
                            cx - s * 0.76f,
                            cy - s * 0.63f,
                            cx + s * 0.76f,
                            cy + s * 0.70f
                    );

            canvas.drawRoundRect(
                    calendar,
                    s * 0.15f,
                    s * 0.15f,
                    paint
            );

            canvas.drawLine(
                    cx - s * 0.76f,
                    cy - s * 0.24f,
                    cx + s * 0.76f,
                    cy - s * 0.24f,
                    paint
            );

            canvas.drawLine(
                    cx - s * 0.40f,
                    cy - s * 0.86f,
                    cx - s * 0.40f,
                    cy - s * 0.45f,
                    paint
            );

            canvas.drawLine(
                    cx + s * 0.40f,
                    cy - s * 0.86f,
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

        // -----------------------------------------------------------------
        // Geld / Kosten
        // -----------------------------------------------------------------

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
                                cx + s * 0.22f,
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
                    cy + s * 0.47f,
                    paint
            );

            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStyle(Paint.Style.STROKE);
        }

        // -----------------------------------------------------------------
        // Statistik
        // -----------------------------------------------------------------

        private void drawStatisticsIcon(
                Canvas canvas,
                float cx,
                float cy,
                float s
        ) {
            paint.setStrokeWidth(dp(2.7f));

            canvas.drawRoundRect(
                    new RectF(
                            cx - s * 0.72f,
                            cy + s * 0.05f,
                            cx - s * 0.35f,
                            cy + s * 0.70f
                    ),
                    dp(4f),
                    dp(4f),
                    paint
            );

            canvas.drawRoundRect(
                    new RectF(
                            cx - s * 0.15f,
                            cy - s * 0.30f,
                            cx + s * 0.22f,
                            cy + s * 0.70f
                    ),
                    dp(4f),
                    dp(4f),
                    paint
            );

            canvas.drawRoundRect(
                    new RectF(
                            cx + s * 0.42f,
                            cy - s * 0.70f,
                            cx + s * 0.79f,
                            cy + s * 0.70f
                    ),
                    dp(4f),
                    dp(4f),
                    paint
            );
        }

        // -----------------------------------------------------------------
        // Zahnrad
        // -----------------------------------------------------------------

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

                double angle =
                        i * Math.PI / 4.0;

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
                                * s * 0.96f;

                float y2 =
                        cy
                                + (float) Math.sin(angle)
                                * s * 0.96f;

                canvas.drawLine(
                        x1,
                        y1,
                        x2,
                        y2,
                        paint
                );
            }
        }

        // -----------------------------------------------------------------
        // Pfeil
        // -----------------------------------------------------------------

        private void drawArrowButton(
                Canvas canvas,
                float cx,
                float cy,
                float radius,
                int index
        ) {
            int accent =
                    getAccentColor(index);

            paint.setStyle(Paint.Style.FILL);

            paint.setColor(
                    Color.argb(
                            42,
                            Color.red(accent),
                            Color.green(accent),
                            Color.blue(accent)
                    )
            );

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1f));

            paint.setColor(
                    Color.rgb(
                            61,
                            70,
                            82
                    )
            );

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

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

        // -----------------------------------------------------------------
        // Touch
        // -----------------------------------------------------------------

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

                    if (pressedIndex >= 0) {

                        if (!hitRects[pressedIndex].contains(x, y)) {
                            pressedIndex = -1;
                            invalidate();
                        }
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

        private int findCardAt(
                float x,
                float y
        ) {
            for (int i = 0; i < hitRects.length; i++) {

                if (hitRects[i] != null
                        && hitRects[i].contains(x, y)
                        && Math.abs(buttonOffset[i])
                        < getWidth() * 0.45f) {

                    return i;
                }
            }

            return -1;
        }

        private void openSubmenu(int selected) {

            Intent intent =
                    new Intent(
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

        public void clearPressedState() {
            pressedIndex = -1;
            invalidate();
        }
    }
}
