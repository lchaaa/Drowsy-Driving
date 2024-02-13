package com.example.navernavi;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

//음량설정
public class PopupActivity extends Activity {
        TextView txtText;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.popup_activity);

                SeekBar seekVolumn = (SeekBar) findViewById(R.id.SeekBar_Volumn);
                final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int nCurrentVolumn = audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);

                seekVolumn.setMax(nMax);
                seekVolumn.setProgress(nCurrentVolumn);
                seekVolumn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                                // TODO Auto-generated method stub
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                                // TODO Auto-generated method stub
                        }
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress,
                                                      boolean fromUser) {
                                // TODO Auto-generated method stub
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                        progress, 0);
                        }
                });
                //UI 객체생성
                txtText = (TextView)findViewById(R.id.txtText);

                //데이터 가져오기
                Intent intent = getIntent();
                String data = intent.getStringExtra("data");
                txtText.setText(data);
        }
        //확인 버튼 클릭
        public void mOnClose(View v){
                //데이터 전달하기
                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);
                //액티비티(팝업) 닫기
                finish();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
                //바깥레이어 클릭시 안닫히게
                if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
                        return false;
                }
                return true;
        }

        @Override
        public void onBackPressed() {
                //안드로이드 백버튼 막기
                return;
        }
}
