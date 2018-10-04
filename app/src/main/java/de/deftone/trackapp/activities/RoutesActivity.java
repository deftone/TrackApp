package de.deftone.trackapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.deftone.trackapp.R;
import de.deftone.trackapp.services.DatabaseGetRouteService;
import de.deftone.trackapp.utils.RecyclerViewAdapter;

import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_ID;
import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_NAME;
import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_TIMESTAMP;

public class RoutesActivity extends AppCompatActivity {

    private Context context = this;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.track_id_recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final List<Integer> trackIds = (ArrayList<Integer>) intent.getSerializableExtra(EXTRA_TRACK_ID);
        final List<Long> trackTimestamps = (ArrayList<Long>) intent.getSerializableExtra(EXTRA_TRACK_TIMESTAMP);
        final List<String> trackNames = (ArrayList<String>) intent.getSerializableExtra(EXTRA_TRACK_NAME);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(trackIds, trackTimestamps, trackNames);
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setListener(new RecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                DatabaseGetRouteService databaseGetRouteService = new DatabaseGetRouteService(context);
                databaseGetRouteService.execute(trackIds.get(position));
            }
        });
    }
}
