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
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubmenuActivity extends Activity {

    // ============================================================
    // KONSTANTEN
    // ============================================================

    private static final int MAX_VEHICLES = 5;
    private static final int REQUEST_PICK_IMAGE = 2001;

    private static final int BG_COLOR =
            Color.rgb(3, 5, 9);

    private static final int PANEL_COLOR =
            Color.rgb(10, 13, 18);

    private static final int FIELD_COLOR =
            Color.rgb(12, 15, 20);

    private static final int BORDER_COLOR =
            Color.rgb(65, 70, 80);

    private static final int RED =
            Color.rgb(175, 5, 15);

    private static final int RED_BORDER =
            Color.rgb(235, 25, 35);

    private static final int WHITE =
            Color.rgb(245, 245, 248);

    private static final int GREY =
            Color.rgb(165, 170, 180);


    // ============================================================
    // DATEN
    // ============================================================

    private SharedPreferences preferences;

    private final List<Vehicle> vehicles =
            new ArrayList<>();

    private int currentVehicle = 0;

    private String pageTitle = "";


    // ============================================================
    // UI
    // ============================================================

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


    // ============================================================
    // START
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Vollbild.
         *
         * Die Navigationsleiste des Handys bleibt sichtbar.
         * Dadurch kann der Benutzer weiterhin Android-Navigation
         * benutzen.
         */

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        pageTitle =
                getIntent().getStringExtra("title");

        if (pageTitle == null) {
            pageTitle = "";
        }

        preferences =
                getSharedPreferences(
                        "auto_check_data",
                        MODE_PRIVATE
                );

        loadVehicles();

        /*
         * Der erste Bereich, den wir jetzt vollständig bauen,
         * ist MEIN AUTO.
         */

        if (pageTitle.equalsIgnoreCase("MEIN AUTO")) {

            createVehiclePage();

        } else {

            createPlaceholderPage();
        }
    }


    // ============================================================
    // MEIN AUTO SEITE
    // ============================================================

    private void createVehiclePage() {

        /*
         * Haupt-ScrollView.
         *
         * Falls ein Gerät kleiner ist, kann der Benutzer scrollen.
         * Auf normalen Geräten wird der komplette Bereich schön
         * mittig angezeigt.
         */

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.setFillViewport(true);

        scrollView.setBackgroundColor(
                BG_COLOR
        );

        scrollView.setClipToPadding(false);


        // --------------------------------------------------------
        // Hauptcontainer
        // --------------------------------------------------------

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        root.setPadding(
                dp(14),
                dp(24),
                dp(14),
                dp(30)
        );

        root.setBackgroundColor(
                BG_COLOR
        );


        scrollView.addView(
                root,
                new ScrollView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );


        // ========================================================
        // OBERER BEREICH
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

        title.setTypeface(
                Typeface.create(
                        "sans-serif",
                        Typeface.BOLD
                )
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setIncludeFontPadding(
                true
        );


        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(48)
                )
        );


        // ========================================================
        // FAHRZEUG NUMMER
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
                        dp(30)
                );

        numberParams.bottomMargin =
                dp(10);

        root.addView(
                vehicleNumber,
                numberParams
        );


        // ========================================================
        // FOTO
        // ========================================================

        vehicleImage =
                new ImageView(this);

        vehicleImage.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        vehicleImage.setBackground(
                createPanelBackground()
        );

        vehicleImage.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        vehicleImage.setColorFilter(
                Color.rgb(
                        105,
                        110,
                        120
                )
        );


        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(250)
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
        // FOTO-TEXT
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
                Typeface.DEFAULT,
                Typeface.BOLD
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
        // DATENFELDER
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


        addField(
                root,
                brandInput
        );

        addField(
                root,
                modelInput
        );

        addField(
                root,
                engineInput
        );

        addField(
                root,
                yearInput
        );

        addField(
                root,
                kilometersInput
        );


        // ========================================================
        // FAHRZEUG NAVIGATION
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


        LinearLayout.LayoutParams navButtonParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1f
                );


        navigation.addView(
                previousButton,
                navButtonParams
        );


        View navigationGap =
                new View(this);

        navigation.addView(
                navigationGap,
                new LinearLayout.LayoutParams(
                        dp(8),
                        1
                )
        );


        navigation.addView(
                nextButton,
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1f
                )
        );


        LinearLayout.LayoutParams navigationParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(52)
                );

        navigationParams.topMargin =
                dp(10);

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


        root.addView(
                saveButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(54)
                )
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
                        dp(48)
                );

        deleteParams.topMargin =
                dp(8);

        deleteParams.bottomMargin =
                dp(5);


        root.addView(
                deleteButton,
                deleteParams
        );


        deleteButton.setOnClickListener(
                v -> confirmDeleteVehicle()
        );


        // ========================================================
        // BUTTON FUNKTIONEN
        // ========================================================

        previousButton.setOnClickListener(
                v -> previousVehicle()
        );

        nextButton.setOnClickListener(
                v -> nextVehicle()
        );


        // ========================================================
        // SET CONTENT
        // ========================================================

        setContentView(
                scrollView
        );


        // ========================================================
        // AKTUELLES FAHRZEUG LADEN
        // ========================================================

        updateVehicleScreen();
    }


    // ============================================================
    // INPUT FELD
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
                dp(14),
                0,
                dp(14),
                0
        );

        input.setBackground(
                createFieldBackground()
        );

        return input;
    }


    // ============================================================
    // FELD HINZUFÜGEN
    // ============================================================

    private void addField(
            LinearLayout parent,
            EditText field
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(52)
                );

        params.bottomMargin =
                dp(7);

        parent.addView(
                field,
                params
        );
    }


    // ============================================================
    // FAHRZEUG WEITER
    // ============================================================

    private void nextVehicle() {

        saveFieldsOnly();

        if (currentVehicle <
                MAX_VEHICLES - 1) {

            currentVehicle++;

            updateVehicleScreen();

        } else {

            Toast.makeText(
                    this,
                    "Du bist bereits bei Fahrzeug 5 von 5.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ============================================================
    // FAHRZEUG ZURÜCK
    // ============================================================

    private void previousVehicle() {

        saveFieldsOnly();

        if (currentVehicle > 0) {

            currentVehicle--;

            updateVehicleScreen();

        } else {

            Toast.makeText(
                    this,
                    "Du bist bereits bei Fahrzeug 1.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ============================================================
    // BILDSCHIRM AKTUALISIEREN
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


        // --------------------------------------------------------
        // Navigation aktivieren/deaktivieren
        // --------------------------------------------------------

        previousButton.setEnabled(
                currentVehicle > 0
        );

        previousButton.setAlpha(
                currentVehicle > 0
                        ? 1.0f
                        : 0.35f
        );


        nextButton.setEnabled(
                currentVehicle <
                        MAX_VEHICLES - 1
        );

        nextButton.setAlpha(
                currentVehicle <
                        MAX_VEHICLES - 1
                        ? 1.0f
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
    // FOTO ZURÜCKBEKOMMEN
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
                requestCode ==
                        REQUEST_PICK_IMAGE
                        &&
                resultCode ==
                        RESULT_OK
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
    // FOTO ANZEIGEN
    // ============================================================

    private void loadVehiclePhoto(
            Vehicle vehicle
    ) {

        vehicleImage.clearColorFilter();


        if (
                vehicle.photo ==
                        null
                        ||
                vehicle.photo.isEmpty()
        ) {

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

                vehicleImage
                        .setImageBitmap(
                                bitmap
                        );

                vehicleImage
                        .clearColorFilter();

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
    // AKTUELLE FELDER SPEICHERN
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
                        "Das Fahrzeug "
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
                        < MAX_VEHICLES
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
    // DATEN SPEICHERN
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
    // DATEN LADEN
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
                        array.getJSONObject(
                                i
                        );


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


        } catch (Exception e) {

            vehicles.clear();
        }


        while (
                vehicles.size()
                        < MAX_VEHICLES
        ) {

            vehicles.add(
                    new Vehicle()
            );
        }


        while (
                vehicles.size()
                        > MAX_VEHICLES
        ) {

            vehicles.remove(
                    vehicles.size() - 1
            );
        }
    }


    // ============================================================
    // HINTERGRUND FELD
    // ============================================================

    private android.graphics.drawable.GradientDrawable
    createFieldBackground() {

        android.graphics.drawable.GradientDrawable background =
                new android.graphics.drawable.GradientDrawable();


        background.setColor(
                FIELD_COLOR
        );


        background.setCornerRadius(
                dp(12)
        );


        background.setStroke(
                dp(1),
                BORDER_COLOR
        );


        return background;
    }


    // ============================================================
    // HINTERGRUND PANEL
    // ============================================================

    private android.graphics.drawable.GradientDrawable
    createPanelBackground() {

        android.graphics.drawable.GradientDrawable background =
                new android.graphics.drawable.GradientDrawable();


        background.setColor(
                PANEL_COLOR
        );


        background.setCornerRadius(
                dp(14)
        );


        background.setStroke(
                dp(2),
                Color.rgb(
                        55,
                        60,
                        70
                )
        );


        return background;
    }


    // ============================================================
    // DUNKLER BUTTON
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


        android.graphics.drawable.GradientDrawable background =
                new android.graphics.drawable.GradientDrawable();


        background.setColor(
                Color.rgb(
                        12,
                        15,
                        20
                )
        );


        background.setCornerRadius(
                dp(13)
        );


        background.setStroke(
                dp(2),
                Color.rgb(
                        70,
                        75,
                        85
                )
        );


        button.setBackground(
                background
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


        android.graphics.drawable.GradientDrawable background =
                new android.graphics.drawable.GradientDrawable();


        background.setColor(
                RED
        );


        background.setCornerRadius(
                dp(13)
        );


        background.setStroke(
                dp(2),
                RED_BORDER
        );


        button.setBackground(
                background
        );


        return button;
    }


    // ============================================================
    // ANDERE SEITEN
    // ============================================================

    private void createPlaceholderPage() {

        FrameLayout root =
                new FrameLayout(this);


        root.setBackgroundColor(
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


        root.addView(
                content,
                params
        );


        setContentView(
                root
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
    // FAHRZEUG DATENKLASSE
    // ============================================================

    private static class Vehicle {

        boolean exists =
                false;

        String brand =
                "";

        String model =
                "";

        String engine =
                "";

        String year =
                "";

        String kilometers =
                "";

        String photo =
                "";


        void clear() {

            exists =
                    false;

            brand =
                    "";

            model =
                    "";

            engine =
                    "";

            year =
                    "";

            kilometers =
                    "";

            photo =
                    "";
        }
    }
}
