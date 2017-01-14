package com.example.seydagokdogan.port;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Created by tolgahanuzun on 28/12/2016.
 */

public class gonder extends AppCompatActivity {
    TextView textResponse;
    EditText ders,numara;
    Button buttonConnect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gonder);

        ders = (EditText)findViewById(R.id.derskodu);
        numara = (EditText)findViewById(R.id.ogrnumber);
        buttonConnect = (Button)findViewById(R.id.yoklama);
        textResponse = (TextView)findViewById(R.id.textView2);

        buttonConnect.setOnClickListener(ConnectOnClickListener);

    }

    View.OnClickListener ConnectOnClickListener =
            new View.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask(
                            ders.getText().toString()
                            ,numara.getText().toString());
                    myClientTask.execute();
                }};


    public class MyClientTask extends AsyncTask<Void,Void, Void> {

        String dstAddress = "192.168.155.59";
        int dstPort = 8080;
        String response ="";
        String Dersid,Numaralist;

        MyClientTask(String ders,String numara){
            Dersid = ders;
            Numaralist = numara;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);


                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];


                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                        StandardCharsets.UTF_8)), true);
                pw.println("ders:"+Dersid+"//numara:"+Numaralist+"//");
                pw.flush();

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                System.out.println(response.length());



            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }
}
