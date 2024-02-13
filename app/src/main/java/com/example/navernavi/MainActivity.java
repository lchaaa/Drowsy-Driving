package com.example.navernavi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.navernavi.PopupActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView textView, textView2;
    private int count = 0;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);

        editText = findViewById(R.id.edittext);
        textView = findViewById(R.id.send_text);
        textView2 = findViewById(R.id.server_text);
        Button sendButton = findViewById(R.id.send_btn);
        final MediaPlayer mp=MediaPlayer.create(this,R.raw.a);

        //휴게소 버튼
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //근처 휴게소
                MainActivity.this.startActivity
                        (new Intent("android.intent.action.VIEW",
                                Uri.parse("nmap://search?query=근처휴게소&appname=com.example.navernavi")));
            }
        });

        //신고버튼
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:11900"));
                try {
                    startActivity(callIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //졸음장치 연결 버튼
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {send(data);}
                }).start();
            }
        });

        Button serverButton = findViewById(R.id.server_btn);
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer();
                    }
                }).start();
            }
        });
    }


    //음량설정 버튼
    public void mOnPopupClick (View v){
        //데이터 담아서 팝업(액티비티) 호출
        Intent intent = new Intent(this, PopupActivity.class);
        startActivityForResult(intent, 1);

    }


   //로그를 출력하고 UI를 업데이트
    public void printClientLog(final String data) {
        Log.d("MainActivity", data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append(data + "\n");
            }
        });
    }

    //로그를 출력하고 UI를 업데이트
    public void printServerLog(final String data) {
        Log.d("MainActivity", data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView2.append(data + "\n");
            }
        });
    }

    //TCP,IP 소켓 통신
    public void send(String data) {
        final MediaPlayer mp=MediaPlayer.create(this,R.raw.a);
        final MediaPlayer mp2=MediaPlayer.create(this,R.raw.b);
        final MediaPlayer mp3=MediaPlayer.create(this,R.raw.c);

        //라즈베리파이 안드로이드 연결
        try {
            int portNumber = 8081 ; //포트번호 라즈베리파이 맞추기
            Socket socket = new Socket("192.168.95.254", portNumber);
            printClientLog("소켓 연결함");
            mp3.start(); //라즈베리파이 안드로이드 연결시 사운드 재생
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            while (true) { //졸음운전 인식
                String message = in.readLine();
                printClientLog("서버로부터 받음: " + message);
                mp.start(); //졸음운전 인식 사운드 재생
                count++;
                if(count==3) {
                 mp2.start(); //졸음운전 인식 3번 소리재생
                    MainActivity.this.startActivity //졸음운전 인식 3번 근처휴게소안내
                            (new Intent("android.intent.action.VIEW",
                                    Uri.parse("nmap://search?query=근처휴게소&appname=com.example.navernavi")));
                  count=0;

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startServer() {
        try {
            int portNumber = 8081;

            ServerSocket server = new ServerSocket(portNumber);
            printServerLog("서버 시작함: " + portNumber);

            while (true) {
                Socket socket = server.accept();
                InetAddress clientHost = socket.getLocalAddress();
                int clientPort = socket.getPort();
                printServerLog("클라이언트 연결됨: " + clientHost + " : " + clientPort);

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Object obj = inputStream.readObject();
                printServerLog("데이터 받음: " + obj);

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(obj + " from Server.");
                outputStream.flush();
                printServerLog("데이터 보냄");
                // mp.start();

                socket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}