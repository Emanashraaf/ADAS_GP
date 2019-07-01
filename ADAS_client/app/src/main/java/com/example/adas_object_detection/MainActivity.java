package com.example.adas_object_detection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import com.github.nkzawa.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    private Socket client;
    {
        try {
            client = IO.socket("http://172.0.0.1/12345");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // establish connection with server
        client.connect();

        // send frame
        String path = null;
        sendImage(path);

        // receive frame
        client.on("recv_image", onNewMessage);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        public void call(final Object... args) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String img = null;
                    try {
                        img = data.getString("imageData");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    byte[] b = Base64.decode(img,Base64.DEFAULT);

                }
            });

            t.start();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        client.disconnect();
    }

    public void sendImage(String path)
    {
        JSONObject data = new JSONObject();
        try{
            data.put("imageData", encodeImage(path));
            client.emit("send_image",data);
        }catch(JSONException e){

        }
    }

    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;
    }

}
