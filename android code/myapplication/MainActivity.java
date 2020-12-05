package com.example.dhrtmdgh.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.dhrtmdgh.myapplication.utils.AudioWriterPCM;
import com.example.dhrtmdgh.myapplication.utils.YoutubeActivity;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;


















public class MainActivity extends YouTubeBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "00E3oewf53m6rhmaUdjy";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;


    Intent YoutubeIntent;

     TextView txtResult;
    TextView text;
    private Button btnStart;
    WebView wb;


/*
    Button btn2;
    YouTubePlayerView youTubeView;
    YouTubePlayer.OnInitializedListener listener;//유튜브 리스너
    YouTubePlayer youTubePlayerInstance;//유튜브 플레이어의 인스턴스
    String targetVideo = "FwnsEWETu6E";//재생하려는 유튜브 비디오. url상의 맷 뒷부분(v= 부분)만 넣으면 됨.
*/





     String mResult;

    private AudioWriterPCM writer;

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

           case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);

                break;



          case R.id.finalResult:

                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();

                txtResult.setText(mResult);


              String a=txtResult.getText().subSequence(txtResult.length()-2,txtResult.length()).toString();


              if(a.equals("검색")||a.equals("검섹")||a.equals("검색해")||a.equals("검색해줘")) {
              String s = "https://search.naver.com/search.naver?sm=tab_hty.top&where=nexearch&query="
                      + txtResult.getText().subSequence(0,txtResult.length()-2).toString();
              wb.setWebViewClient(new WebViewClient());
              wb.loadUrl(s);
          }

              if(a.equals("재생")) {

                  YoutubeIntent.putExtra("SearchWord",txtResult.getText().subSequence(0,txtResult.length()-2).toString());
                  startActivity(YoutubeIntent);
              }




                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                //btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YoutubeIntent=new Intent(getApplicationContext(),YoutubeActivity.class);

        txtResult = (TextView) findViewById(R.id.txt_result);
        text=(TextView)findViewById(R.id.texttt);

        btnStart = (Button) findViewById(R.id.btn_start);
        wb=findViewById(R.id.WebView);






























        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    btnStart.setText(R.string.str_stop);

                    naverRecognizer.recognize();



                } else {
                    Log.d(TAG, "stop and wait Final Result");

                    btnStart.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
        txtResult.setText("");
        //btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        RecognitionHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }


}
