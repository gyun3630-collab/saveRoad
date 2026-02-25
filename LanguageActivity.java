package com.example.mytest;

import static com.example.mytest.R.layout.settingactivity_layout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class LanguageActivity extends AppCompatActivity {
    private static final String LANGUAGE_PREF_KEY = "language";
    private boolean isLanguageChanged = false;
    private AtomicReference<String> savedLanguage = new AtomicReference<>();

    LayoutInflater inflater;
    View settingLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.languageactivity_layout);


        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settingLayout = inflater.inflate(settingactivity_layout, null);
        // 언어 설정 적용
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        savedLanguage.set(preferences.getString(LANGUAGE_PREF_KEY, "한국어"));

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.language_title);

        // 언어 선택 스피너 설정
        setupLanguageSpinner(preferences, savedLanguage);

        // 폰트 크기 선택 스피너 설정
        setupFontSizeSpinner(preferences);

        // 모든 레이아웃에 폰트 크기 적용
        applyFontSizeToAllLayouts();
    }

    // 모든 레이아웃에 폰트 크기 적용
    private void applyFontSizeToAllLayouts() {
        Log.d("FontSize", "applyFontSizeToAllLayouts() 호출됨");
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fontSize = preferences.getString("fontSize", "25Pt");
        applyFontSize(fontSize);
    }

    // 언어 선택 스피너 설정
    private void setupLanguageSpinner(SharedPreferences preferences, AtomicReference<String> savedLanguageRef) {
        Spinner spinner = findViewById(R.id.spinner);
        String[] languages = getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String savedLanguage = savedLanguageRef.get();
        int savedLanguagePosition = adapter.getPosition(savedLanguage);
        spinner.setSelection(savedLanguagePosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                if (!selectedLanguage.equals(savedLanguageRef.get())) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(LANGUAGE_PREF_KEY, selectedLanguage);
                    editor.apply();

                    // 언어가 변경되었음을 표시하고 활동 재시작
                    isLanguageChanged = true;
                    setLocale(selectedLanguage);

                    // 선택된 언어를 저장
                    savedLanguageRef.set(selectedLanguage);

                    // 언어가 변경된 후 스피너를 다시 설정
                    spinner.setSelection(position);
                    recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    // 폰트 크기 선택 스피너 설정
    private void setupFontSizeSpinner(SharedPreferences preferences) {
        Spinner spinner = findViewById(R.id.spinner2);
        String[] fontSizes = getResources().getStringArray(R.array.font);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fontSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String defaultFontSize = preferences.getString("fontSize", "25sp");
        int defaultFontSizePosition = adapter.getPosition(defaultFontSize);
        spinner.setSelection(defaultFontSizePosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFontSize = parent.getItemAtPosition(position).toString();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("fontSize", selectedFontSize);
                editor.apply();

                // 선택된 폰트 크기 적용
                applyFontSize(selectedFontSize);

                // 폰트 크기 변경 후 스피너를 다시 설정
                spinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 초기 폰트 크기 적용
        applyFontSize(defaultFontSize);
    }


    // 폰트 크기 적용
    private void applyFontSize(String fontSize) {
        float size;
        switch (fontSize) {
            case "25sp":
                size = 25f;
                break;
            case "30sp":
                size = 30f;
                break;
            case "35sp":
                size = 35f;
                break;
            default:
                size = 25f;
                break;
        }

        // 루트 뷰 그룹 가져오기
        int[] rootLayoutIds = {
                R.id.coordinator_layout, R.id.root_layout_1, R.id.root_layout_2, R.id.root_layout_3,
                R.id.root_layout_4, R.id.root_layout_5, R.id.root_layout_6, R.id.root_layout_7,
                R.id.root_layout_8, R.id.root_layout_9, R.id.root_layout_10, R.id.root_layout_11,
                R.id.root_layout_12
        };

        for (int rootId : rootLayoutIds) {
            ViewGroup rootLayout = (ViewGroup) settingLayout.findViewById(rootId);
            if (rootLayout == null) {
                Log.d("FontSize", "Root layout with ID " + rootId + " is null.");
            } else {
                applyFontSizeToAllTextViews(rootLayout, size);
            }
        }
    }


    // 모든 텍스트 뷰에 폰트 크기 적용
    private void applyFontSizeToAllTextViews(ViewGroup root, float size) {
        if (root == null) {
            Log.d("FontSize", "Root layout is null.");
            return;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                applyFontSizeToAllTextViews((ViewGroup) child, size);
            } else if (child instanceof TextView) {
                TextView textView = (TextView) child;
                // TextView의 폰트 크기 설정
                textView.setTextSize(size);
                // TextView를 다시 그리도록 요청
                textView.invalidate();
                Log.d("sdfsdf", String.valueOf(textView.getTextSize()));
            }
        }
    }


    // 언어 설정
    private void setLocale(String language) {
        Locale locale;
        switch (language) {
            case "영어":
                locale = new Locale("en");
                break;
            case "한국어":
                locale = new Locale("ko");
                break;
            case "중국어":
                locale = new Locale("zh");
                break;
            case "스페인어":
                locale = new Locale("es");
                break;
            default:
                locale = Locale.getDefault();
                break;
        }

        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        isLanguageChanged = true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), set_activity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

