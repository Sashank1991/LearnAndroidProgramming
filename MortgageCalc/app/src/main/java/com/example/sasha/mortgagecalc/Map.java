package com.example.sasha.mortgagecalc;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;


public class Map extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mapView;
    DatabaseHelper db;
    Cursor _csr;
    HashMap<String, HashMap> extraMarkerInfo;
    HashMap<String, String> extraMarkerInfosub;

    public Map() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.fragment_map, container, false);
        db = new DatabaseHelper(getActivity());
        _csr = db.getAllProperties();
        return _view;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        extraMarkerInfo = new HashMap<String, HashMap>();
        if (_csr.moveToFirst()) {
            do {
                extraMarkerInfosub = new HashMap<String, String>();
                extraMarkerInfosub.put("id", _csr.getString(_csr.getColumnIndex("id")));
                extraMarkerInfosub.put("type", _csr.getString(_csr.getColumnIndex("type")));
                extraMarkerInfosub.put("streetAdd", _csr.getString(_csr.getColumnIndex("streetAdd")));
                extraMarkerInfosub.put("city", _csr.getString(_csr.getColumnIndex("city")));
                extraMarkerInfosub.put("lat", _csr.getString(_csr.getColumnIndex("lat")));
                extraMarkerInfosub.put("long", _csr.getString(_csr.getColumnIndex("long")));
                extraMarkerInfosub.put("loneAmt", _csr.getString(_csr.getColumnIndex("loneAmt")));
                extraMarkerInfosub.put("apr", _csr.getString(_csr.getColumnIndex("apr")));
                extraMarkerInfosub.put("monthlyPayment", _csr.getString(_csr.getColumnIndex("monthlyPayment")));
                extraMarkerInfosub.put("propertyPrice", _csr.getString(_csr.getColumnIndex("propertyPrice")));
                extraMarkerInfosub.put("downPayment", _csr.getString(_csr.getColumnIndex("downPayment")));
                extraMarkerInfosub.put("term", _csr.getString(_csr.getColumnIndex("term")));
                extraMarkerInfosub.put("state", _csr.getString(_csr.getColumnIndex("state")));
                extraMarkerInfosub.put("zip", _csr.getString(_csr.getColumnIndex("zip")));
                extraMarkerInfo.put(_csr.getString(_csr.getColumnIndex("id")), extraMarkerInfosub);
                LatLng dst = new LatLng(Double.parseDouble(_csr.getString(_csr.getColumnIndex("lat"))), Double.parseDouble(_csr.getString(_csr.getColumnIndex("long"))));
                mMap.addMarker(new MarkerOptions().position(dst).title(_csr.getString(_csr.getColumnIndex("id"))));
                Float zoomLevel = 8.0f ;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dst, zoomLevel));
                mMap.setOnMarkerClickListener(this);
            } while (_csr.moveToNext());
        }


    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        final HashMap<String, String> currentData = new HashMap<String, String>();
        currentData.putAll(extraMarkerInfo.get(marker.getTitle()));
        //public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Property Type : " + currentData.get("type") + "\n" +
                "Street Address: " + currentData.get("streetAdd") + "\n" +
                "City: " + currentData.get("city") + "\n" +
                "Loan Amount: " + currentData.get("loneAmt") + "\n" +
                "APR: " + currentData.get("apr") + "\n" +
                "Monthly payment: " + currentData.get("monthlyPayment") + "\n" +
                "");
        alertDialogBuilder.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Put the value
                        Calculator ldf = new Calculator();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("loadData", currentData);
                        ldf.setArguments(bundle);

                        FrameLayout fl = (FrameLayout) getActivity().findViewById(R.id.calc);
                        fl.removeAllViews();
                        FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragTransaction.add(R.id.calc, ldf, "");
                        fragTransaction.commit();
                        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
                        TabLayout.Tab tab = tabLayout.getTabAt(0);
                        tab.select();
                    }
                });

        alertDialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteProperty(Integer.parseInt(marker.getTitle()));
                dialog.dismiss();
                Toast.makeText(getActivity(), R.string.deleteProperty, Toast.LENGTH_LONG).show();
                ReloadMapFragment();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button buttonbackground = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonbackground.setBackgroundColor(Color.parseColor(getString(R.string.buttons)));
        buttonbackground.setTextColor(Color.parseColor(getString(R.string.buttonsText)));

        Button buttonbackground1 = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonbackground1.setBackgroundColor(Color.parseColor(getString(R.string.buttons)));
        buttonbackground1.setTextColor(Color.parseColor(getString(R.string.buttonsText)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 20, 0);
        buttonbackground.setLayoutParams(params);
        return true;
    }

    //Reload Fragment for editing
    protected void ReloadMapFragment() {
        FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.map, new Map());
        fragTransaction.commit();

    }
}



