package com.example.jarvisassistant;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView status;
    private TextToSpeech tts;
    private static final int SPEECH_REQUEST = 42;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
        tts = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) tts.setLanguage(Locale.getDefault());
        });
        setStatus("Ready. Tap the microphone button and say a command.");
    }

    private void buildUi() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(28, 44, 28, 28);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setBackgroundColor(Color.rgb(9, 14, 27));

        TextView title = new TextView(this);
        title.setText("J.A.R.V.I.S");
        title.setTextSize(32);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(93, 220, 255));
        title.setGravity(Gravity.CENTER);
        page.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(this);
        subtitle.setText("PERSONAL VOICE ASSISTANT");
        subtitle.setTextSize(12);
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(-1, -2);
        subParams.setMargins(0, 8, 0, 34);
        page.addView(subtitle, subParams);

        Button mic = new Button(this);
        mic.setText("🎙  TAP TO SPEAK");
        mic.setTextSize(17);
        mic.setOnClickListener(v -> startListening());
        page.addView(mic, new LinearLayout.LayoutParams(-1, 58));

        status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setTextSize(16);
        status.setPadding(18, 24, 18, 24);
        status.setText("Ready");
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, -2);
        statusParams.setMargins(0, 20, 0, 20);
        page.addView(status, statusParams);

        TextView examples = new TextView(this);
        examples.setText("TRY SAYING\n\n• Open YouTube\n• Open WhatsApp\n• Search for Minecraft tips\n• Open Chrome\n• What time is it?\n• Help");
        examples.setTextColor(Color.rgb(205, 218, 235));
        examples.setTextSize(16);
        examples.setPadding(20, 20, 20, 20);
        examples.setBackgroundColor(Color.rgb(20, 30, 48));
        page.addView(examples, new LinearLayout.LayoutParams(-1, -2));

        TextView note = new TextView(this);
        note.setText("Tap-to-talk version • No OpenAI API required");
        note.setTextColor(Color.GRAY);
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(-1, -2);
        noteParams.setMargins(0, 22, 0, 0);
        page.addView(note, noteParams);

        setContentView(page);
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command for JARVIS");
        try {
            startActivityForResult(intent, SPEECH_REQUEST);
        } catch (Exception e) {
            respond("Speech recognition is not available on this phone. Check your Google app and language settings.");
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) handleCommand(matches.get(0));
        }
    }

    private void handleCommand(String raw) {
        String command = raw.toLowerCase(Locale.ROOT).trim();
        setStatus("You said: " + raw);
        if (command.contains("youtube")) {
            openApp("com.google.android.youtube", "YouTube");
        } else if (command.contains("whatsapp")) {
            openApp("com.whatsapp", "WhatsApp");
        } else if (command.contains("chrome")) {
            openApp("com.android.chrome", "Chrome");
        } else if (command.startsWith("search for ") || command.startsWith("search ")) {
            String query = command.startsWith("search for ") ? raw.substring(11).trim() : raw.substring(7).trim();
            if (query.isEmpty()) query = "web search";
            respond("Searching the web for " + query);
            Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)));
            startActivity(web);
        } else if (command.contains("what time") || command.equals("time")) {
            String time = new java.text.SimpleDateFormat("h:mm a", Locale.getDefault()).format(new java.util.Date());
            respond("The time is " + time);
        } else if (command.contains("help")) {
            respond("You can ask me to open YouTube, WhatsApp, or Chrome, or say search for followed by your topic.");
        } else {
            respond("I heard " + raw + ". Try saying open YouTube, open WhatsApp, or search for a topic.");
        }
    }

    private void openApp(String packageName, String label) {
        Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch != null) {
            respond("Opening " + label);
            startActivity(launch);
        } else {
            respond(label + " does not appear to be installed.");
        }
    }

    private void respond(String message) {
        setStatus(message);
        if (tts != null) tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "jarvis_reply");
    }

    private void setStatus(String message) {
        if (status != null) status.setText(message);
    }

    @Override protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
