package com.aryan.digital_wallet_main.activities;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.aryan.digital_wallet_main.R;
import com.aryan.digital_wallet_main.database.CardEntity;
import com.aryan.digital_wallet_main.utils.QRCodeHelper;
import com.aryan.digital_wallet_main.viewmodels.CardViewModel;

public class CardDetailsActivity extends AppCompatActivity {

    private TextView textViewCardName;
    private TextView textViewCardNumber;
    private TextView textViewCardType;
    private TextView textViewCategory;
    private TextView textViewExpiryDate;
    private TextView textViewNotes;
    private ImageView imageViewQRCode;
    private ImageView imageViewCardImage;
    private CardViewModel cardViewModel;
    private CardEntity currentCard;
    private int cardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Card Details");


        textViewCardName = findViewById(R.id.text_view_card_name);
        textViewCardNumber = findViewById(R.id.text_view_card_number);
        textViewCardType = findViewById(R.id.text_view_card_type);
        textViewCategory = findViewById(R.id.text_view_category);
        textViewExpiryDate = findViewById(R.id.text_view_expiry_date);
        textViewNotes = findViewById(R.id.text_view_notes);
        imageViewQRCode = findViewById(R.id.image_view_qr_code);
        imageViewCardImage = findViewById(R.id.image_view_card_image);


        cardId = getIntent().getIntExtra("card_id", -1);
        if (cardId == -1) {
            Toast.makeText(this, "Error: Card not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);


        cardViewModel.getCardById(cardId).observe(this, new Observer<CardEntity>() {
            @Override
            public void onChanged(CardEntity card) {
                if (card != null) {
                    currentCard = card;
                    displayCardDetails(card);
                } else {
                    Toast.makeText(CardDetailsActivity.this, "Card not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void displayCardDetails(CardEntity card) {
        textViewCardName.setText(card.getCardName());
        textViewCardNumber.setText(card.getCardNumber());
        textViewCardType.setText(card.getCardType());
        textViewCategory.setText(card.getCategory());

        if (card.getExpiryDate() != null && !card.getExpiryDate().isEmpty()) {
            textViewExpiryDate.setText(card.getExpiryDate());
        } else {
            textViewExpiryDate.setText("No expiry date");
        }

        if (card.getNotes() != null && !card.getNotes().isEmpty()) {
            textViewNotes.setText(card.getNotes());
        } else {
            textViewNotes.setText("No notes");
        }


        if (card.getQrCodeData() != null && !card.getQrCodeData().isEmpty()) {
            Bitmap qrCodeBitmap = QRCodeHelper.generateQRCode(card.getQrCodeData());
            if (qrCodeBitmap != null) {
                imageViewQRCode.setVisibility(View.VISIBLE);
                imageViewQRCode.setImageBitmap(qrCodeBitmap);
            } else {
                imageViewQRCode.setVisibility(View.GONE);
            }
        } else {
            imageViewQRCode.setVisibility(View.GONE);
        }


        if (card.getCardImage() != null && !card.getCardImage().isEmpty()) {
            imageViewCardImage.setVisibility(View.VISIBLE);
            imageViewCardImage.setImageResource(R.drawable.ic_launcher_background);
        } else {
            imageViewCardImage.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddCardActivity.class);
            intent.putExtra("card_id", cardId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete) {
            if (currentCard != null) {
                cardViewModel.deleteCard(currentCard);
                Toast.makeText(this, "Card deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}