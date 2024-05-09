/**
 * Copyright 2021 The exTHmUI Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.messaging.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.android.messaging.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaptchaKeywordsUtils {

    private Context mContext;

    private List<String> mKeywords = new ArrayList<>();

    private BuglePrefs mPrefs;
    private String mDefaultKeywordString;
    private String mKeywordsPrefsKey;

    public CaptchaKeywordsUtils(Context context) {
        this.mContext = context;

        mPrefs = BuglePrefs.getApplicationPrefs();

        mDefaultKeywordString = this.mContext.getString(R.string.captcha_keywords_default);
        mKeywordsPrefsKey = this.mContext.getString(R.string.captcha_keywords_key);

        parseKeywordsList();
    }

    private void parseKeywordsList() {
        mKeywords.clear();
        String keyWordsData = mPrefs.getString(this.mKeywordsPrefsKey, mDefaultKeywordString);
        if (TextUtils.isEmpty(keyWordsData)) keyWordsData = mDefaultKeywordString;

        String[] keyWordsListArray = keyWordsData.split("\n");
        mKeywords.addAll(Arrays.asList(keyWordsListArray));
    }

    public List<String> getKeywordsList() {
        return this.mKeywords;
    }

    private void saveKeywordsList() {
        String keywordListData = TextUtils.join("\n", mKeywords);
        mPrefs.putString(this.mKeywordsPrefsKey, keywordListData);
    }

    public int addKeywordToList(String str) {
        if (!mKeywords.contains(str)) {
            mKeywords.add(str);
            return 0;
        } else {
            return -1;
        }
    }

    public void removeStringFromList(String str) {
        mKeywords.remove(str);
        saveKeywordsList();
    }

    public void addKeywords(EditText editText) {
        if (!TextUtils.isEmpty(editText.getText())) {
            String[] keywordArr = editText.getText().toString().split("\n");
            for (String keyword : keywordArr) {
                int addKeywordsResult = addKeywordToList(keyword);
                if (addKeywordsResult == -1) {
                    new MaterialAlertDialogBuilder(this.mContext)
                            .setTitle(R.string.error)
                            .setMessage(R.string.captcha_keyword_duplicate)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                    return;
                }
            }
            saveKeywordsList();
            Toast.makeText(this.mContext, R.string.captcha_keyword_add_successsful_tip, Toast.LENGTH_SHORT).show();
        } else {
            new MaterialAlertDialogBuilder(this.mContext)
                    .setTitle(R.string.error)
                    .setMessage(R.string.captcha_keyword_add_empty)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

}