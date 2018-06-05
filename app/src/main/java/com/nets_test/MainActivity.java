package com.nets_test;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nets_test.R;
import com.nets.enets.exceptions.InvalidPaymentRequestException;
import com.nets.enets.listener.PaymentCallback;
import com.nets.enets.network.PaymentRequestManager;
import com.nets.enets.utils.result.DebitCreditPaymentResponse;
import com.nets.enets.utils.result.NETSError;
import com.nets.enets.utils.result.NonDebitCreditPaymentResponse;
import com.nets.enets.utils.result.PaymentResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Default set of keys.
    private static final String DEFAULT_KEY = "154eb31c-0f72-45bb-9249-84a1036fd1ca";
    private static final String DEFAULT_SECRET_KEY = "38a4b473-0295-439d-92e1-ad26a8c60279";
    private static final String DEFAULT_UMID = "UMID_877772003";
    private static final String DEFAULT_TXN_VALUE = "1000";

    // Fields on the screen.
    EditText txnValue;
    EditText keyID;
    EditText secretKey;
    EditText UMID;
    EditText payload;

    // Secondary set of keys. Use if needed.
    private static final String KEY2 = "c28e0590-c780-4f9c-9522-428cb0eb1d62";
    private static final String SECRET_KEY2 = "1241dcf0-4b57-4b82-95e3-47f285c5d432";
    private static final String UMID2 = "UMID_877858000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txnValue = findViewById(R.id.TXN_value);
        keyID = findViewById(R.id.KEY_ID_value);
        secretKey = findViewById(R.id.SECRET_KEY_value);
        UMID = findViewById(R.id.UMID_value);
        payload = findViewById(R.id.payload);
    }

    // Grabs the Strings from the textEdit and use it to call startPayment.
    @SuppressLint("SetTextI18n")
    public void genPayload(View view) {
        String value = txnValue.getText().toString();
        String umid = UMID.getText().toString();
        String sKey = secretKey.getText().toString();
        String txnReq = "{\"ss\":\"1\",\"msg\":{\"netsMid\":\""+umid+"\",\"tid\":\"\",\"submissionMode\":\"B\",\"txnAmount\":\""+value+"\",\"merchantTxnRef\":\""+getTime()+"\",\"merchantTxnDtm\":\""+getTime()+".000\",\"paymentType\":\"SALE\",\"currencyCode\":\"SGD\",\"paymentMode\":\"\",\"merchantTimeZone\":\"+8:00\",\"b2sTxnEndURL\":\"https://sit2.enets.sg/MerchantApp/sim/b2sTxnEndURL.jsp\",\"b2sTxnEndURLParam\":\"\",\"s2sTxnEndURL\":\"https://sit2.enets.sg/MerchantApp/rest/s2sTxnEnd\",\"s2sTxnEndURLParam\":\"\",\"clientType\":\"S\",\"supMsg\":\"\",\"netsMidIndicator\":\"U\",\"ipAddress\":\"127.0.0.1\",\"language\":\"en\",\"mobileOs\":\"ANDROID\"}}";
        payload.setText(txnReq);
        payload.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showHMAC(s.toString(), secretKey.getText().toString());
            }
        });
        showHMAC(txnReq, sKey);
    }

    /**
     * Generate HMAC and make call.
     * @param txnReq payload
     * @param sKey secret key
     * @param key key ID
     */
    private void startPayment(String txnReq, String sKey, String key) {

        // Generate HMAC
        String hmac = HMAC_Gen.generateSignature(txnReq, sKey);

        Log.d("startPayment", "txn: " + txnReq);
        Log.d("startPayment", "DEFAULT_SECRET_KEY: " + sKey);
        Log.d("startPayment", "hmac: " + hmac);

        PaymentRequestManager manager = PaymentRequestManager.getSharedInstance();
        try {
            manager.sendPaymentRequest(key, hmac, txnReq, new PaymentCallback() {
                @Override
                public void onResult(PaymentResponse paymentResponse) {
                    // To implement callback functions
                    if (paymentResponse instanceof DebitCreditPaymentResponse) {
                        final DebitCreditPaymentResponse debitCreditPaymentResponse = (DebitCreditPaymentResponse) paymentResponse;
                        String txnRes = debitCreditPaymentResponse.txnResp;
                        String hmac = debitCreditPaymentResponse.hmac;
                        String keyId = debitCreditPaymentResponse.keyId;


                        Log.d("DebitCreditPaymentResponse", "txnRes: " + txnRes);
                        Log.d("DebitCreditPaymentResponse", "hmac: " + hmac);
                        Log.d("DebitCreditPaymentResponse", "keyId: " + keyId);

                        // Next 4 lines show a simplified verification.
                        // Basically checking if the hmac returned tallies with a hmac generated by our secret key
                        String hmacVerification = HMAC_Gen.generateSignature(txnRes, keyId);
                        Log.d("DebitCreditPaymentResponse", "hmacVerification: " + hmacVerification);
                        if (hmacVerification.equals(hmac)){
                            Log.d("DebitCreditPaymentResponse", "Verification Successful");
                        }

                        try {
                            JSONObject txnJSON = new JSONObject(txnRes);
                            JSONObject msg = txnJSON.getJSONObject("msg");
                            String stageRespCode = msg.getString("stageRespCode");
                            Toast.makeText(MainActivity.this, "Payment Success\nstageRespCode: " + stageRespCode, Toast.LENGTH_LONG).show();
                            Log.d("DebitCreditPaymentResponse", "stageRespCode: " + stageRespCode);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    // NETSPay payment will result in this callback.
                    } else if (paymentResponse instanceof NonDebitCreditPaymentResponse) {
                        final NonDebitCreditPaymentResponse nonDebitCreditPaymentResponse = (NonDebitCreditPaymentResponse) paymentResponse;

                        String txn_Status = nonDebitCreditPaymentResponse.status;
                        Log.d("nonDebitCreditPaymentResponse", "txn_Status: " + txn_Status);
                        Toast.makeText(MainActivity.this, "nonDebitCreditPaymentResponse Payment Success", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(NETSError netsError) {
                    String txn_ResponseCode = netsError.responeCode;
                    String txn_ActionCode = netsError.actionCode;

                    Log.d("netsError", "txn_ResponseCode: " + txn_ResponseCode);
                    Log.d("netsError", "txn_ActionCode: " + txn_ActionCode);

                }
            }, this);
        } catch (InvalidPaymentRequestException e) {
            e.printStackTrace();
            Log.e("InvalidPaymentException", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
        }

    }

    // To create date&time string in proper format.
    public static String getTime(){
        Date date = new Date();
        String formattedTime = String.format("%d%02d%02d %02d:%02d:%02d", date.getYear()+1900, date.getMonth()+1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        Log.d("Time", formattedTime);
        return formattedTime;
    }

    // To populate default values
    public void defaultVal(View view) {
        txnValue.setText(DEFAULT_TXN_VALUE);
        keyID.setText(DEFAULT_KEY);
        secretKey.setText(DEFAULT_SECRET_KEY);
        UMID.setText(DEFAULT_UMID);

    }

    /**
     * Changes the HMAC according to the edited value of the payload.
     * @param txn txnReq string
     * @param secretKey secretKey
     */
    public void showHMAC(String txn, String secretKey){
        TextView hmacView = findViewById(R.id.hmac);
        hmacView.setText("hmac:\n"+HMAC_Gen.generateSignature(txn, secretKey));
    }

    /**
     *
     * @param view Send Button
     */
    public void startPayment(View view) {
        startPayment(payload.getText().toString(), secretKey.getText().toString(), keyID.getText().toString());
    }
}
