package com.example.weatherappandroidclient.classes;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.weatherappandroidclient.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RangeGraphActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_graph);
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        RangeCategorySeries series =
                new RangeCategorySeries("High low temperature");

        series.add(32, 85);
        series.add(0, 50);
        series.add(-20, 35);
        series.add(60, 110);
        mRenderer.addXTextLabel(0, "day");

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        dataset.addSeries(series.toXYSeries());

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setDisplayChartValues(true);
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMax(110);
        mRenderer.setYAxisMin(-20);
        mRenderer.setBarWidth(HelperFunctions.dpToPx(20, this));

        mRenderer.setInScroll(true);
        renderer.setChartValuesTextSize(12);
        renderer.setChartValuesFormat(new DecimalFormat("#"));
        renderer.setColor(Color.GREEN);
        GraphicalView chartView = ChartFactory.getRangeBarChartView(
                this, dataset,
                mRenderer, BarChart.Type.DEFAULT);

        ConstraintLayout view = findViewById(R.id.graphView);
        chartView.setMinimumHeight(300);
        chartView.setMinimumWidth(300);
        view.addView(chartView);
    }
}