package com.aryan.digital_wallet_main.utils;



import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;


public class QRCodeHelper {

    // Default QR code size
    private static final int QR_CODE_SIZE = 400;


    public static Bitmap generateQRCode(String data) {
        return generateQRCode(data, QR_CODE_SIZE);
    }


    public static Bitmap generateQRCode(String data, int size) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size);

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Bitmap generateCardQRCode(String cardName, String cardNumber,
                                            String expiryDate, String cardType,
                                            String category, String notes) {
        try {
            JSONObject json = new JSONObject();
            json.put("cardName", cardName);
            json.put("cardNumber", cardNumber);

            if (expiryDate != null && !expiryDate.isEmpty()) {
                json.put("expiryDate", expiryDate);
            }
            if (cardType != null && !cardType.isEmpty()) {
                json.put("cardType", cardType);
            }
            if (category != null && !category.isEmpty()) {
                json.put("category", category);
            }
            if (notes != null && !notes.isEmpty()) {
                json.put("notes", notes);
            }

            return generateQRCode(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}