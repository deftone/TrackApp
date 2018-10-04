package de.deftone.trackapp.utils;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import de.deftone.trackapp.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Integer> trackIds;
    private List<Long> timestamps;
    private List<String> names;
    private Listener listener;

    public interface Listener {
        void onClick(int position);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public RecyclerViewAdapter(List<Integer> trackIdList,
                               List<Long> timestampList,
                               List<String> nameList) {
        this.trackIds = trackIdList;
        this.timestamps = timestampList;
        this.names = nameList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_routes, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        TextView textViewId = cardView.findViewById(R.id.text_view_track_id);
        TextView textViewDate = cardView.findViewById(R.id.text_view_track_date);
        TextView textViewName = cardView.findViewById(R.id.text_view_track_name);

        textViewId.setText(String.valueOf(trackIds.get(position)));

        LocalDateTime dateTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamps.get(position)),
                        TimeZone.getDefault().toZoneId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        textViewDate.setText(dateTime.format(dateTimeFormatter));
        textViewName.setText(names.get(position));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
