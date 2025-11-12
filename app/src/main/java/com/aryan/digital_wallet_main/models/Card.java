package com.aryan.digital_wallet_main.models;



/**
 * Model class representing a Card in the application.
 * This class is used for business logic and can be mapped to and from CardEntity.
 */
public class Card {

    private int id;
    private String cardName;
    private String cardNumber;
    private String cardType;
    private String category;
    private String expiryDate;
    private String notes;
    private String qrCodeData;
    private String cardImage;

    public Card() {
        // Empty constructor
    }

    public Card(int id, String cardName, String cardNumber, String cardType, String category,
                String expiryDate, String notes, String qrCodeData, String cardImage) {
        this.id = id;
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.category = category;
        this.expiryDate = expiryDate;
        this.notes = notes;
        this.qrCodeData = qrCodeData;
        this.cardImage = cardImage;
    }

    // Convert CardEntity to Card
    public static Card fromEntity(com.aryan.digital_wallet_main.database.CardEntity entity) {
        Card card = new Card();
        card.setId(entity.getId());
        card.setCardName(entity.getCardName());
        card.setCardNumber(entity.getCardNumber());
        card.setCardType(entity.getCardType());
        card.setCategory(entity.getCategory());
        card.setExpiryDate(entity.getExpiryDate());
        card.setNotes(entity.getNotes());
        card.setQrCodeData(entity.getQrCodeData());
        card.setCardImage(entity.getCardImage());
        return card;
    }

    // Convert Card to CardEntity
    public com.aryan.digital_wallet_main.database.CardEntity toEntity() {
        com.aryan.digital_wallet_main.database.CardEntity entity = new com.aryan.digital_wallet_main.database.CardEntity();
        entity.setId(this.id);
        entity.setCardName(this.cardName);
        entity.setCardNumber(this.cardNumber);
        entity.setCardType(this.cardType);
        entity.setCategory(this.category);
        entity.setExpiryDate(this.expiryDate);
        entity.setNotes(this.notes);
        entity.setQrCodeData(this.qrCodeData);
        entity.setCardImage(this.cardImage);
        return entity;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }
}