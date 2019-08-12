package com.example.testtools.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testtools.CardInformation;
import com.example.testtools.Parameters;
import com.example.testtools.R;
import com.example.testtools.Utils.FileOperations;

import com.pax.poslink.CommSetting;
import com.pax.poslink.POSLinkAndroid;
import com.pax.poslink.PaymentRequest;
import com.pax.poslink.PaymentResponse;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.poslink.POSLinkCreator;

import java.util.Random;

public class Stress_Test extends Fragment {

    Button start;
    EditText editText;
    TextView textView;
    PosLink posLink;
    StringBuilder sb;
    CardInformation[] cardInformation;
    FileOperations fileOperations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stress__test, container, false);

        cardInformation = new CardInformation[Parameters.getInstance().numOfCard];
        fillCardInformation();
        start = rootView.findViewById(R.id.startButton);
        editText = rootView.findViewById(R.id.editText);
        textView = rootView.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    textView.setText("");
                    final int times = Integer.parseInt(editText.getText().toString());
                    Log.d("Times", String.valueOf(times));
                    //POSLink processTrans() has to run in a background thread
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 0; i < times; i++){
                                boolean isRunning = doPayment(i + 1).equals("000000");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.append(sb.toString());
                                    }
                                });
                                fileOperations = new FileOperations("ResultLogs.txt", sb, getContext());
                                fileOperations.saveFile();
                                if(!isRunning){
                                    break;
                                }
                            }
                        }
                    });

                    thread.start();
                } catch(NumberFormatException e){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Please input valid #", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        return rootView;
    }

    private void fillCardInformation(){
        cardInformation[0] = new CardInformation("6011000990156527", "1225", "350");
        cardInformation[1] = new CardInformation("4264281511118886", "1225", "");
        cardInformation[2] = new CardInformation("4005578003333335", "0420", "");
    }
    // Setup Payment Configuration
    private String doPayment(int i){
        PaymentRequest paymentRequest = new PaymentRequest();

        Random random = new Random();
        int amount = random.nextInt(1000) + 500;
        int cardIndex = random.nextInt(Parameters.getInstance().numOfCard);

        paymentRequest.TenderType = paymentRequest.ParseTenderType("CREDIT");
        paymentRequest.TransType = paymentRequest.ParseTransType("SALE");
        paymentRequest.Zip = "32224";
        paymentRequest.Amount = String.valueOf(amount);
        paymentRequest.Street = "ABCD";
        paymentRequest.ECRRefNum = "1";
        paymentRequest.ExtData = String.format("<Account>%s</Account><ExpDate>%s</ExpDate><CPMode>1</CPMode>",cardInformation[cardIndex].cardNo, cardInformation[cardIndex].expDate);
        Log.d("Using Card: ", String.valueOf(cardIndex));

        CommSetting comm_setting = buildConnection();
        POSLinkAndroid.init(getActivity().getApplicationContext(), comm_setting);
        posLink = new POSLinkCreator().createPoslink(getActivity().getApplicationContext());
        posLink.PaymentRequest = paymentRequest;
        posLink.SetCommSetting(comm_setting);

        ProcessTransResult processTransResult = posLink.ProcessTrans();
        PaymentResponse paymentResponse = posLink.PaymentResponse;

        if(paymentResponse == null){
            sb.append("No transaction is running");
            return "";
        }
        sb = new StringBuilder();
        String res = String.format("Result:\n  Transaction number: %d\n  ProcessTransResult Code: %s\n  ProcessTransResult Msg: %s\n  Result Code: %s\n  Result Text: %s\n  HostResCode: %s\n  HostResMsg: %s\n  HostResNum: %s\n  Message: %s\n  Approved Amount: %s\n", i, processTransResult.Code, processTransResult.Msg, paymentResponse.ResultCode, paymentResponse.ResultTxt, paymentResponse.HostCode, paymentResponse.HostResponse, paymentResponse.AuthCode, paymentResponse.Message, paymentResponse.ApprovedAmount);
        Log.d("PaymentResult: ",res);
        sb.append(res);
        return String.valueOf(paymentResponse.ResultCode);
    }

    private CommSetting buildConnection(){
        CommSetting commSetting = new CommSetting();
        commSetting.setType(CommSetting.AIDL);
        commSetting.setTimeOut("-1");
        return commSetting;
    }
}
