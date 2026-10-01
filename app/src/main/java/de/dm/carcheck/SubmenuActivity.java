package de.dm.carcheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.drawable.GradientDrawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubmenuActivity extends Activity {

    private static final int MAX_VEHICLES = 5;
    private static final int REQUEST_PICK_IMAGE = 2001;

    private static final int BG_COLOR = Color.rgb(3, 5, 9);
    private static final int PANEL_COLOR = Color.rgb(10, 13, 18);
    private static final int FIELD_COLOR = Color.rgb(12, 15, 20);

    private static final int BORDER_COLOR = Color.rgb(65, 70, 80);

    private static final int RED = Color.rgb(175, 5, 15);
    private static final int RED_BORDER = Color.rgb(235, 25, 35);

    private static final int WHITE = Color.rgb(245, 245, 248);
    private static final int GREY = Color.rgb(165, 170, 180);

    private SharedPreferences preferences;

    private final List<Vehicle> vehicles = new ArrayList<>();

    private int currentVehicle = 0;

    private String pageTitle = "";

    private ScrollView scrollView;
    private LinearLayout root;

    private ImageView vehicleImage;
    private TextView vehicleNumber;

    private EditText brandInput;
    private EditText modelInput;
    private EditText engineInput;
    private EditText yearInput;
    private EditText kilometersInput;

    private Button previousButton;
    private Button nextButton;
    private Button saveButton;
    private Button deleteButton;

    /*
     * Dieser Wert wird anhand der echten Navigationsleiste
     * ermittelt.
     */
    private int navigationBarInset = 0;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Schwarze Systemleisten.
         */
        Window window = getWindow();

        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);

        /*
         * Die Statusleiste bleibt ausgeblendet.
         * Die Navigationsleiste bleibt sichtbar.
         */
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        pageTitle = getIntent().getStringExtra("title");

        if (pageTitle == null) {
            pageTitle = "";
        }

        preferences = getSharedPreferences(
                "auto_check_data",
                MODE_PRIVATE
        );

        loadVehicles();

        if (pageTitle.equalsIgnoreCase("MEIN AUTO")) {
            createVehiclePage();
        } else {
            createPlaceholderPage();
        }
    }


    // ============================================================
    // MEIN AUTO
    // ============================================================

    private void createVehiclePage() {

        /*
         * ScrollView über den gesamten verfügbaren Bereich.
         */
        scrollView = new ScrollView(this);

        scrollView.setFillViewport(false);

        scrollView.setBackgroundColor(
                BG_COLOR
        );

        /*
         * Wichtig:
         * Die letzte Zeile darf bis zum Ende scrollen,
         * aber der Speichern-Button wird zusätzlich
         * vom unteren Systembereich ferngehalten.
         */
        scrollView.setClipToPadding(false);


        root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        root.setBackgroundColor(
                BG_COLOR
        );

        /*
         * Anfangswerte.
         *
         * Der wichtige Abstand unten wird später
         * automatisch korrigiert.
         */
        root.setPadding(
                dp(14),
                dp(20),
                dp(14),
                dp(110)
        );


        scrollView.addView(
                root,
                new ScrollView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );


        // ========================================================
        // SYSTEM-INSETS
        // ========================================================

        scrollView.setOnApplyWindowInsetsListener(
                (view, insets) -> {

                    navigationBarInset =
                            getNavigationBarInset(insets);

                    /*
                     * Niemals weniger als 110dp unten.
                     *
                     * Damit ist selbst dann ausreichend Platz
                     * vorhanden, wenn Android auf dem Gerät
                     * keine zuverlässige Höhe liefert.
                     */
                    int safeBottom =
                            Math.max(
                                    dp(110),
                                    navigationBarInset + dp(35)
                            );


                    root.setPadding(
                            dp(14),
                            dp(20),
                            dp(14),
                            safeBottom
                    );


                    return insets;
                }
        );


        // ========================================================
        // TITEL
        // ========================================================

        TextView title =
                new TextView(this);

        title.setText(
                "MEIN AUTO"
        );

        title.setTextColor(
                WHITE
        );

        title.setTextSize(
                28
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setTypeface(
                Typeface.create(
                        "sans-serif",
                        Typeface.BOLD
                )
        );


        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(50)
                );


        root.addView(
                title,
                titleParams
        );


        // ========================================================
        // FAHRZEUG 1 / 5
        // ========================================================

        vehicleNumber =
                new TextView(this);

        vehicleNumber.setTextColor(
                GREY
        );

        vehicleNumber.setTextSize(
                15
        );

        vehicleNumber.setGravity(
                Gravity.CENTER
        );

        vehicleNumber.setTypeface(
                Typeface.create(
                        "sans-serif",
                        Typeface.BOLD
                )
        );


        LinearLayout.LayoutParams numberParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(32)
                );

        numberParams.bottomMargin =
                dp(14);


        root.addView(
                vehicleNumber,
                numberParams
        );


        // ========================================================
        // FAHRZEUG FOTO
        // ========================================================

        vehicleImage =
                new ImageView(this);

        vehicleImage.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        vehicleImage.setBackground(
                createPhotoBackground()
        );

        vehicleImage.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        vehicleImage.setColorFilter(
                Color.rgb(
                        90,
                        95,
                        105
                )
        );


        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(245)
                );

        imageParams.bottomMargin =
                dp(8);


        root.addView(
                vehicleImage,
                imageParams
        );


        vehicleImage.setOnClickListener(
                v -> chooseVehiclePhoto()
        );


        // ========================================================
        // FOTO BESCHREIBUNG
        // ========================================================

        TextView photoText =
                new TextView(this);

        photoText.setText(
                "📷   Fahrzeugfoto hinzufügen / ändern"
        );

        photoText.setTextColor(
                WHITE
        );

        photoText.setTextSize(
                16
        );

        photoText.setGravity(
                Gravity.CENTER
        );

        photoText.setTypeface(
                Typeface.create(
                        "sans-serif",
                        Typeface.BOLD
                )
        );


        LinearLayout.LayoutParams photoTextParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(42)
                );

        photoTextParams.bottomMargin =
                dp(12);


        root.addView(
                photoText,
                photoTextParams
        );


        photoText.setOnClickListener(
                v -> chooseVehiclePhoto()
        );


        // ========================================================
        // EINGABEFELDER
        // ========================================================

        brandInput =
                createInput(
                        "Marke"
                );

        modelInput =
                createInput(
                        "Typ / Modell"
                );

        engineInput =
                createInput(
                        "Motor"
                );

        yearInput =
                createInput(
                        "Baujahr"
                );

        kilometersInput =
                createInput(
                        "Kilometerstand"
                );


        addInput(
                brandInput
        );

        addInput(
                modelInput
        );

        addInput(
                engineInput
        );

        addInput(
                yearInput
        );

        addInput(
                kilometersInput
        );


        // ========================================================
        // ZURÜCK / WEITER
        // ========================================================

        LinearLayout navigation =
                new LinearLayout(this);

        navigation.setOrientation(
                LinearLayout.HORIZONTAL
        );

        navigation.setGravity(
                Gravity.CENTER
        );


        previousButton =
                createDarkButton(
                        "‹   ZURÜCK"
                );


        nextButton =
                createDarkButton(
                        "WEITER   ›"
                );


        navigation.addView(
                previousButton,
                new LinearLayout.LayoutParams(
                        0,
                        dp(58),
                        1f
                )
        );


        View navigationGap =
                new View(this);


        navigation.addView(
                navigationGap,
                new LinearLayout.LayoutParams(
                        dp(10),
                        1
                )
        );


        navigation.addView(
                nextButton,
                new LinearLayout.LayoutParams(
                        0,
                        dp(58),
                        1f
                )
        );


        LinearLayout.LayoutParams navigationParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        navigationParams.topMargin =
                dp(14);

        navigationParams.bottomMargin =
                dp(12);


        root.addView(
                navigation,
                navigationParams
        );


        // ========================================================
        // SPEICHERN
        // ========================================================

        saveButton =
                createRedButton(
                        "FAHRZEUG SPEICHERN"
                );


        LinearLayout.LayoutParams saveParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(62)
                );

        saveParams.bottomMargin =
                dp(16);


        root.addView(
                saveButton,
                saveParams
        );


        saveButton.setOnClickListener(
                v -> saveCurrentVehicle()
        );


        // ========================================================
        // LÖSCHEN
        // ========================================================

        deleteButton =
                createDarkButton(
                        "FAHRZEUG LÖSCHEN"
                );


        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        deleteParams.bottomMargin =
                dp(10);


        root.addView(
                deleteButton,
                deleteParams
        );


        deleteButton.setOnClickListener(
                v -> confirmDeleteVehicle()
        );


        // ========================================================
        // BUTTON AKTIONEN
        // ========================================================

        previousButton.setOnClickListener(
                v -> previousVehicle()
        );


        nextButton.setOnClickListener(
                v -> nextVehicle()
        );


        // ========================================================
        // ANZEIGEN
        // ========================================================

        setContentView(
                scrollView
        );


        /*
         * Insets nach dem Einfügen erneut anfordern.
         */
        if (Build.VERSION.SDK_INT >= 23) {
            scrollView.requestApplyInsets();
        }


        updateVehicleScreen();
    }


    // ============================================================
    // NAVIGATION BAR INSET
    // ============================================================

    private int getNavigationBarInset(
            WindowInsets insets
    ) {

        if (insets == null) {
            return 0;
        }


        /*
         * Android 11 / API 30 und neuer.
         */
        if (Build.VERSION.SDK_INT >= 30) {

            return insets
                    .getInsets(
                            WindowInsets.Type.navigationBars()
                    )
                    .bottom;
        }


        /*
         * Android 6 bis Android 10.
         */
        if (Build.VERSION.SDK_INT >= 23) {

            return insets.getSystemWindowInsetBottom();
        }


        return 0;
    }


    // ============================================================
    // INPUT ERSTELLEN
    // ============================================================

    private EditText createInput(
            String hint
    ) {

        EditText input =
                new EditText(this);

        input.setHint(
                hint
        );

        input.setHintTextColor(
                Color.rgb(
                        125,
                        130,
                        140
                )
        );

        input.setTextColor(
                WHITE
        );

        input.setTextSize(
                18
        );

        input.setSingleLine(
                true
        );

        input.setGravity(
                Gravity.CENTER_VERTICAL
        );

        input.setPadding(
                dp(16),
                0,
                dp(16),
                0
        );

        input.setBackground(
                createFieldBackground()
        );

        return input;
    }


    // ============================================================
    // INPUT HINZUFÜGEN
    // ============================================================

    private void addInput(
            EditText input
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        params.bottomMargin =
                dp(8);


        root.addView(
                input,
                params
        );
    }


    // ============================================================
    // ZUM NÄCHSTEN AUTO
    // ============================================================

    private void nextVehicle() {

        saveFieldsOnly();


        if (currentVehicle < MAX_VEHICLES - 1) {

            currentVehicle++;

            updateVehicleScreen();

            scrollToTop();

        } else {

            Toast.makeText(
                    this,
                    "Du bist bereits bei Fahrzeug 5 von 5.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ============================================================
    // ZUM VORHERIGEN AUTO
    // ============================================================

    private void previousVehicle() {

        saveFieldsOnly();


        if (currentVehicle > 0) {

            currentVehicle--;

            updateVehicleScreen();

            scrollToTop();

        } else {

            Toast.makeText(
                    this,
                    "Du bist bereits bei Fahrzeug 1.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ============================================================
    // NACH OBEN
    // ============================================================

    private void scrollToTop() {

        scrollView.post(
                () -> scrollView.scrollTo(
                        0,
                        0
                )
        );
    }


    // ============================================================
    // FAHRZEUG ANZEIGEN
    // ============================================================

    private void updateVehicleScreen() {

        Vehicle vehicle =
                getVehicle(
                        currentVehicle
                );


        vehicleNumber.setText(
                "FAHRZEUG "
                        +
                (currentVehicle + 1)
                        +
                " / "
                        +
                MAX_VEHICLES
        );


        brandInput.setText(
                vehicle.brand
        );

        modelInput.setText(
                vehicle.model
        );

        engineInput.setText(
                vehicle.engine
        );

        yearInput.setText(
                vehicle.year
        );

        kilometersInput.setText(
                vehicle.kilometers
        );


        loadVehiclePhoto(
                vehicle
        );


        previousButton.setEnabled(
                currentVehicle > 0
        );

        previousButton.setAlpha(
                currentVehicle > 0
                        ? 1f
                        : 0.35f
        );


        nextButton.setEnabled(
                currentVehicle < MAX_VEHICLES - 1
        );

        nextButton.setAlpha(
                currentVehicle < MAX_VEHICLES - 1
                        ? 1f
                        : 0.35f
        );
    }


    // ============================================================
    // FOTO AUSWÄHLEN
    // ============================================================

    private void chooseVehiclePhoto() {

        Intent intent =
                new Intent(
                        Intent.ACTION_OPEN_DOCUMENT
                );


        intent.setType(
                "image/*"
        );


        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );


        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );


        intent.addFlags(
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );


        startActivityForResult(
                intent,
                REQUEST_PICK_IMAGE
        );
    }


    // ============================================================
    // FOTO RESULT
    // ============================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );


        if (
                requestCode == REQUEST_PICK_IMAGE
                        &&
                resultCode == RESULT_OK
                        &&
                data != null
                        &&
                data.getData() != null
        ) {

            Uri uri =
                    data.getData();


            try {

                getContentResolver()
                        .takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

            } catch (Exception ignored) {
            }


            Vehicle vehicle =
                    getVehicle(
                            currentVehicle
                    );


            vehicle.photo =
                    uri.toString();


            vehicle.exists =
                    true;


            saveVehicles();


            loadVehiclePhoto(
                    vehicle
            );


            Toast.makeText(
                    this,
                    "Fahrzeugfoto gespeichert",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ============================================================
    // FOTO LADEN
    // ============================================================

    private void loadVehiclePhoto(
            Vehicle vehicle
    ) {

        vehicleImage.clearColorFilter();


        if (
                vehicle.photo == null
                        ||
                vehicle.photo.isEmpty()
        ) {

            showDefaultPhoto();

            return;
        }


        try {

            Uri uri =
                    Uri.parse(
                            vehicle.photo
                    );


            InputStream stream =
                    getContentResolver()
                            .openInputStream(uri);


            Bitmap bitmap =
                    BitmapFactory
                            .decodeStream(
                                    stream
                            );


            if (stream != null) {
                stream.close();
            }


            if (bitmap != null) {

                vehicleImage.setImageBitmap(
                        bitmap
                );

                vehicleImage.clearColorFilter();

            } else {

                showDefaultPhoto();
            }

        } catch (Exception e) {

            showDefaultPhoto();
        }
    }


    // ============================================================
    // STANDARD FOTO
    // ============================================================

    private void showDefaultPhoto() {

        vehicleImage.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        vehicleImage.setColorFilter(
                Color.rgb(
                        90,
                        95,
                        105
                )
        );
    }


    // ============================================================
    // FELDER SPEICHERN
    // ============================================================

    private void saveFieldsOnly() {

        Vehicle vehicle =
                getVehicle(
                        currentVehicle
                );


        vehicle.brand =
                brandInput
                        .getText()
                        .toString()
                        .trim();


        vehicle.model =
                modelInput
                        .getText()
                        .toString()
                        .trim();


        vehicle.engine =
                engineInput
                        .getText()
                        .toString()
                        .trim();


        vehicle.year =
                yearInput
                        .getText()
                        .toString()
                        .trim();


        vehicle.kilometers =
                kilometersInput
                        .getText()
                        .toString()
                        .trim();


        if (
                !vehicle.brand.isEmpty()
                        ||
                !vehicle.model.isEmpty()
                        ||
                !vehicle.engine.isEmpty()
                        ||
                !vehicle.year.isEmpty()
                        ||
                !vehicle.kilometers.isEmpty()
                        ||
                (
                        vehicle.photo != null
                                &&
                        !vehicle.photo.isEmpty()
                )
        ) {

            vehicle.exists = true;
        }
    }


    // ============================================================
    // FAHRZEUG SPEICHERN
    // ============================================================

    private void saveCurrentVehicle() {

        saveFieldsOnly();

        saveVehicles();


        Toast.makeText(
                this,
                "Fahrzeug gespeichert",
                Toast.LENGTH_SHORT
        ).show();
    }


    // ============================================================
    // FAHRZEUG LÖSCHEN
    // ============================================================

    private void confirmDeleteVehicle() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Fahrzeug löschen?"
                )

                .setMessage(
                        "Fahrzeug "
                                +
                        (currentVehicle + 1)
                                +
                        " wird vollständig gelöscht."
                )

                .setNegativeButton(
                        "ABBRECHEN",
                        null
                )

                .setPositiveButton(
                        "LÖSCHEN",
                        (dialog, which) -> {

                            Vehicle vehicle =
                                    getVehicle(
                                            currentVehicle
                                    );


                            vehicle.clear();


                            saveVehicles();


                            updateVehicleScreen();


                            Toast.makeText(
                                    this,
                                    "Fahrzeug gelöscht",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )

                .show();
    }


    // ============================================================
    // FAHRZEUG HOLEN
    // ============================================================

    private Vehicle getVehicle(
            int index
    ) {

        while (
                vehicles.size()
                        <
                MAX_VEHICLES
        ) {

            vehicles.add(
                    new Vehicle()
            );
        }


        return vehicles.get(
                index
        );
    }


    // ============================================================
    // FAHRZEUGE SPEICHERN
    // ============================================================

    private void saveVehicles() {

        try {

            JSONArray array =
                    new JSONArray();


            for (
                    Vehicle vehicle :
                    vehicles
            ) {

                JSONObject object =
                        new JSONObject();


                object.put(
                        "exists",
                        vehicle.exists
                );


                object.put(
                        "brand",
                        vehicle.brand
                );


                object.put(
                        "model",
                        vehicle.model
                );


                object.put(
                        "engine",
                        vehicle.engine
                );


                object.put(
                        "year",
                        vehicle.year
                );


                object.put(
                        "kilometers",
                        vehicle.kilometers
                );


                object.put(
                        "photo",
                        vehicle.photo
                );


                array.put(
                        object
                );
            }


            preferences
                    .edit()
                    .putString(
                            "vehicles",
                            array.toString()
                    )
                    .apply();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Speichern fehlgeschlagen",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ============================================================
    // FAHRZEUGE LADEN
    // ============================================================

    private void loadVehicles() {

        vehicles.clear();


        String saved =
                preferences.getString(
                        "vehicles",
                        ""
                );


        if (
                saved == null
                        ||
                saved.isEmpty()
        ) {

            for (
                    int i = 0;
                    i < MAX_VEHICLES;
                    i++
            ) {

                vehicles.add(
                        new Vehicle()
                );
            }


            return;
        }


        try {

            JSONArray array =
                    new JSONArray(
                            saved
                    );


            for (
                    int i = 0;
                    i < array.length()
                            &&
                            i < MAX_VEHICLES;
                    i++
            ) {

                JSONObject object =
                        array.getJSONObject(i);


                Vehicle vehicle =
                        new Vehicle();


                vehicle.exists =
                        object.optBoolean(
                                "exists",
                                false
                        );


                vehicle.brand =
                        object.optString(
                                "brand",
                                ""
                        );


                vehicle.model =
                        object.optString(
                                "model",
                                ""
                        );


                vehicle.engine =
                        object.optString(
                                "engine",
                                ""
                        );


                vehicle.year =
                        object.optString(
                                "year",
                                ""
                        );


                vehicle.kilometers =
                        object.optString(
                                "kilometers",
                                ""
                        );


                vehicle.photo =
                        object.optString(
                                "photo",
                                ""
                        );


                vehicles.add(
                        vehicle
                );
            }

        } catch (Exception ignored) {
        }


        while (
                vehicles.size()
                        <
                MAX_VEHICLES
        ) {

            vehicles.add(
                    new Vehicle()
            );
        }
    }


    // ============================================================
    // FELD DESIGN
    // ============================================================

    private GradientDrawable createFieldBackground() {

        GradientDrawable drawable =
                new GradientDrawable();


        drawable.setColor(
                FIELD_COLOR
        );


        drawable.setCornerRadius(
                dp(14)
        );


        drawable.setStroke(
                dp(1),
                BORDER_COLOR
        );


        return drawable;
    }


    // ============================================================
    // FOTO DESIGN
    // ============================================================

    private GradientDrawable createPhotoBackground() {

        GradientDrawable drawable =
                new GradientDrawable();


        drawable.setColor(
                PANEL_COLOR
        );


        drawable.setCornerRadius(
                dp(16)
        );


        drawable.setStroke(
                dp(2),
                Color.rgb(
                        55,
                        60,
                        70
                )
        );


        return drawable;
    }


    // ============================================================
    // SCHWARZER BUTTON
    // ============================================================

    private Button createDarkButton(
            String text
    ) {

        Button button =
                new Button(this);


        button.setText(
                text
        );


        button.setTextColor(
                WHITE
        );


        button.setTextSize(
                14
        );


        button.setTypeface(
                Typeface.create(
                        "sans-serif",
                        Typeface.BOLD
                )
        );


        button.setGravity(
                Gravity.CENTER
        );


        button.setAllCaps(
                false
        );


        button.setPadding(
                dp(5),
                0,
                dp(5),
                0
        );


        GradientDrawable drawable =
                new GradientDrawable();


        drawable.setColor(
                Color.rgb(
                        12,
                        15,
                        20
                )
        );


        drawable.setCornerRadius(
                dp(14)
        );


        drawable.setStroke(
                dp(2),
                Color.rgb(
                        70,
                        75,
                        85
                )
        );


        button.setBackground(
                drawable
        );


        return button;
    }


    // ============================================================
    // ROTER BUTTON
    // ============================================================

    private Button createRedButton(
            String text
    ) {

        Button button =
                new Button(this);


        button.setText(
                text
        );


        button.setTextColor(
                Color.WHITE
        );


        button.setTextSize(
                15
        );


        button.setTypeface(
                Typeface.create(
                        "sans-serif",
                        Typeface.BOLD
                )
        );


        button.setGravity(
                Gravity.CENTER
        );


        button.setAllCaps(
                false
        );


        GradientDrawable drawable =
                new GradientDrawable();


        drawable.setColor(
                RED
        );


        drawable.setCornerRadius(
                dp(14)
        );


        drawable.setStroke(
                dp(2),
                RED_BORDER
        );


        button.setBackground(
                drawable
        );


        return button;
    }


    // ============================================================
    // ANDERE SEITEN
    // ============================================================

    private void createPlaceholderPage() {

        FrameLayout frame =
                new FrameLayout(this);


        frame.setBackgroundColor(
                BG_COLOR
        );


        LinearLayout content =
                new LinearLayout(this);


        content.setOrientation(
                LinearLayout.VERTICAL
        );


        content.setGravity(
                Gravity.CENTER
        );


        TextView title =
                new TextView(this);


        title.setText(
                pageTitle
        );


        title.setTextColor(
                WHITE
        );


        title.setTextSize(
                30
        );


        title.setTypeface(
                Typeface.create(
                        "sans-serif",
                        Typeface.BOLD
                )
        );


        title.setGravity(
                Gravity.CENTER
        );


        content.addView(
                title,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(55)
                )
        );


        TextView info =
                new TextView(this);


        info.setText(
                "Dieser Bereich wird als Nächstes aufgebaut."
        );


        info.setTextColor(
                GREY
        );


        info.setTextSize(
                17
        );


        info.setGravity(
                Gravity.CENTER
        );


        content.addView(
                info,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(50)
                )
        );


        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        params.gravity =
                Gravity.CENTER;


        frame.addView(
                content,
                params
        );


        setContentView(
                frame
        );
    }


    // ============================================================
    // DP UMRECHNUNG
    // ============================================================

    private int dp(
            int value
    ) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;


        return Math.round(
                value * density
        );
    }


    // ============================================================
    // FAHRZEUG DATEN
    // ============================================================

    private static class Vehicle {

        boolean exists = false;

        String brand = "";
        String model = "";
        String engine = "";
        String year = "";
        String kilometers = "";
        String photo = "";


        void clear() {

            exists = false;

            brand = "";
            model = "";
            engine = "";
            year = "";
            kilometers = "";
            photo = "";
        }
    }
}
