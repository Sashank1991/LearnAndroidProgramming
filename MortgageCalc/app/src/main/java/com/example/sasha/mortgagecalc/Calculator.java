package com.example.sasha.mortgagecalc;


import android.app.AlertDialog;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class Calculator extends Fragment implements View.OnClickListener {

    EditText txtPropertyType;
    EditText txtStreetAddress;
    EditText txtCity;
    Spinner txtState;
    TextView dummyError;
    EditText txtZipCode;
    EditText txtPropertyPrice;
    EditText txtDownPayment;
    EditText txtAPR;
    Spinner txtTerm;
    ArrayAdapter<String> adapter;
    Button btnNew;
    Button btnCalc;
    Button btnSave;
    TextView txtEMI;
    final String[] terms = {"15", "30"};
    DatabaseHelper db;

    ProgressDialog progress;

    final String[] usStates = {
            "-select-",
            "AL Alabama",
            "AK Alaska",
            "AZ Arizona",
            "AR Arkansas",
            "CA California",
            "CO Colorado",
            "CT Connecticut",
            "DE Delaware",
            "DC District of Columbia",
            "FL Florida",
            "GA Georgia",
            "HI Hawaii",
            "ID Idaho",
            "IL Illinois",
            "IN Indiana",
            "IA Iowa",
            "KS Kansas",
            "KY Kentucky",
            "LA Louisiana",
            "ME Maine",
            "MD Maryland",
            "MA Massachusetts",
            "MI Michigan",
            "MN Minnesota",
            "MS Mississippi",
            "MO Missouri",
            "MT Montana",
            "NE Nebraska",
            "NV Nevada",
            "NH New Hampshire",
            "NJ New Jersey",
            "NM New Mexico",
            "NY New York",
            "NC North Carolina",
            "ND North Dakota",
            "OH Ohio",
            "OK Oklahoma",
            "OR Oregon",
            "PA Pennsylvania",
            "RI Rhode Island",
            "SC South Carolina",
            "SD South Dakota",
            "TN Tennessee",
            "TX Texas",
            "UT Utah",
            "VT Vermont",
            "VA Virginia",
            "WA Washington",
            "WV West Virginia",
            "WI Wisconsin",
            "WY Wyoming"
    };

    // load or new Fragment
    boolean _new;
    String _id;

    public Calculator() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View _calc = inflater.inflate(R.layout.fragment_calculator, container, false);
        _new = true;
        txtPropertyType = (EditText) _calc.findViewById(R.id.txtPropertyType);
        txtStreetAddress = (EditText) _calc.findViewById(R.id.txtStreetAddress);
        txtCity = (EditText) _calc.findViewById(R.id.txtCity);
        txtState = (Spinner) _calc.findViewById(R.id.txtState);
        dummyError = (TextView) _calc.findViewById(R.id.dummyError);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, usStates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtState.setAdapter(adapter);
        txtZipCode = (EditText) _calc.findViewById(R.id.txtZipCode);
        txtPropertyPrice = (EditText) _calc.findViewById(R.id.txtPropertyPrice);
        txtDownPayment = (EditText) _calc.findViewById(R.id.txtDownPayment);
        txtAPR = (EditText) _calc.findViewById(R.id.txtAPR);
        txtTerm = (Spinner) _calc.findViewById(R.id.txtTerm);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, terms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtTerm.setAdapter(adapter);
        txtEMI = (TextView) _calc.findViewById(R.id.txtEMI);
        btnNew = (Button) _calc.findViewById(R.id.btnNew);
        btnNew.setOnClickListener(this);
        btnCalc = (Button) _calc.findViewById(R.id.btnCalc);
        btnCalc.setOnClickListener(this);
        btnSave = (Button) _calc.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        db = new DatabaseHelper(getActivity());
        progress = new ProgressDialog(getActivity());
        progress.setTitle(getString(R.string.Loading));
        progress.setMessage(getString(R.string.LoadingMessage));
        progress.setCancelable(false);

        HashMap<String, String> mMap = new HashMap<String, String>();
        Bundle b = this.getArguments();
        if (b != null && b.getSerializable("loadData") != null) {
            _new = false;
            mMap = (HashMap<String, String>) b.getSerializable("loadData");
            _id = mMap.get("id");
            txtPropertyType.setText(mMap.get("type"));
            txtStreetAddress.setText(mMap.get("streetAdd"));
            txtCity.setText(mMap.get("city"));
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, usStates);
            txtState.setSelection(adapter.getPosition(mMap.get("state")));
            txtZipCode.setText(mMap.get("zip"));
            txtPropertyPrice.setText(mMap.get("propertyPrice"));
            txtDownPayment.setText(mMap.get("downPayment"));
            txtAPR.setText(mMap.get("apr"));
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, terms);
            txtTerm.setSelection(adapter.getPosition(mMap.get("term")));
        }

        return _calc;
    }

    //Handling click events
    @Override
    public void onClick(View view) {
        progress.show();
        if (view.getId() == R.id.btnNew) {
            clearFields();
            progress.dismiss();
        } else if (view.getId() == R.id.btnCalc) {
            ClaculateEMI();
            progress.dismiss();
        } else if (view.getId() == R.id.btnSave) {
            if (!ClaculateEMI()) {
                String _propertyType = txtPropertyType.getText().toString().trim();
                String _StreetAddress = txtStreetAddress.getText().toString().trim();
                String _city = txtCity.getText().toString().trim();
                String _state = txtState.getSelectedItem().toString().trim();
                String _zipCode = txtZipCode.getText().toString().trim();

                boolean error = false;
                if (_propertyType.isEmpty() || !_propertyType.matches("^[a-zA-Z0-9\\s]*$")) {
                    error = true;
                    txtPropertyType.setError(getString(R.string.errPropertyType));
                }

                if (_StreetAddress.isEmpty() || !_StreetAddress.matches("^[a-zA-Z0-9\\s]*$")) {
                    error = true;
                    txtStreetAddress.setError(getString(R.string.errStreetAddress));
                }

                if (_city.isEmpty() || !_city.matches("^[a-zA-Z0-9\\s]*$")) {
                    error = true;
                    txtCity.setError(getString(R.string.errCity));
                }
                if (_state == "-select-") {
                    error = true;
                    dummyError.setError(getString(R.string.errState));
                }
                if (_zipCode.isEmpty() || !_zipCode.matches("^[0-9]{5}(?:-[0-9]{4})?$")) {
                    error = true;
                    txtZipCode.setError(getString(R.string.errZipCode));
                }

                if (!error) {
                    String address = _StreetAddress + "," + _city + "," + _state + ", " + _zipCode;
                    try {
                        new GeocoderTask().execute(address);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progress.dismiss();
                    }

                } else {
                    progress.dismiss();
                }

            }
        } else {
            progress.dismiss();
        }
    }

    // Address Saving
    public static String[] getLatLongPositions(String address) throws Exception {
        int response = 0;
        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=true");
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        httpConnection.connect();
        response = httpConnection.getResponseCode();
        if (response == 200) {
            DocumentBuilder _documentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = _documentbuilder.parse(httpConnection.getInputStream());
            XPathFactory xml = XPathFactory.newInstance();
            XPath xmlResolver = xml.newXPath();
            XPathExpression expr = xmlResolver.compile("/GeocodeResponse/status");
            String status = (String) expr.evaluate(document, XPathConstants.STRING);
            if (status.equals("OK")) {
                expr = xmlResolver.compile("//geometry/location/lat");
                String latitude = (String) expr.evaluate(document, XPathConstants.STRING);
                expr = xmlResolver.compile("//geometry/location/lng");
                String longitude = (String) expr.evaluate(document, XPathConstants.STRING);
                return new String[]{latitude, longitude};
            } else {
                throw new Exception("Error occured from Google API " + status);
            }
        }
        return null;
    }


    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<String>> {
        private List _lst;

        @Override
        protected List<String> doInBackground(String... locationName) {
            try {
                String[] _latlng = getLatLongPositions(locationName[0]);
                _lst = new ArrayList();
                Collections.addAll(_lst, _latlng);
            } catch (Exception e) {
                e.printStackTrace();
                progress.dismiss();

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> addresses) {
            if (!(_lst == null && _lst.isEmpty())) {
                String _propertyType = txtPropertyType.getText().toString().trim();
                String _StreetAddress = txtStreetAddress.getText().toString().trim();
                String _city = txtCity.getText().toString().trim();
                String _state = txtState.getSelectedItem().toString().trim();
                String _zipCode = txtZipCode.getText().toString().trim();
                String _apr = txtAPR.getText().toString().trim();
                String _term = txtTerm.getSelectedItem().toString().trim();
                String _price = txtPropertyPrice.getText().toString().trim();
                String _downpayment = txtDownPayment.getText().toString().trim();
                String _EMI = txtEMI.getText().toString().trim();

                List<String> _data = new ArrayList<String>();
                if (!_new) {
                    _data.add(_id);
                }
                _data.add(_propertyType);
                _data.add(_StreetAddress);
                _data.add(_city);
                Double LoanAmount = Double.parseDouble(_price) - Double.parseDouble(_downpayment);
                _data.add(LoanAmount.toString());
                _data.add(_apr);
                _data.add(_lst.get(0).toString());
                _data.add(_lst.get(1).toString());
                _data.add(_EMI);
                _data.add(_price);
                _data.add(_downpayment);
                _data.add(_term);
                _data.add(_state);
                _data.add(_zipCode);
                if (!_new) {
                    db.updateProperty(_data);
                } else {
                    db.insertProperty(_data);
                }
                // reload Map Fragment
                ReloadMapFragment();
                clearFields();
                progress.dismiss();
            } else {
                progress.dismiss();
                new AlertDialog.Builder(getActivity())
                        .setTitle("Address Validation")
                        .setMessage("The entered address seems to be invalid.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }


    //EMI Calculation
    public boolean ClaculateEMI() {
        clearErrors();
        String _apr = txtAPR.getText().toString().trim();
        String _term = txtTerm.getSelectedItem().toString().trim();
        String _price = txtPropertyPrice.getText().toString().trim();
        String _downpayment = txtDownPayment.getText().toString().trim();

        boolean error = false;
        if (_apr.isEmpty() || !_apr.matches("[-+]?[0-9]*\\.?[0-9]*")) {
            error = true;
            txtAPR.setError(getString(R.string.errApr));
        }

        if (_price.isEmpty() || !_price.matches("[-+]?[0-9]*\\.?[0-9]*")) {
            error = true;
            txtPropertyPrice.setError(getString(R.string.errPropertyPrice));
        }

        if (_downpayment.isEmpty() || !_downpayment.matches("[-+]?[0-9]*\\.?[0-9]*")) {
            error = true;
            txtDownPayment.setError(getString(R.string.errDownPayment));
        }

        if (!error) {

            Double monthlyInterestRate = Double.parseDouble(_apr) / (12 * 100);
            Integer npr = Integer.parseInt(_term) * 12;
            Double pv = Double.parseDouble(_price) - Double.parseDouble(_downpayment);

            Double _monthlyPayment = pv * ((monthlyInterestRate * Math.pow((monthlyInterestRate + 1), npr)) / (Math.pow((monthlyInterestRate + 1), npr) - 1));
            DecimalFormat df = new DecimalFormat("####0.0000");
            txtEMI.setText(df.format(_monthlyPayment).toString());

        } else {
            progress.dismiss();
        }
        return error;
    }

    //clearing Errors
    public void clearErrors() {

        txtPropertyType.setError(null);
        txtStreetAddress.setError(null);
        txtCity.setError(null);
        txtZipCode.setError(null);
        txtPropertyPrice.setError(null);
        txtDownPayment.setError(null);
        txtAPR.setError(null);
        txtEMI.setError(null);
        dummyError.setError(null);
    }

    //clearing fields
    public void clearFields() {

        clearErrors();
        _new = true;
        txtPropertyType.setText("");
        txtStreetAddress.setText("");
        txtCity.setText("");
        txtState.setSelection(0);
        txtZipCode.setText("");
        txtPropertyPrice.setText("");
        txtDownPayment.setText("");
        txtAPR.setText("");

        txtTerm.setSelection(0);
        txtEMI.setText(getString(R.string.enterData));
        txtPropertyType.requestFocus();
    }

    //Reload Fragment
    protected void ReloadMapFragment() {
        FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.map, new Map());
        fragTransaction.commit();

    }
}






