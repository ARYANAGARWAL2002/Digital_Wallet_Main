package com.aryan.digital_wallet_main.adapters;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aryan.digital_wallet_main.R;
import com.aryan.digital_wallet_main.activities.CardDetailsActivity;
import com.aryan.digital_wallet_main.database.CardEntity;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardEntity> cardList;
    private final Context context;

    public CardAdapter(Context context, List<CardEntity> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardEntity card = cardList.get(position);

        holder.cardName.setText(card.getCardName());
        holder.cardType.setText(card.getCardType());

        String number = card.getCardNumber();
        if (number != null && number.length() >= 4) {
            String last4 = number.substring(number.length() - 4);
            holder.cardNumber.setText("•••• •••• •••• " + last4);
        } else {
            holder.cardNumber.setText("•••• •••• ••••");
        }

        if (card.getExpiryDate() != null && !card.getExpiryDate().isEmpty()) {
            holder.expiryDate.setText("Expires: " + card.getExpiryDate());
        } else {
            holder.expiryDate.setText("No Expiration Date");
        }

        if (holder.cardImage != null) {
            holder.cardImage.setVisibility(View.VISIBLE);
            holder.cardImage.setImageResource(R.drawable.ic_card_logo);
        }


        int[][] gradientColors = new int[][]{
                {Color.parseColor("#3F51B5"), Color.parseColor("#2196F3")}, // blue
                {Color.parseColor("#4CAF50"), Color.parseColor("#81C784")}, // green
                {Color.parseColor("#FF5722"), Color.parseColor("#FF8A65")}, // orange
                {Color.parseColor("#9C27B0"), Color.parseColor("#E040FB")}, // purple
                {Color.parseColor("#00BCD4"), Color.parseColor("#4DD0E1")}, // teal
                {Color.parseColor("#FFC107"), Color.parseColor("#FFD54F")}, // amber
                {Color.parseColor("#795548"), Color.parseColor("#A1887F")}  // brown
        };


        String key = card.getCardName();
        int hash = Math.abs(key.hashCode());
        int index = hash % gradientColors.length;

        int[] selectedColors = gradientColors[index];
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                selectedColors
        );
        gradient.setCornerRadius(32f);
        holder.cardRoot.setBackground(gradient);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CardDetailsActivity.class);
            intent.putExtra("card_id", card.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cardList == null ? 0 : cardList.size();
    }

    public void setCards(List<CardEntity> cards) {
        this.cardList = cards;
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardName, cardNumber, cardType, expiryDate;
        ImageView cardImage;
        View cardRoot;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.card_name);
            cardNumber = itemView.findViewById(R.id.card_number);
            cardType = itemView.findViewById(R.id.card_type);
            expiryDate = itemView.findViewById(R.id.expiry_date);
            cardImage = itemView.findViewById(R.id.card_image);
            cardRoot = itemView.findViewById(R.id.card_root);
        }
    }
}
