package com.example.languager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText sourceLanguageEt;
    private TextView destinationLanguageTv;
    private MaterialButton sourceLanguageChooseBtn;
    private MaterialButton destinationLanguageChooseBtn;
    private MaterialButton translateBtn;

    private TranslatorOptions translatorOptions;
    private Translator translator;
    private ProgressDialog progressDialog;
    private ArrayList<ModelLanguage> languagesArrayList;
    private static  final String TAG = "MAIN_TAG";

    private String sourceLanguageCode = "en";
    private String sourceLanguageTitle = "English";
    private String destinationLanguageCode = "tr";
    private String destinationLanguageTitle = "Turkish";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourceLanguageEt = findViewById(R.id.sourceLanguageEt);
        destinationLanguageTv = findViewById(R.id.destinationLanguageTv);
        sourceLanguageChooseBtn = findViewById(R.id.sourceLanguageChooseBtn);
        destinationLanguageChooseBtn = findViewById(R.id.destinationLanguageChooseBtn);
        translateBtn = findViewById(R.id.translateBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAvailableLanguages();

        sourceLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SourceLanguageChoose();
            }
        });

        destinationLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DestinationLanguageChoose();
            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateDATA();
            }
        });
    }

    private  String sourceLanguageText ="";
    private void validateDATA() {
        sourceLanguageText =sourceLanguageEt.getText().toString().trim();

        Log.d(TAG, "validateDATA: sourceLanguageText: "+sourceLanguageText);

        if (sourceLanguageText.isEmpty()){
            Toast.makeText(this, "Enter text to translate...", Toast.LENGTH_SHORT).show();
        }
        else {
            startTranslations();
        }
    }

    private void startTranslations() {
        progressDialog.setMessage("Processing language model...");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();
        translator = Translation.getClient(translatorOptions);


        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.d(TAG, "onSuccess: model ready, starting translate...");

                        progressDialog.setMessage("Translating...");

                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {

                                        Log.d(TAG, "onSuccess: translatedText: "+translatedText);
                                        progressDialog.dismiss();

                                        destinationLanguageTv.setText(translatedText);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.e(TAG, "onFailure: ",e );
                                        Toast.makeText(MainActivity.this,"Failed to translate due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onFailure: ",e );
                        Toast.makeText(MainActivity.this, "Failed to ready model due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SourceLanguageChoose() {
        PopupMenu popupMenu = new PopupMenu(this, sourceLanguageChooseBtn);

        for (int i=0; i<languagesArrayList.size(); i++) {

            popupMenu.getMenu().add(Menu.NONE, i, i, languagesArrayList.get(i).languageTitle);

        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem Item) {

                int position = Item.getItemId();

                sourceLanguageCode = languagesArrayList.get(position).languageCode;
                sourceLanguageTitle = languagesArrayList.get(position).languageTitle;

                sourceLanguageChooseBtn.setText(sourceLanguageTitle);
                sourceLanguageEt.setHint("Enter "+sourceLanguageTitle);

                Log.d(TAG, "onMenuItemClick: sourceLanguageCode: "+sourceLanguageCode);
                Log.d(TAG, "onMenuItemClick: sourceLanguageTitle: "+sourceLanguageTitle);

                return false;
            }
        });
    }

    private void DestinationLanguageChoose() {
        PopupMenu popupMenu = new PopupMenu(this, destinationLanguageChooseBtn);

        for (int i=0; i<languagesArrayList.size(); i++) {

            popupMenu.getMenu().add(Menu.NONE, i, i, languagesArrayList.get(i).getLanguageTitle());

        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem Item) {

                int position = Item.getItemId();

                destinationLanguageCode = languagesArrayList.get(position).languageCode;
                destinationLanguageTitle = languagesArrayList.get(position).languageTitle;

                destinationLanguageChooseBtn.setText(destinationLanguageTitle);

                Log.d(TAG, "onMenuItemClick: destinationLanguageCode: "+destinationLanguageCode);
                Log.d(TAG, "onMenuItemClick: destinationLanguageTitle: "+destinationLanguageTitle);

                return false;
            }
        });
    }
    private void loadAvailableLanguages() {
        languagesArrayList = new ArrayList<>();

        List<String> languageCodelist = TranslateLanguage.getAllLanguages();

        for (String languageCode: languageCodelist) {
            String languageTitle = new Locale(languageCode).getDisplayLanguage();

            Log.d(TAG, "loadAvaibleLanguages: languageCode" + languageCode);
            Log.d(TAG, "loadAvaibleLanguages: languageTitle" + languageTitle);

            ModelLanguage modelLanguage = new ModelLanguage(languageCode, languageTitle);
            languagesArrayList.add(modelLanguage);
        }
    }
}