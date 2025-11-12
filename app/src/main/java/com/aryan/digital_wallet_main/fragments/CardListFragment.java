package com.aryan.digital_wallet_main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryan.digital_wallet_main.R;
import com.aryan.digital_wallet_main.adapters.CardAdapter;
import com.aryan.digital_wallet_main.database.CardEntity;
import com.aryan.digital_wallet_main.utils.SecurityHelper;
import com.aryan.digital_wallet_main.viewmodels.CardViewModel;

import java.util.ArrayList;
import java.util.List;

public class CardListFragment extends Fragment {

    private CardViewModel cardViewModel;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private Spinner spinnerCategory;
    private TextView textViewNoCards;
    private SecurityHelper securityHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_list, container, false);

        securityHelper = new SecurityHelper(requireContext());

        recyclerView = view.findViewById(R.id.recycler_view_cards);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        textViewNoCards = view.findViewById(R.id.text_view_no_cards);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cardAdapter = new CardAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(cardAdapter);

        setupCategorySpinner();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardViewModel = new ViewModelProvider(requireActivity()).get(CardViewModel.class);
        String userId = securityHelper.getCurrentUserEmail();


        cardViewModel.getSelectedCategory().observe(getViewLifecycleOwner(), selectedCategory -> {
            if (selectedCategory != null) {
                int spinnerIndex = getSpinnerIndex(spinnerCategory, selectedCategory);
                if (spinnerIndex >= 0) {
                    spinnerCategory.setSelection(spinnerIndex);
                }
            }
        });

        observeCards(userId, null);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.category_filter_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                String userId = securityHelper.getCurrentUserEmail();

                if (selectedCategory.equals("All Categories")) {
                    observeCards(userId, null);
                } else {
                    observeCards(userId, selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
    }

    private void observeCards(String userId, String category) {
        if (category == null || category.isEmpty()) {
            cardViewModel.getCardsByUserId(userId).observe(getViewLifecycleOwner(), this::updateCardsList);
        } else {
            cardViewModel.getCardsByUserIdAndCategory(userId, category).observe(getViewLifecycleOwner(), this::updateCardsList);
        }
    }

    private void updateCardsList(List<CardEntity> cards) {
        cardAdapter.setCards(cards);
        if (cards == null || cards.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewNoCards.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewNoCards.setVisibility(View.GONE);
        }
    }
}
