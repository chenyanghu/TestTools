package com.example.testtools.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.testtools.R;
import com.pax.poslink.CommSetting;

import java.util.HashMap;


public class Fragment_CommSetting extends Fragment {

    private Spinner spinner_commType;
    private Button saveButton;
    private TextView textView_SerialPort;
    private TextView textView_BaudRate;
    private TextView textView_DestIP;
    private TextView textView_DestPort;
    private TextView textView_MacAddr;
    private EditText editText_Timeout;
    private EditText editText_SerialPort;
    private EditText editText_BaudRate;
    private EditText editText_DestIP;
    private EditText editText_DestPort;
    private EditText editText_MacAddr;
    private CommSetting commSetting;

    private HashMap<String, Integer> commType;
    private SharedPreferences savedCommSettings;
    private SharedPreferences.Editor editor;

    OnSavedCommSettingListener onSavedCommSettingListener;
    public void setOnSavedCommSettingListener(OnSavedCommSettingListener onSavedCommSettingListener) {
        this.onSavedCommSettingListener = onSavedCommSettingListener;
    }
    // Interface: Transfer commSetting to Host Activity
    public interface OnSavedCommSettingListener {
        void onSavedCommSetting(CommSetting commSetting);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commType = new HashMap<>();
        commType.put("UART", 0);
        commType.put("TCP", 1);
        commType.put("BLUETOOTH", 2);
        commType.put("SSL", 3);
        commType.put("HTTP", 4);
        commType.put("HTTPS", 5);
        commType.put("USB", 6);
        commType.put("AIDL", 7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comm_setting, container, false);

        textView_SerialPort = rootView.findViewById(R.id.serialPort);
        textView_BaudRate = rootView.findViewById(R.id.baudRate);
        textView_DestIP = rootView.findViewById(R.id.destIP);
        textView_DestPort = rootView.findViewById(R.id.destPort);
        textView_MacAddr = rootView.findViewById(R.id.macAddr);
        editText_Timeout = rootView.findViewById(R.id.editText_Timeout);
        editText_SerialPort = rootView.findViewById(R.id.editText_SerialPort);
        editText_BaudRate = rootView.findViewById(R.id.editText_BaudRate);
        editText_DestIP = rootView.findViewById(R.id.editText_DestIP);
        editText_DestPort = rootView.findViewById(R.id.editText_DestPort);
        editText_MacAddr = rootView.findViewById(R.id.editText_MacAddr);
        spinner_commType = rootView.findViewById(R.id.spinner_CommType);

        try{
            savedCommSettings = getActivity().getSharedPreferences("CommSettings", Context.MODE_PRIVATE);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        editor = savedCommSettings.edit();

        if(!savedCommSettings.contains("CommType")){
            editor.putString("CommType", "AIDL");
            editor.putString("TimeOut", "-1");
            editor.putString("DestIP", "");
            editor.putString("DestPort", "");
            editor.putString("MacAddr", "");
            editor.apply();
        }
        initializeCommSetting(savedCommSettings);
        setCommSetting();
        //default selection: AIDL(position 7)
        //spinner_commType.setSelection(7);
        spinner_commType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position 0: UART
                //          1: TCP
                //          2: BLUETOOTH
                //          3: SSL
                //          4: HTTP
                //          5: HTTPS
                //          6: USB
                //          7: AIDL
                hideAll();
                switch (position){
                    case 0:
                        textView_SerialPort.setVisibility(View.VISIBLE);
                        editText_SerialPort.setVisibility(View.VISIBLE);
                        textView_BaudRate.setVisibility(View.VISIBLE);
                        editText_BaudRate.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                    case 3:
                    case 4:
                    case 5:
                        textView_DestIP.setVisibility(View.VISIBLE);
                        editText_DestIP.setVisibility(View.VISIBLE);
                        textView_DestPort.setVisibility(View.VISIBLE);
                        editText_DestPort.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        textView_MacAddr.setVisibility(View.VISIBLE);
                        editText_MacAddr.setVisibility(View.VISIBLE);
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveButton = rootView.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCommSettings();
                setCommSetting();
                onSavedCommSettingListener.onSavedCommSetting(commSetting);
            }
        });
        return rootView;
    }

    private void hideAll(){
        textView_SerialPort.setVisibility(View.INVISIBLE);
        textView_BaudRate.setVisibility(View.INVISIBLE);
        textView_DestIP.setVisibility(View.INVISIBLE);
        textView_DestPort.setVisibility(View.INVISIBLE);
        textView_MacAddr.setVisibility(View.INVISIBLE);
        editText_SerialPort.setVisibility(View.INVISIBLE);
        editText_BaudRate.setVisibility(View.INVISIBLE);
        editText_DestIP.setVisibility(View.INVISIBLE);
        editText_DestPort.setVisibility(View.INVISIBLE);
        editText_MacAddr.setVisibility(View.INVISIBLE);
    }

    private void setCommSetting(){
        commSetting = new CommSetting();
        switch (spinner_commType.getSelectedItem().toString()){
            case "UART":
                commSetting.setType(CommSetting.UART);
                break;
            case "TCP":
                commSetting.setType(CommSetting.TCP);
                break;
            case "BLUETOOTH":
                commSetting.setType(CommSetting.BT);
                break;
            case "SSL":
                commSetting.setType(CommSetting.SSL);
                break;
            case "HTTP":
                commSetting.setType(CommSetting.HTTP);
                break;
            case "HTTPS":
                commSetting.setType(CommSetting.HTTPS);
                break;
            case "USB":
                commSetting.setType(CommSetting.USB);
                break;
            case "AIDL":
                commSetting.setType(CommSetting.AIDL);
                break;
            default:
                break;
        }
        commSetting.TimeOut = editText_Timeout.getText().toString();
        commSetting.SerialPort = editText_SerialPort.getText().toString();
        commSetting.DestIP = editText_DestIP.getText().toString();
        commSetting.DestPort = editText_DestPort.getText().toString();
        commSetting.MacAddr = editText_MacAddr.getText().toString();
    }

    private void initializeCommSetting(SharedPreferences savedCommSettings){
        int test = commType.get(savedCommSettings.getString("CommType", null));
        spinner_commType.setSelection(test);
        editText_Timeout.setText(savedCommSettings.getString("SerialPort", null));
        editText_Timeout.setText(savedCommSettings.getString("TimeOut",null));
        editText_DestIP.setText(savedCommSettings.getString("DestIP", null));
        editText_DestPort.setText(savedCommSettings.getString("DestPort", null));
        editText_MacAddr.setText(savedCommSettings.getString("MacAddr", null));
        setCommSetting();
    }

    private void saveCommSettings(){
        editor.putString("CommType", spinner_commType.getSelectedItem().toString());
        editor.putString("TimeOut", editText_Timeout.getText().toString());
        editor.putString("DestIP", editText_DestIP.getText().toString());
        editor.putString("DestPort", editText_DestPort.getText().toString());
        editor.putString("MacAddr", editText_MacAddr.getText().toString());
        editor.apply();
    }
}
