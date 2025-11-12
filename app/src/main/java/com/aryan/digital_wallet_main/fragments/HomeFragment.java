package com.aryan.digital_wallet_main.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aryan.digital_wallet_main.R;
import com.aryan.digital_wallet_main.adapters.CardAdapter;
import com.aryan.digital_wallet_main.adapters.CategoryAdapter;
import com.aryan.digital_wallet_main.database.CardEntity;
import com.aryan.digital_wallet_main.utils.NotificationHelper;
import com.aryan.digital_wallet_main.utils.SecurityHelper;
import com.aryan.digital_wallet_main.viewmodels.CardViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class HomeFragment extends Fragment {
    private RecyclerView recentCardsRecycler, expiringCardsRecycler, categoriesRecycler;
    private CardAdapter recentCardsAdapter, expiringCardsAdapter;
    private CategoryAdapter categoryAdapter;
    private CardViewModel cardViewModel;
    private TextView tvNoRecentCards, tvNoExpiringCards, tvUserGreeting;
    private SecurityHelper securityHelper;
    private NotificationHelper notificationHelper;  // Add NotificationHelper instance

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        securityHelper = new SecurityHelper(requireContext());

        tvUserGreeting = view.findViewById(R.id.tv_user_greeting);
        tvNoRecentCards = view.findViewById(R.id.tv_no_recent_cards);
        tvNoExpiringCards = view.findViewById(R.id.tv_no_expiring_cards);
        recentCardsRecycler = view.findViewById(R.id.recent_cards_recycler);
        expiringCardsRecycler = view.findViewById(R.id.expiring_cards_recycler);
        categoriesRecycler = view.findViewById(R.id.categories_recycler);
        notificationHelper = new NotificationHelper(requireContext());
        String userName = securityHelper.getCurrentUserName();
        tvUserGreeting.setText("Hello, " + userName + "!");
        categoryAdapter = new CategoryAdapter(getContext(), getCategoryList(), this::onCategoryClicked);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesRecycler.setAdapter(categoryAdapter);
        recentCardsAdapter = new CardAdapter(getContext(), new ArrayList<>());
        recentCardsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recentCardsRecycler.setAdapter(recentCardsAdapter);
        expiringCardsAdapter = new CardAdapter(getContext(), new ArrayList<>());
        expiringCardsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        expiringCardsRecycler.setAdapter(expiringCardsAdapter);
        cardViewModel = new ViewModelProvider(requireActivity()).get(CardViewModel.class);
        String userId = securityHelper.getCurrentUserEmail();
        cardViewModel.getCardsByUserId(userId).observe(getViewLifecycleOwner(), cardEntities -> {
            if (cardEntities != null && !cardEntities.isEmpty()) {
                List<CardEntity> recent = cardEntities.size() > 5 ? cardEntities.subList(0, 5) : cardEntities;
                recentCardsAdapter.setCards(recent);
                recentCardsRecycler.setVisibility(View.VISIBLE);
                tvNoRecentCards.setVisibility(View.GONE);
            } else {
                recentCardsRecycler.setVisibility(View.GONE);
                tvNoRecentCards.setVisibility(View.VISIBLE);
            }
        });
        cardViewModel.getExpiringCardsList(userId).observe(getViewLifecycleOwner(), expiringCards -> {
            if (expiringCards != null && !expiringCards.isEmpty()) {
                expiringCardsAdapter.setCards(expiringCards);
                expiringCardsRecycler.setVisibility(View.VISIBLE);
                tvNoExpiringCards.setVisibility(View.GONE);
                // Trigger notification for expiring cards
                notificationHelper.showExpiringCardsNotification(expiringCards);
            } else {
                expiringCardsRecycler.setVisibility(View.GONE);
                tvNoExpiringCards.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private List<String> getCategoryList() {
        return Arrays.asList("Work", "Education", "Travel", "Other");
    }

    private void onCategoryClicked(String category) {
        cardViewModel.setSelectedCategory(category);
        requireActivity().findViewById(R.id.navigation_cards).performClick();
    }
    @Override
    public void onResume() {
        super.onResume();
        String userId = securityHelper.getCurrentUserEmail();
        cardViewModel.refreshExpiringCards(userId);
    }
}
