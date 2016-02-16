package com.christofan.oneuptest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.christofan.oneuptest.dialog.DatePickerDialogFragment;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Date cardExpiration;
    private String submitText;

    private EditText editName;
    private EditText editCardNumber;
    private EditText editCardCVV;
    private Button buttonCardExpiration;
    private EditText editAddress1;
    private EditText editAddress2;
    private EditText editCity;
    private EditText editState;
    private EditText editZipCode;
    private EditText editCountry;
    private Button buttonSubmit;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        double price = getIntent().getDoubleExtra("product_price", 0);

        editName = (EditText)findViewById(R.id.EditPaymentName);
        editCardNumber = (EditText)findViewById(R.id.EditPaymentCardNumber);
        editCardCVV = (EditText)findViewById(R.id.EditPaymentCardCVV);
        buttonCardExpiration = (Button)findViewById(R.id.ButtonPaymentCardExpiration);
        editAddress1 = (EditText)findViewById(R.id.EditPaymentAddressLine1);
        editAddress2 = (EditText)findViewById(R.id.EditPaymentAddressLine2);
        editCity = (EditText)findViewById(R.id.EditPaymentCity);
        editState = (EditText)findViewById(R.id.EditPaymentState);
        editZipCode = (EditText)findViewById(R.id.EditPaymentZipCode);
        editCountry = (EditText)findViewById(R.id.EditPaymentCountry);
        buttonSubmit = (Button)findViewById(R.id.ButtonPaymentSubmit);

        submitText = getString(R.string.payment_submit) + " " + String.format(Locale.GERMAN, "%,.0f", price);
        buttonCardExpiration.setOnClickListener(this);
        buttonSubmit.setText(submitText);
        buttonSubmit.setOnClickListener(this);

        pref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        loadDetail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ButtonPaymentCardExpiration:
                DatePickerDialogFragment f = new DatePickerDialogFragment();
                f.show(getSupportFragmentManager(), "DATE_PICKER");
                return;
            case R.id.ButtonPaymentSubmit:
                submitPayment();
                return;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        cardExpiration = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        String cardExpirationText = (new SimpleDateFormat("MM / yyyy")).format(cardExpiration);
        buttonCardExpiration.setText(cardExpirationText);
    }

    public void submitPayment() {
        String cardNumber = editCardNumber.getText().toString();
        int cardExpMonth = 0;
        int cardExpYear = 0;
        if (cardExpiration != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(cardExpiration);
            cardExpMonth = c.get(Calendar.MONTH);
            cardExpYear = c.get(Calendar.YEAR);
        } else {
            Toast.makeText(this, "Please pick card expiration date.", Toast.LENGTH_SHORT).show();
            return;
        }
        String cardCVV = editCardCVV.getText().toString();

        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVV);
        if (card.validateCard()) {
            buttonSubmit.setText("Processing...");
            new Stripe().createToken(card, "pk_test_f4XMxB4adZctlxeMUPbmQDAc", new TokenCallback() {
                @Override
                public void onError(Exception error) {
                    buttonSubmit.setText(submitText);
                    Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Token token) {
                    //TODO dialog sukses
                    saveDetail();
                    Toast.makeText(PaymentActivity.this, "Payment success", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        } else if (!card.validateNumber()) {
            Toast.makeText(this, "The card number that you entered is invalid",Toast.LENGTH_SHORT).show();
        } else if (!card.validateExpiryDate()) {
            Toast.makeText(this, "The expiration date that you entered is invalid",Toast.LENGTH_SHORT).show();
        } else if (!card.validateCVC()) {
            Toast.makeText(this, "The CVC code that you entered is invalid",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "The card details that you entered are invalid",Toast.LENGTH_SHORT).show();
        }
    }

    public void saveDetail() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(getString(R.string.pref_payment_name), editName.getText().toString());
        editor.putString(getString(R.string.pref_payment_card_number), editCardNumber.getText().toString());
        editor.putString(getString(R.string.pref_payment_card_cvv), editCardCVV.getText().toString());
        editor.putLong(getString(R.string.pref_payment_card_expiration), cardExpiration.getTime());
        editor.putString(getString(R.string.pref_payment_address_line_1), editAddress1.getText().toString());
        editor.putString(getString(R.string.pref_payment_address_line_2), editAddress2.getText().toString());
        editor.putString(getString(R.string.pref_payment_city), editCity.getText().toString());
        editor.putString(getString(R.string.pref_payment_state), editState.getText().toString());
        editor.putString(getString(R.string.pref_payment_zip_code), editZipCode.getText().toString());
        editor.putString(getString(R.string.pref_payment_country), editCountry.getText().toString());
        editor.commit();
    }

    public void loadDetail() {
        String name = pref.getString(getString(R.string.pref_payment_name), "");
        if (!name.equals("")) {
            editName.setText(name);
            editCardNumber.setText(pref.getString(getString(R.string.pref_payment_card_number), ""));
            editCardCVV.setText(pref.getString(getString(R.string.pref_payment_card_cvv), ""));
            long expiration = pref.getLong(getString(R.string.pref_payment_card_expiration), 0);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(expiration);
            cardExpiration = c.getTime();
            String cardExpirationText = (new SimpleDateFormat("MM / yyyy")).format(cardExpiration);
            buttonCardExpiration.setText(cardExpirationText);
            editAddress1.setText(pref.getString(getString(R.string.pref_payment_address_line_1), ""));
            editAddress2.setText(pref.getString(getString(R.string.pref_payment_address_line_2), ""));
            editCity.setText(pref.getString(getString(R.string.pref_payment_city), ""));
            editState.setText(pref.getString(getString(R.string.pref_payment_state), ""));
            editZipCode.setText(pref.getString(getString(R.string.pref_payment_zip_code), ""));
            editCountry.setText(pref.getString(getString(R.string.pref_payment_country), ""));
        }
    }
}
