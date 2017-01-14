package com.example.seydagokdogan.port;

        import android.content.Intent;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.TextView;

        import com.google.android.gms.appindexing.Action;
        import com.google.android.gms.appindexing.AppIndex;
        import com.google.android.gms.appindexing.Thing;
        import com.google.android.gms.common.api.GoogleApiClient;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.ByteArrayOutputStream;
        import java.io.FileNotFoundException;
        import java.io.FileReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStreamWriter;
        import java.io.PrintWriter;
        import java.net.Socket;
        import java.net.UnknownHostException;
        import java.nio.charset.StandardCharsets;
        import java.util.ArrayList;


public class aktarim extends AppCompatActivity {
    Button btnRead, gonder;
    TextView textResult, textsonuc;

    ListView listViewNode;
    ArrayList<Node> listNote;
    ArrayAdapter<Node> adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktarim);
        btnRead = (Button) findViewById(R.id.readclient);
        gonder = (Button) findViewById(R.id.button);
        textResult = (TextView) findViewById(R.id.result);
        textsonuc = (TextView) findViewById(R.id.sonuc);

        gonder.setOnClickListener(buttonConnect);


        listViewNode = (ListView) findViewById(R.id.nodelist);

        listNote = new ArrayList<>();
        adapter =
                new ArrayAdapter<Node>(
                        aktarim.this,
                        android.R.layout.simple_list_item_1,
                        listNote);
        listViewNode.setAdapter(adapter);


        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TaskReadAddresses(listNote, listViewNode).execute();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    View.OnClickListener buttonConnect =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask(
                            listNote
                    );
                    myClientTask.execute();
                }
            };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("aktarim Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    class Node {
        String ip;
        String mac;

        Node(String ip, String mac) {
            this.ip = ip;
            this.mac = mac;
            System.out.println(mac);
        }

        @Override
        public String toString() {

            return "IP: " + ip + "\n" + "MAC: " + mac;

        }
    }

    private class TaskReadAddresses extends AsyncTask<Void, Node, Void> {

        ArrayList<Node> array;
        ListView listView;

        TaskReadAddresses(ArrayList<Node> array, ListView v) {
            listView = v;
            this.array = array;
            array.clear();
            textResult.setText("araniyor...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            readAddresses();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            textResult.setText("Bitti");
        }

        @Override
        protected void onProgressUpdate(Node... values) {
            listNote.add(values[0]);
            ((ArrayAdapter) (listView.getAdapter())).notifyDataSetChanged();

        }

        private void readAddresses() {

            BufferedReader bufferedReader = null;

            try {
                bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] splitted = line.split(" +");
                    if (splitted != null && splitted.length >= 4) {
                        String ip = splitted[0];
                        String mac = splitted[3];
                        if (mac.matches("..:..:..:..:..:..")) {
                            Node thisNode = new Node(ip, mac);
                            publishProgress(thisNode);
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress = "192.168.155.59";
        int dstPort = 8080;
        String response = "";
        ArrayList<Node> listNote;

        MyClientTask(ArrayList data) {
            listNote = data;
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
                pw.println(listNote);
                pw.flush();

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                System.out.println(response.length());

                if (response.length() == 3) {
                    System.out.println("oldu");
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), gonder.class);
                    intent.putExtra("data", (String) "sayfa 1'den veri gönderildi");
                    startActivity(intent);
                }


            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
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
            textsonuc.setText(response);
            super.onPostExecute(result);
        }

    }

}
