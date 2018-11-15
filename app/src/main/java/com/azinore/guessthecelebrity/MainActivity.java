package com.azinore.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button but1;
    Button but2;
    Button but3;
    Button but4;
    int corLoc;
    ImageView celeb;
    ArrayList<String> dad = new ArrayList<String>();
    ArrayList<String> mum = new ArrayList<String>();
    String result = "";
    int x = 0;
    String[] four = new String[4];
    Random random = new Random();

    public void celebClick(View view){

        //System.out.println("Tag: "+view.getTag().toString());
        //System.out.println("Cor: "+corLoc);

        if(view.getTag().toString().equals(Integer.toString(corLoc))) {

            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();

        }

        imageGen();


    }
    public void imageGen(){
        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;

        x =  random.nextInt(100 );

        try {
            myImage = task.execute(dad.get(x)).get();
            celeb.setImageBitmap(myImage);
            namePlaces(x);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findUrls(){

        Pattern p = Pattern.compile("img src=\"(.*?)\"");
        Matcher m = p.matcher(result);
        while(m.find()){
            String temp = m.group(1);
            //System.out.println(m.group(1));
            if((temp.contains("images"))) {
                dad.add(temp);

                //System.out.println(temp);
            }
        }

        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(result);
        while(m.find()){
            mum.add(m.group(1));
            //System.out.println(m.group(1));
        }



    }
    public void namePlaces(int cor){
        int g;
        corLoc = random.nextInt(4);
        for(int i = 0;i<4;i++){
            if(corLoc==i){
                four[i]=mum.get(cor);
            }
            else{
                g = (random.nextInt(100));

                while(g==cor){
                    g = (random.nextInt(100));
                }

                four[i]=mum.get(g);
            }

            but1.setText(four[0]);
            but2.setText(four[1]);
            but3.setText(four[2]);
            but4.setText(four[3]);



        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownLoadTask fullString = new DownLoadTask();
        celeb =  findViewById(R.id.celebPic);
        but1 = findViewById(R.id.button1);
        but2 = findViewById(R.id.button2);
        but3 = findViewById(R.id.button3);
        but4 = findViewById(R.id.button4);


        try {
            result = fullString.execute("http://www.posh24.se/kandisar").get(); //WEBPAGE
        }catch(Exception e){
            e.printStackTrace();
        }


        Log.i("Result", result);
        findUrls();
        imageGen();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownLoadTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... strings) {

            String webpage = "";
            URL url;
            HttpURLConnection connection;

            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader =  new InputStreamReader(inputStream);
                int data = reader.read();

                while (data!=-1){

                    char current = (char)data;
                    webpage += current;

                    data = reader.read();
                }

                return  webpage;

            } catch (Exception e) {
                e.printStackTrace();

                return "Failed";
            }

        }
    }
}
