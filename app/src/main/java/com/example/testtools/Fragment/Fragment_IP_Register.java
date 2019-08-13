package com.example.testtools.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.testtools.Activities.Activity_InputSN;
import com.example.testtools.R;
import com.example.testtools.Utils.FileOperations;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fragment_IP_Register extends Fragment {

    private static final int SEARCH_CANCELLED = 0;
    private static final int SEARCH_SUCCESSFUL = 1;
    private static final int GET_SN_REQUEST = 1;
    private Button button;
    private TextView textView;
    private StringBuilder sb;
    private FileOperations fileOperations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ip__register, container, false);
        button = rootView.findViewById(R.id.button_Search);
        textView = rootView.findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_InputSN.class);
                startActivityForResult(intent, GET_SN_REQUEST);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_SN_REQUEST)
            switch (resultCode) {
                case SEARCH_CANCELLED:
                    String str1 = data.getStringExtra("cancelMessage");
                    textView.setText(str1);
                    break;

                case SEARCH_SUCCESSFUL:
                    sb = new StringBuilder();
                    String SN = data.getStringExtra("sendMessage");
                    parseXML(SN);
                    textView.setText(sb.toString());

                    //Save result to file
                    fileOperations = new FileOperations("result.txt", sb, getContext());
                    fileOperations.saveFile();
                default:
                    break;
            }
    }

    private void parseXML(String SN) {
        final String SerialNumber = SN;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XmlPullParserFactory factory;
                try {
                    String response = sendGet("http://poslink.com/poslink/ws/process2.asmx/GetDeviceLocalIP?TerminalId=8&Token=&SerialNo=" + SerialNumber);
                    factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser responseParser = factory.newPullParser();
                    responseParser.setInput(new StringReader(response));
                    int eventType = responseParser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                if(responseParser.getName().equals("Result"))
                                    sb.append(responseParser.getName() + ": ");
                                else
                                    sb.append("\n" + responseParser.getName() + ": ");
                                Log.d("MainActivity.java", responseParser.getName());
                                break;
                            case XmlPullParser.TEXT:
                                if(responseParser.getText().startsWith(" "))
                                    break;
                                sb.append(responseParser.getText());
                                Log.d("MainActivity.java", responseParser.getText());
                                break;
                            default:
                                break;
                        }
                        eventType = responseParser.next();
                    }
                    sb.append("\n\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            thread.start();
            thread.join();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { // connection ok
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            return "error";
        }
    }

}
