/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.http.callback;

import org.apache.http.HttpEntity;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class StringDownloadHandler {

    public String handleEntity(HttpEntity entity, RequestCallBackHandler callBackHandler, String charset) throws IOException {
        if (entity == null) return null;

        long current = 0;
        long total = entity.getContentLength();

        if (callBackHandler != null && !callBackHandler.updateProgress(total, current, true)) {
            return null;
        }

        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
                current += sizeOfString(line, charset);
                if (callBackHandler != null) {
                    if (!callBackHandler.updateProgress(total, current, false)) {
                        break;
                    }
                }
            }
            if (callBackHandler != null) {
                callBackHandler.updateProgress(total, current, true);
            }
        } finally {
        	utils.IOUtils.closeQuietly(inputStream);
        }
        return sb.toString().trim();
    }

    private static final int STRING_BUFFER_LENGTH = 100;

    public static long sizeOfString(final String str, String charset) throws UnsupportedEncodingException {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int len = str.length();
        if (len < STRING_BUFFER_LENGTH) {
            return str.getBytes(charset).length;
        }
        long size = 0;
        for (int i = 0; i < len; i += STRING_BUFFER_LENGTH) {
            int end = i + STRING_BUFFER_LENGTH;
            end = end < len ? end : len;
            String temp = getSubString(str, i, end);
            size += temp.getBytes(charset).length;
        }
        return size;
    }

    public static String getSubString(final String str, int start, int end) {
        return new String(str.substring(start, end));
    }
}
