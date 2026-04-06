package com.example.travelcompanion;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare UI components
    Spinner Category, From, To;
    EditText Value;
    Button Convert;
    TextView Result;

    // Categories shown in the first spinner
    String[] categories = {"Currency", "Fuel & Distance", "Temperature"};

    // Units for currency conversion
    String[] currencyUnits = {"USD", "AUD", "EUR", "JPY", "GBP"};

    // Units for fuel, volume, and distance conversion
    String[] fuelDistanceUnits = {"mpg", "km/L", "Gallon (US)", "Liters", "Nautical Mile", "Kilometers"};

    // Units for temperature conversion
    String[] temperatureUnits = {"Celsius", "Fahrenheit", "Kelvin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect Java variables to XML components using their IDs
        Category = findViewById(R.id.Category);
        From = findViewById(R.id.From);
        To = findViewById(R.id.To);
        Value = findViewById(R.id.Value);
        Convert = findViewById(R.id.Convert);
        Result = findViewById(R.id.Result);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Category.setAdapter(categoryAdapter);
        // By default, show currency units first
        updateUnitSpinners("Currency");

        // Change source and destination unit lists when category changes
        Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = Category.getSelectedItem().toString();
                updateUnitSpinners(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed here
            }
        });

        // Run conversion when button is clicked
        Convert.setOnClickListener(v -> {

            // Get value entered by the user
            String inputText = Value.getText().toString().trim();

            // Convert input text to double
            double inputValue = Double.parseDouble(inputText);

            // Get selected category and units
            String category = Category.getSelectedItem().toString();
            String fromUnit = From.getSelectedItem().toString();
            String toUnit = To.getSelectedItem().toString();

            // Call method to calculate converted value
            double result = convertValue(category, fromUnit, toUnit, inputValue);

            // Display result rounded to 2 decimal places
            Result.setText("Converted Value: " + String.format("%.2f", result));
        });
    }

    // Method to update the From and To spinners based on selected category
    private void updateUnitSpinners(String category) {
        String[] units;

        switch (category) {
            case "Fuel & Distance":
                units = fuelDistanceUnits;
                break;
            case "Temperature":
                units = temperatureUnits;
                break;
            default:
                units = currencyUnits;
        }

        // Create adapter for unit spinners
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        // Set dropdown layout style
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach same adapter to both From and To spinners
        From.setAdapter(unitAdapter);
        To.setAdapter(unitAdapter);
    }

    // Main method to choose which conversion type to use
    private double convertValue(String category, String fromUnit, String toUnit, double value) {

        // If source and destination are the same, return original value
        if (fromUnit.equals(toUnit)) {
            return value;
        }

        switch (category) {
            case "Currency":
                return convertCurrency(fromUnit, toUnit, value);

            case "Fuel & Distance":
                return convertFuelDistance(fromUnit, toUnit, value);

            case "Temperature":
                return convertTemperature(fromUnit, toUnit, value);

            default:
                return value;
        }
    }

    // Currency conversion using USD as the base currency
    private double convertCurrency(String from, String to, double amount) {
        double usdValue = 0.0;

        // First convert input currency into USD
        switch (from) {
            case "USD":
                usdValue = amount;
                break;
            case "AUD":
                usdValue = amount / 1.55;
                break;
            case "EUR":
                usdValue = amount / 0.92;
                break;
            case "JPY":
                usdValue = amount / 148.50;
                break;
            case "GBP":
                usdValue = amount / 0.78;
                break;
        }

        // Then convert USD into target currency
        switch (to) {
            case "USD":
                return usdValue;
            case "AUD":
                return usdValue * 1.55;
            case "EUR":
                return usdValue * 0.92;
            case "JPY":
                return usdValue * 148.50;
            case "GBP":
                return usdValue * 0.78;
            default:
                return amount;
        }
    }

    // Fuel, liquid volume, and distance conversion
    private double convertFuelDistance(String from, String to, double value) {

        // Miles per gallon to kilometers per liter
        if (from.equals("mpg") && to.equals("km/L")) {
            return value * 0.425;
        }

        // Kilometers per liter to miles per gallon
        if (from.equals("km/L") && to.equals("mpg")) {
            return value / 0.425;
        }

        // Gallon (US) to liters
        if (from.equals("Gallon (US)") && to.equals("Liters")) {
            return value * 3.785;
        }

        // Liters to gallon (US)
        if (from.equals("Liters") && to.equals("Gallon (US)")) {
            return value / 3.785;
        }

        // Nautical mile to kilometers
        if (from.equals("Nautical Mile") && to.equals("Kilometers")) {
            return value * 1.852;
        }

        // Kilometers to nautical mile
        if (from.equals("Kilometers") && to.equals("Nautical Mile")) {
            return value / 1.852;
        }

        // If conversion pair is not defined, return original value
        return value;
    }

    // Temperature conversion formulas
    private double convertTemperature(String from, String to, double value) {

        // Celsius to Fahrenheit
        if (from.equals("Celsius") && to.equals("Fahrenheit")) {
            return (value * 1.8) + 32;
        }

        // Fahrenheit to Celsius
        if (from.equals("Fahrenheit") && to.equals("Celsius")) {
            return (value - 32) / 1.8;
        }

        // Celsius to Kelvin
        if (from.equals("Celsius") && to.equals("Kelvin")) {
            return value + 273.15;
        }

        // Kelvin to Celsius
        if (from.equals("Kelvin") && to.equals("Celsius")) {
            return value - 273.15;
        }

        // Fahrenheit to Kelvin
        if (from.equals("Fahrenheit") && to.equals("Kelvin")) {
            return ((value - 32) / 1.8) + 273.15;
        }

        // Kelvin to Fahrenheit
        if (from.equals("Kelvin") && to.equals("Fahrenheit")) {
            return ((value - 273.15) * 1.8) + 32;
        }

        // If no matching rule is found, return original value
        return value;
    }
}