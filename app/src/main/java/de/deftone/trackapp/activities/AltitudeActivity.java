package de.deftone.trackapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.deftone.trackapp.R;
import de.deftone.trackapp.model.MyLocation;

import static de.deftone.trackapp.settings.Constants.EXTRA_LOCATION_LIST;
import static de.deftone.trackapp.settings.Constants.SHARED_PREF_NAME;


public class AltitudeActivity extends AppCompatActivity {

    @BindView(R.id.graph)
    GraphView statisticGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altitude);
        ButterKnife.bind(this);

        //get locations
        Intent intent = getIntent();
        ArrayList<MyLocation> myLocations = (ArrayList<MyLocation>) intent.getSerializableExtra(EXTRA_LOCATION_LIST);

        // get route infos for title
        int trackId = myLocations.get(0).getTrackId();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String routeName = pref.getString(String.valueOf(trackId), "-");
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(myLocations.get(0).getTimestamp() / 1000,
                0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date = formatter.format(localDateTime);
        //adjust title of activity
        setTitle(routeName + " (" + date + ")");

        createBarChartGraph(myLocations);
    }

    private void createBarChartGraph(List<MyLocation> locations) {

//        DataPoint[] dataPoints = new DataPoint[locations.size()];
        List<DataPoint> dataPoints = new ArrayList<>();

        int i = 0;
        for (MyLocation location : locations) {
            if (location.getVerticalAccuracy() < 15 && location.getAltitude() > 0) {
//                dataPoints.add(new DataPoint(i, location.getAltitude()));
//            dataPoints[i] = new DataPoint(i, location.getAltitude());
                dataPoints.add(new DataPoint(location.getDistance() / 1000, location.getAltitude()));
//            dataPoints[i] = new DataPoint(location.getDistance() / 1000, location.getAltitude());
                i++;
            }
        }
        float maxDistance = locations.get(locations.size() - 2).getDistance() / 1000;

        DataPoint[] dataPoints2 = dataPoints.toArray(new DataPoint[0]);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints2);
        statisticGraph.addSeries(series);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(1);
        //change line color
        series.setColor(Color.parseColor(getResources().getString(0 + R.color.colorAccent)));

        // set date label formatter
//        statisticGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        statisticGraph.getGridLabelRenderer().setNumHorizontalLabels(4); //nur wenige, da sich labels sonst ueberlagern
//        statisticGraph.getGridLabelRenderer().setNumVerticalLabels(); //nur wenige, da sich labels sonst ueberlagern
        // as we use dates as labels, the human rounding to nice readable numbers is not necessary
//        statisticGraph.getGridLabelRenderer().setHumanRounding(false);

//        statisticGraph.getViewport().setMinY(0.0);
        statisticGraph.getViewport().setYAxisBoundsManual(true);
        statisticGraph.getViewport().setMinX(0);
        statisticGraph.getViewport().setMaxX(maxDistance);
//        statisticGraph.getViewport().setMaxX(locations.size() - 1);
        statisticGraph.getViewport().setXAxisBoundsManual(true);
        GridLabelRenderer gridLabel = statisticGraph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("km");
//        gridLabel.setVerticalAxisTitle("m");
    }


}
