package com.aryan.digital_wallet_main.activities;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.aryan.digital_wallet_main.R;
import com.aryan.digital_wallet_main.database.CardEntity;
import com.aryan.digital_wallet_main.utils.SecurityHelper;
import com.aryan.digital_wallet_main.viewmodels.CardViewModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCardActivity extends AppCompatActivity {

    private static final String TAG = "AddCardActivity";
    private EditText editTextCardName, editTextCardNumber, editTextExpiryDate, editTextNotes, editTextQrCodeData;
    private Spinner spinnerCardType, spinnerCategory;
    private Button buttonScanQR, buttonSave, buttonTakePhoto;
    private ImageView imagePreview;
    private CardViewModel cardViewModel;
    private boolean isEditMode = false;
    private int cardId = -1;
    private CardEntity currentCard;
    private Calendar calendar;
    private SecurityHelper securityHelper;
    private Button buttonAddImage;
    private ActivityResultLauncher<Intent> galleryLauncher;


    private ActivityResultLauncher<Intent> cameraLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        securityHelper = new SecurityHelper(this);
        setupToolbar();
        initializeViews();
        setupSpinners();
        setupDatePicker();
        setupViewModel();
        checkEditMode();

        setupCameraLauncher();
        setupButtonListeners();

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Uri imageUri = result.getData().getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                            imagePreview.setVisibility(View.VISIBLE);
                            imagePreview.setImageBitmap(bitmap);
                            Bitmap processedBitmap = preprocessImage(bitmap);
                            runTextRecognition(processedBitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeViews() {
        calendar = Calendar.getInstance();
        editTextCardName = findViewById(R.id.edit_text_card_name);
        editTextCardNumber = findViewById(R.id.edit_text_card_number);
        editTextExpiryDate = findViewById(R.id.edit_text_expiry_date);
        editTextNotes = findViewById(R.id.edit_text_notes);
        editTextQrCodeData = findViewById(R.id.edit_text_qr_code_data);
        spinnerCardType = findViewById(R.id.spinner_card_type);
        spinnerCategory = findViewById(R.id.spinner_category);
        buttonScanQR = findViewById(R.id.button_scan_qr);
        buttonSave = findViewById(R.id.button_save);
        buttonTakePhoto = findViewById(R.id.button_take_photo);
        imagePreview = findViewById(R.id.image_preview);
        buttonAddImage = findViewById(R.id.button_add_image);

    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> cardTypeAdapter = ArrayAdapter.createFromResource(this, R.array.card_types, android.R.layout.simple_spinner_item);
        cardTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCardType.setAdapter(cardTypeAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void setupDatePicker() {
        editTextExpiryDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {

        Calendar today = Calendar.getInstance();


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInView();
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
        datePickerDialog.show();
    }
    private void updateDateInView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editTextExpiryDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void setupViewModel() {
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
    }

    private void checkEditMode() {
        cardId = getIntent().getIntExtra("card_id", -1);
        isEditMode = (cardId != -1);

        if (isEditMode) {
            getSupportActionBar().setTitle("Edit Card");
            loadCard();
        } else {
            getSupportActionBar().setTitle("Add New Card");
        }
    }

    private void setupButtonListeners() {
        buttonScanQR.setOnClickListener(v -> scanQRCode());
        buttonSave.setOnClickListener(v -> saveCard());
        buttonTakePhoto.setOnClickListener(v -> launchCamera());
        buttonAddImage.setOnClickListener(v -> openGallery());

    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }



    private void setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");

                if (bitmap != null) {
                    imagePreview.setVisibility(View.VISIBLE);
                    imagePreview.setImageBitmap(bitmap);
                    Bitmap processedBitmap = preprocessImage(bitmap);
                    runTextRecognition(processedBitmap);
                }
            }
        });
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private Bitmap preprocessImage(Bitmap original) {
        Bitmap processedBitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(processedBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        float contrast = 1.2f;
        float brightness = -30f;
        ColorMatrix contrastMatrix = new ColorMatrix(new float[]{
                contrast, 0, 0, 0, brightness,
                0, contrast, 0, 0, brightness,
                0, 0, contrast, 0, brightness,
                0, 0, 0, 1, 0
        });
        colorMatrix.postConcat(contrastMatrix);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));

        canvas.drawBitmap(original, 0, 0, paint);
        return processedBitmap;
    }
    private void runTextRecognition(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(this::extractTextFromImage)
                .addOnFailureListener(e -> Toast.makeText(this, "OCR failed", Toast.LENGTH_SHORT).show());
    }


    private void extractTextFromImage(Text visionText) {
        String resultText = visionText.getText();
        if (resultText.isEmpty()) {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show();
            return;
        }

        analyzeAndFillFromOCR(resultText);

    }


    private void loadCard() {
        cardViewModel.getCardById(cardId).observe(this, card -> {
            if (card != null) {
                currentCard = card;
                populateForm(card);
            } else {
                Toast.makeText(this, "Card not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateForm(CardEntity card) {
        editTextCardName.setText(card.getCardName());
        editTextCardNumber.setText(card.getCardNumber());
        editTextNotes.setText(card.getNotes());
        editTextQrCodeData.setText(card.getQrCodeData());
        editTextExpiryDate.setText(card.getExpiryDate());
        setSpinnerSelection(spinnerCardType, card.getCardType());
        setSpinnerSelection(spinnerCategory, card.getCategory());
    }

    private void scanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, R.string.scan_cancelled, Toast.LENGTH_LONG).show();
            } else {
                editTextQrCodeData.setText(result.getContents());
                processScannedQRContent(result.getContents());
            }
        }
    }

    private void processScannedQRContent(String qrContent) {
        try {
            JSONObject qrData = new JSONObject(qrContent);
            fillFormFromJSON(qrData);
        } catch (JSONException e) {
            Log.d(TAG, "QR content is not JSON, trying plain text parsing");
            fillFormFromPlainText(qrContent);
        }
    }

    private void fillFormFromJSON(JSONObject qrData) throws JSONException {
        if (qrData.has("cardName")) editTextCardName.setText(qrData.getString("cardName"));
        if (qrData.has("cardNumber")) editTextCardNumber.setText(qrData.getString("cardNumber"));
        if (qrData.has("expiryDate")) editTextExpiryDate.setText(qrData.getString("expiryDate"));
        if (qrData.has("notes")) editTextNotes.setText(qrData.getString("notes"));
        if (qrData.has("cardType")) setSpinnerSelection(spinnerCardType, qrData.getString("cardType"));
        if (qrData.has("category")) setSpinnerSelection(spinnerCategory, qrData.getString("category"));
    }

    private void fillFormFromPlainText(String text) {
        String[] lines = text.split("\\r?\\n");

        String cardNumber = null;
        String expiryDate = null;
        String cardName = null;


        for (String line : lines) {
            line = line.trim();


            if (cardNumber == null && line.matches("\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b")) {
                cardNumber = line.replaceAll("[ -]", "");
            }

            if (expiryDate == null && line.matches(".*(\\d{2}/\\d{2}|\\d{2}-\\d{2}|\\d{2}/\\d{4}|\\d{2}-\\d{4}).*")) {
                Matcher matcher = Pattern.compile("(0[1-9]|1[0-2])[/-](\\d{2,4})").matcher(line);
                if (matcher.find()) {
                    String month = matcher.group(1);
                    String year = matcher.group(2);
                    year = year.length() == 2 ? "20" + year : year;
                    expiryDate = year + "-" + month + "-01";
                }
            }


            if (cardName == null && !line.isEmpty() && line.length() >= 3 && !line.matches(".*\\d.*")) {
                cardName = line;
            }
        }

        if (cardNumber != null) editTextCardNumber.setText(cardNumber);
        if (expiryDate != null) editTextExpiryDate.setText(expiryDate);
        if (cardName != null) editTextCardName.setText(cardName);
    }
    private void analyzeAndFillFromOCR(String fullText) {
        Map<String, String> extractedFields = extractRelevantFields(fullText);

        String name = extractedFields.get("cardName");
        String number = extractedFields.get("cardNumber");
        String expiry = extractedFields.get("expiryDate");

        if (name != null) editTextCardName.setText(name);
        if (number != null) editTextCardNumber.setText(number);
        if (expiry != null) editTextExpiryDate.setText(expiry);

        editTextQrCodeData.setText(fullText);
    }

    private Map<String, String> extractRelevantFields(String ocrText) {
        Map<String, String> result = new HashMap<>();
        List<String> lines = Arrays.asList(ocrText.split("\\r?\\n"));

        Pattern numberPattern = Pattern.compile("\\b\\d{4}([- ]?\\d{4}){3}\\b");
        Pattern expiryPattern = Pattern.compile("(0[1-9]|1[0-2])[/-](\\d{2,4})");
        Pattern nameLabelPattern = Pattern.compile("(name|card holder|member):?\\s*(.+)", Pattern.CASE_INSENSITIVE);
        String[] cardKeywords = { "membership", "student", "employee", "gym", "library", "club", "id card", "loyalty", "pass" };

        String bestNumber = null, bestExpiry = null, bestName = null;
        int scoreNumber = 0, scoreExpiry = 0, scoreName = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            Matcher numberMatcher = numberPattern.matcher(line);
            if (numberMatcher.find()) {
                int score = line.toLowerCase().contains("card") ? 3 : 2;
                if (score > scoreNumber) {
                    bestNumber = numberMatcher.group().replaceAll("[ -]", "");
                    scoreNumber = score;
                }
            }
            Matcher expiryMatcher = expiryPattern.matcher(line);
            if (expiryMatcher.find()) {
                int score = (line.toLowerCase().contains("valid") || line.toLowerCase().contains("exp")) ? 3 : 2;
                if (score > scoreExpiry) {
                    String month = expiryMatcher.group(1);
                    String year = expiryMatcher.group(2);
                    if (year.length() == 2) year = "20" + year;
                    bestExpiry = year + "-" + month + "-01";
                    scoreExpiry = score;
                }
            }
            Matcher nameMatcher = nameLabelPattern.matcher(line);
            if (nameMatcher.find()) {
                String potentialName = nameMatcher.group(2).trim();
                if (potentialName.length() > 2 && scoreName < 4) {
                    bestName = potentialName;
                    scoreName = 4;
                }
            }


            if (scoreName < 3 && !line.matches(".*\\d.*")) {
                for (String keyword : cardKeywords) {
                    if (line.toLowerCase().contains(keyword)) {
                        bestName = line;
                        scoreName = 3;
                        break;
                    }
                }
            }


            if (scoreName < 2 && bestName == null && line.length() > 3 && !line.matches(".*\\d.*")) {
                bestName = line;
                scoreName = 2;
            }
        }

        if (bestName != null) result.put("cardName", bestName);
        if (bestNumber != null) result.put("cardNumber", bestNumber);
        if (bestExpiry != null) result.put("expiryDate", bestExpiry);

        return result;
    }





    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void saveCard() {
        String cardName = editTextCardName.getText().toString().trim();
        String cardNumber = editTextCardNumber.getText().toString().trim();
        String expiryDate = editTextExpiryDate.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();
        String qrData = editTextQrCodeData.getText().toString().trim();
        String cardType = spinnerCardType.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String userId = securityHelper.getCurrentUserEmail();

        if (cardName.isEmpty()) {
            editTextCardName.setError("Card name is required");
            editTextCardName.requestFocus();
            return;
        }

        CardEntity card = isEditMode ? currentCard : new CardEntity();
        card.setCardName(cardName);
        card.setCardNumber(cardNumber);
        card.setExpiryDate(expiryDate);
        card.setNotes(notes);
        card.setQrCodeData(qrData);
        card.setCardType(cardType);
        card.setCategory(category);
        card.setUserId(userId);

        if (isEditMode) {
            cardViewModel.updateCard(card);
            Toast.makeText(this, "Card updated", Toast.LENGTH_SHORT).show();
        } else {
            cardViewModel.insertCard(card);
            Toast.makeText(this, "Card added", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
