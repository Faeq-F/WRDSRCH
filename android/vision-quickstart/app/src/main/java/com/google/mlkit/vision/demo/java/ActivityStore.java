package com.google.mlkit.vision.demo.java;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.java.textdetector.TextGraphic;

import java.util.HashMap;

public class ActivityStore extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer);


        TextView mTextView = (TextView) findViewById(R.id.textView);
        //String[] words = (String[]) TextGraphic.WordsToFind.toArray();
        //int i = 0;
        //String acc = "";
        /*
        for (int[] word : CoreSearchMethods.searchWords(TextGraphic.Word_src, words).values() ){
            i++;

            acc += String.valueOf(word[0]) + ", " + String.valueOf(word[1])+"; length: "+String.valueOf(words[i].length())+"\n";

        }*/
        //Log.d("TAG", acc);
        mTextView.setText("Please see log for output - memory leak");



    }

}