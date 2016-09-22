package edu.nthu.mao.testhttp;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.LogRecord;
import edu.nthu.mao.testhttp.settings.Settings;

/**
 * Created by Mao on 2016/9/21.
 */
public class DatabaseHandler {
    private static final String TAG = "DBHandler";
    private Context application;
    private static final int statePartId = 0;
    private static final int stateWorkId = 1;
    private static final int stateMeasId = 2;
    private static final int sendData = 3;

    public DatabaseHandler (Context context){
        this.application = context;
    }


    public void PartId(){
        String mURL = Settings.serverURL + "appGetPartData.php";
        URL url;
        try {
            url = new URL(mURL);
            Thread getThread;
            getThread = new Thread(new httpGet(url,statePartId));
            getThread.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG,"URL error");
        }


    }

    /*public ArrayList<String> getPartIds(){
        ArrayList<String> partIdArray = new ArrayList<String>();


    }*/

    public class httpGet implements Runnable {
        private URL url;
        private int state;

        public httpGet(URL url, int s){
            this.state = s;
            this.url = url;
        }

        @Override
        public void run() {
            HttpURLConnection urlConnection;
            String jsonData = null;
            try {
                urlConnection = (HttpURLConnection) this.url.openConnection();
                urlConnection.setRequestMethod("GET");

                if( urlConnection.getResponseCode() == 200){

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String seg = null;

                    while( (seg = reader.readLine()) != null ){
                        builder.append(seg);
                    }

                    reader.close();

                    String res = builder.toString();
                    Message message = ResponseHandler.obtainMessage(this.state,res);
                    message.sendToTarget();

                } else {
                    Log.d(TAG, " Http connection error with code:" + urlConnection.getResponseCode());
                }


                urlConnection.disconnect();
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private Handler ResponseHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case stateWorkId:
                    Log.d(TAG,"Receive:"+msg.obj.toString());
                    break;
                case statePartId:
                    Log.d(TAG,"Receive:"+msg.obj.toString());
                    break;
                case stateMeasId:
                    Log.d(TAG,"Receive:"+msg.obj.toString());
                    break;
                default:
                    Log.d(TAG,"Error");
                    break;
            }
        }
    };


}
