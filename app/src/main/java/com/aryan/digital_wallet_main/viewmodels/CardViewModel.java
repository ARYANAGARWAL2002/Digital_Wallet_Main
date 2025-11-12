package com.aryan.digital_wallet_main.viewmodels;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.aryan.digital_wallet_main.database.AppDatabase;
import com.aryan.digital_wallet_main.database.CardDao;
import com.aryan.digital_wallet_main.database.CardEntity;
import com.aryan.digital_wallet_main.utils.NotificationHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class CardViewModel extends AndroidViewModel {
    private final CardDao cardDao;
    private final LiveData<List<CardEntity>> allCards;
    private final ExecutorService executorService;
    private final NotificationHelper notificationHelper;
    private final MutableLiveData<List<CardEntity>> expiringCardsList = new MutableLiveData<>();
    private String expiryThreshold;
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>();
    public CardViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        cardDao = database.cardDao();
        allCards = cardDao.getAllCards();
        executorService = Executors.newSingleThreadExecutor();
        notificationHelper = new NotificationHelper(application);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        expiryThreshold = dateFormat.format(calendar.getTime());
    }
    public void setSelectedCategory(String category) {
        selectedCategory.setValue(category);
    }
    public LiveData<String> getSelectedCategory() {
        return selectedCategory;
    }
    public LiveData<List<CardEntity>> getAllCards() {
        return allCards;
    }
    public LiveData<List<CardEntity>> getCardsByUserId(String userId) {
        return cardDao.getCardsByUserId(userId);
    }
    public LiveData<List<CardEntity>> getCardsByCategory(String category) {
        return cardDao.getCardsByCategory(category);
    }

    public LiveData<List<CardEntity>> getCardsByUserIdAndCategory(String userId, String category) {
        return cardDao.getCardsByUserIdAndCategory(userId, category);
    }

    public LiveData<CardEntity> getCardById(int id) {
        return cardDao.getCardById(id);
    }

    public LiveData<List<CardEntity>> getExpiringCardsList(String userId) {
        return cardDao.getLiveCardsAboutToExpireByUserId(userId, expiryThreshold);
    }

    public void insertCard(CardEntity card) {
        executorService.execute(() -> cardDao.insertCard(card));
    }

    public void updateCard(CardEntity card) {
        executorService.execute(() -> cardDao.updateCard(card));
    }

    public void deleteCard(CardEntity card) {
        executorService.execute(() -> cardDao.deleteCard(card));
    }

    public void checkExpiringCards(String userId) {
        executorService.execute(() -> {
            List<CardEntity> expiringCards = cardDao.getCardsAboutToExpireByUserId(userId, expiryThreshold);
            if (!expiringCards.isEmpty()) {
                notificationHelper.showExpiringCardsNotification(expiringCards);
            }
        });
    }

    public void refreshExpiringCards(String userId) {
        executorService.execute(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            expiryThreshold = dateFormat.format(calendar.getTime());
        });
    }
}
