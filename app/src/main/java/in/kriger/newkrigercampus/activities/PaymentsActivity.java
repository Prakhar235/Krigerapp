package in.kriger.newkrigercampus.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import java.io.IOException;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.CheckSum;
import in.kriger.newkrigercampus.classes.Transaction;
import in.kriger.newkrigercampus.extras.Processor;
import in.kriger.newkrigercampus.services.KrigerConstants;
import in.kriger.newkrigercampus.services.FireService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentsActivity extends AppCompatActivity {


    TextView text_coins;
    Button btn_buy1, btn_buy2,btn_havingTrouble,btn_havingTrouble1;
    Dialog progress;
    String contact = "9999999999";

    final String mid = "NRwPWo83766787671559";
    String order_id = null;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private FirebaseRecyclerAdapter<Transaction, transactionViewHolder> adapter_payments;


    private static final String BASE_URL = "http://134.209.157.104:3000/";

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    RecyclerView recyclerView;
    int recharge_type = 0;

    Button btn_validity;
    TextView no_payments;


    private FirebaseRecyclerAdapter<Long, validityViewHolder> adapter_validity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Wallet");

        try {
            if (getIntent().getExtras().getString("recharge").equals("new")) {
                recharge_type = 1;
                startbuying();
            }
        } catch (NullPointerException e) {

        }


        text_coins = findViewById(R.id.text_coins);
        btn_buy1 = findViewById(R.id.btn_buy1);
        btn_buy2 = findViewById(R.id.btn_buy2);
        btn_havingTrouble = findViewById(R.id.button_login_trouble);
        btn_havingTrouble1 = findViewById(R.id.button_login_trouble1);

        KrigerConstants.transaction_detailRef.child(user.getUid()).child("value").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    text_coins.setText(String.valueOf(Integer.valueOf(dataSnapshot.getValue().toString()) * 2000));
                } else {
                    text_coins.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_buy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recharge_type = 1;
                startbuying();
            }
        });

        btn_buy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recharge_type = 0;
                startbuying();

            }
        });

        btn_havingTrouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               havingTrouble();
            }
        });
      //Having trouble in payments
        btn_havingTrouble1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                havingTrouble();
            }
        });

        btn_validity = findViewById(R.id.btn_validity);


        no_payments = findViewById(R.id.no_payment);
        recyclerView =  findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());


        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PaymentsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                //set layout custom
                dialog.setContentView(R.layout.dialog_recyclerview);
                final RecyclerView recyclerView_validity =  dialog.findViewById(R.id.recycler_view);

                final TextView textView_validity = dialog.findViewById(R.id.text_validity);

                LinearLayoutManager mLayoutManager1 =
                        new LinearLayoutManager(getApplicationContext());


                mLayoutManager1.setReverseLayout(true);
                mLayoutManager1.setStackFromEnd(true);
                recyclerView_validity.setLayoutManager(mLayoutManager1);
                recyclerView_validity.setItemAnimator(new DefaultItemAnimator());

                final Query validityRef = KrigerConstants.transaction_detailRef.child(user.getUid()).child("list");

                validityRef.keepSynced(true);

                FirebaseRecyclerOptions<Long> options =
                        new FirebaseRecyclerOptions.Builder<Long>()
                                .setQuery(validityRef, Long.class)
                                .setLifecycleOwner(PaymentsActivity.this)
                                .build();

                adapter_validity = new FirebaseRecyclerAdapter<Long, validityViewHolder>(options) {
                    @Override
                    public validityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        return new validityViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.layout_validity, parent, false));
                    }

                    @Override
                    public void onDataChanged() {
                        textView_validity.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull validityViewHolder validityViewHolder, int i, @NonNull Long aLong) {
                        if (aLong == 1) {
                            validityViewHolder.type.setText("Type : Add 5 New Listing");
                        } else {
                            validityViewHolder.type.setText("Type : Recharge 5 Old Listing");
                        }
                        validityViewHolder.purchased.setText("Purchased on : " + Processor.timestamp(FireService.diff28days(adapter_validity.getRef(i).getKey())));
                        validityViewHolder.expired.setText("Expiring on : " + Processor.timestamp(adapter_validity.getRef(i).getKey()));
                    }


                };

                recyclerView_validity.setAdapter(adapter_validity);
                dialog.show();


            }
        });


        final Query transactionRef = KrigerConstants.transaction_historyref.child(user.getUid());

        transactionRef.keepSynced(true);

        FirebaseRecyclerOptions<Transaction> options =
                new FirebaseRecyclerOptions.Builder<Transaction>()
                        .setQuery(transactionRef, Transaction.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter_payments = new FirebaseRecyclerAdapter<Transaction, transactionViewHolder>(options) {
            @Override
            public transactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new transactionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_transaction, parent, false));

            }


            @Override
            public void onDataChanged() {
                    no_payments.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);

            }

            @Override
            protected void onBindViewHolder(@NonNull transactionViewHolder transactionViewHolder, int i, @NonNull Transaction transaction) {

                transactionViewHolder.order_id.setText(adapter_payments.getRef(i).getKey());
                transactionViewHolder.bank_txnid.setText(transaction.getBank_txnid());
                transactionViewHolder.timestamp.setText(Processor.paymentTimestamp(transaction.getTimestamp()).substring(0, 6) + " " + transaction.getTimestamp().substring(0, 4));
                try {
                    transactionViewHolder.time.setText(Processor.timestamp(transaction.getTimestamp()).substring(10, 18));
                } catch (IndexOutOfBoundsException e) {
                    transactionViewHolder.time.setText(Processor.timestamp(transaction.getTimestamp()).substring(10, 17));
                }

                transactionViewHolder.txnid.setText(transaction.getTransaction_id());

            }


        };

        recyclerView.setAdapter(adapter_payments);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startpayment();

                } else {
                    Toasty.custom(getApplicationContext(), "Can't proceed further until permission granted", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void startbuying() {
        startpayment();

    }


    private void startpayment() {

        progress = new Dialog(PaymentsActivity.this);
        progress.setContentView(R.layout.dialog_progress);
        progress.show();

        KrigerConstants.userRef.child(user.getUid()).child("contact").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    contact = dataSnapshot.getValue().toString();
                }

                order_id = KrigerConstants.transaction_historyref.push().getKey();
                CheckSum checkSum = retrofit.create(CheckSum.class);

                Call<ResponseBody> call = checkSum.sendUserDetail(user.getUid(), order_id, user.getEmail(), contact);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {

                            PaytmPGService Service = PaytmPGService.getProductionService();
                            HashMap<String, String> paramMap = new HashMap<String, String>();
                            paramMap.put("MID", mid);
                            paramMap.put("ORDER_ID", order_id);
                            paramMap.put("CUST_ID", user.getUid());
                            paramMap.put("MOBILE_NO", contact);
                            paramMap.put("EMAIL", user.getEmail());
                            paramMap.put("CHANNEL_ID", "WAP");
                            paramMap.put("TXN_AMOUNT", "2000.00");
                            paramMap.put("WEBSITE", "DEFAULT");
                            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                            paramMap.put("CALLBACK_URL", "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + order_id);
                            paramMap.put("CHECKSUMHASH", response.body().string().replace("\"", ""));
                            PaytmOrder Order = new PaytmOrder(paramMap);

                            Service.initialize(Order, null);

                            startTransaction(Service);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Toasty.custom(getApplicationContext(), t.getMessage(), R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                true).show();
                        progress.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void startTransaction(PaytmPGService service) {
        service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle inResponse) {

                if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("timestamp", FireService.getToday());
                    map.put("transaction_id", inResponse.getString("TXNID"));
                    map.put("bank_txnid", inResponse.getString("BANKTXNID"));
                    KrigerConstants.transaction_historyref.child(user.getUid()).child(order_id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            KrigerConstants.transaction_detailRef.child(user.getUid()).child("list").child(FireService.getValidity()).setValue(recharge_type).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progress.dismiss();
                                    Toasty.custom(getApplicationContext(), "Transaction Successful", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                            true).show();
                                }
                            });
                        }
                    });
                } else {
                    progress.dismiss();
                    Toasty.custom(getApplicationContext(), "Transaction Failed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progress.dismiss();
                Toasty.custom(getApplicationContext(), "Network connection error: Check your internet connectivity", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();

            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                progress.dismiss();
                Toasty.custom(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();
            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                progress.dismiss();

                Toasty.custom(getApplicationContext(), "UI Error" + inErrorMessage, R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();


            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                progress.dismiss();
                Toasty.custom(getApplicationContext(), "Unable to load webpage" + inErrorMessage, R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();

            }

            @Override
            public void onBackPressedCancelTransaction() {
                progress.dismiss();
                Toasty.custom(getApplicationContext(), "Transaction cancelled", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();


            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                progress.dismiss();
                Toasty.custom(getApplicationContext(), "Transaction cancelled", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                        true).show();

            }
        });

    }


    public static class transactionViewHolder extends RecyclerView.ViewHolder {

        private TextView txnid, bank_txnid, timestamp, time, order_id;

        public transactionViewHolder(View itemView) {
            super(itemView);

            txnid = itemView.findViewById(R.id.txn_id);
            bank_txnid = itemView.findViewById(R.id.bank_txnid);
            timestamp = itemView.findViewById(R.id.timestamp);
            time = itemView.findViewById(R.id.time);
            order_id = itemView.findViewById(R.id.order_id);

        }


    }


    private void havingTrouble(){
        final Dialog trouble = new Dialog(PaymentsActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        // Set GUI of login screen
        trouble.setContentView(R.layout.dialog_havingtrouble);

        // Init button of login GUI
        Button email =  trouble.findViewById(R.id.btn_trouble_email);
        Button whatsapp =  trouble.findViewById(R.id.btn_trouble_whatsapp);
        Button verification = trouble.findViewById(R.id.btn_trouble_verification);
        verification.setVisibility(View.GONE);
        whatsapp.setVisibility(View.GONE);
        //Mail to payments@kriger.in
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"payments@kriger.in"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Issue");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                try {
                    startActivity(Intent.createChooser(intent, "Send mail"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toasty.custom(getApplicationContext(),"There are no email clients installed", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                            true).show();
                }

            }
        });


        trouble.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        trouble.show();

    }



    @Override
    public void onBackPressed() {
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class validityViewHolder extends RecyclerView.ViewHolder {

        TextView type, expired, purchased;

        public validityViewHolder(View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.text_type);
            expired = itemView.findViewById(R.id.text_expired);
            purchased = itemView.findViewById(R.id.text_purchased);

        }
    }


}
