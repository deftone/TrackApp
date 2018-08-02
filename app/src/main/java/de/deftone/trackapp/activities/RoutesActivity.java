package de.deftone.trackapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.deftone.trackapp.R;
import de.deftone.trackapp.services.DatabaseGetRouteService;
import de.deftone.trackapp.utils.RecyclerViewAdapter;

import static de.deftone.trackapp.settings.Constants.EXTRA_TRACK_SET;

//todo: nicth nur trackId, sondern mehr info
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
        final ArrayList<Integer> trackIds = (ArrayList<Integer>) intent.getSerializableExtra(EXTRA_TRACK_SET);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(trackIds);
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setListener(new RecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                DatabaseGetRouteService databaseGetRouteService = new DatabaseGetRouteService(context);
                //todo: nur solange wie ahorn routen noch nicht neu abgespeichert!
                if (position >= 8 && position <= 20) {
                    //ahorn talstation bis edelhaus
                    databaseGetRouteService.execute(25, 39);
                } else if (position == 21 || position == 22) {
                    //edelhaus bis ahorn bergstation
                    databaseGetRouteService.execute(40, 41);
                } else if (position >= 23 & position <= 26) {
                    //bergstation bis isskogel gipfel
                    databaseGetRouteService.execute(47, 50);
                } else
                    databaseGetRouteService.execute(trackIds.get(position));
            }
        });
    }
}
