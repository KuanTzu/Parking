package com.example.parking;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ToWeb {

    private String strurl="https://iot.comm.yzu.edu.tw:8800/ParkingStatus";
    private String strurltext="https://iot.comm.yzu.edu.tw:8800/Image";
    Handler mHand=new Handler(new HDRcb());
    HttpURLConnection con;
    String ans;
    public ToWeb(String ans){
        this.ans = ans;
    }

    class HDRcb implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
//            if(msg!=null) {
//                Log.d("Nora_Image:",msg.toString());
//                Picasso.get().load("https://iot.comm.yzu.edu.tw:3333/" + msg.obj).into(imageView);
//            }
            return true;
        }
    }
    //
    class TRClass implements Runnable {
        String url;
        String urltext;
        // Constructor ...
        public TRClass(String url, String urltext) {
            this.url = url;
            this.urltext=urltext;
        }
        @Override
        public void run() {
            String res=sentPostDataToInternet(url);
            String imagefile = getImageOnInternet(urltext);
//            mHand.obtainMessage(1,res)
//                    .sendToTarget();
            mHand.obtainMessage(1,imagefile)
                    .sendToTarget();

        };
    }
    public void run(){
        Thread t1 = new Thread(new TRClass(strurl,strurltext));
        t1.start();
    }
    private String sentPostDataToInternet(String strTxt){
        final String USER_AGENT="Mozilla/5.0";
        StringBuffer response;
        try {
            Log.d("Nora","sentPostDataToInternet");
            URL obj = new URL(strTxt);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());

            String urlParameters = "status="+ans;
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            Log.w("Nora", String.valueOf(responseCode));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Log.d("Nora_input:",in.readLine());
                response.append(inputLine);
            }
            in.close();
            Log.w("Nora", response.toString());
            return response.toString();
        }catch(IOException e){
            Log.w("Nora",e.getMessage().toString());
            return null;
        }
        catch(Exception e){
            Log.w("Nora",e.getMessage().toString());
            return null;
        }
        finally{
            con.disconnect();
        }
    };
    private String getImageOnInternet(String urltext){
        final String USER_AGENT="Mozilla/5.0";
        URL url;
        StringBuffer response = new StringBuffer();
        try {
            url = new URL(urltext);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url");
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

            //Here is your json in string format
            String responseJSON = response.toString();
            return responseJSON;
        }

    };
}
