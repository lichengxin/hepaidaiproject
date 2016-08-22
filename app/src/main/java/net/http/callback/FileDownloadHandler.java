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

import android.text.TextUtils;
import org.apache.http.HttpEntity;

public class FileDownloadHandler {

    public java.io.File handleEntity(HttpEntity entity,
                             RequestCallBackHandler callBackHandler,
                             String target,
                             boolean isResume,
                             String responseFileName) throws java.io.IOException {
        if (entity == null || TextUtils.isEmpty(target)) {
            return null;
        }

        java.io.File targetFile = new java.io.File(target);

        if (!targetFile.exists()) {
        	java.io.File dir = targetFile.getParentFile();
            if (dir.exists() || dir.mkdirs()) {
                targetFile.createNewFile();
            }
        }

        long current = 0;
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;
        try {
        	java.io.FileOutputStream fileOutputStream = null;
            if (isResume) {
                current = targetFile.length();
                fileOutputStream = new java.io.FileOutputStream(target, true);
            } else {
                fileOutputStream = new java.io.FileOutputStream(target);
            }
            long total = entity.getContentLength() + current;
            bis = new java.io.BufferedInputStream(entity.getContent());
            bos = new java.io.BufferedOutputStream(fileOutputStream);

            if (callBackHandler != null && !callBackHandler.updateProgress(total, current, true)) {
                return targetFile;
            }

            byte[] tmp = new byte[4096];
            int len;
            while ((len = bis.read(tmp)) != -1) {
                bos.write(tmp, 0, len);
                current += len;
                if (callBackHandler != null) {
                    if (!callBackHandler.updateProgress(total, current, false)) {
                        return targetFile;
                    }
                }
            }
            bos.flush();
            if (callBackHandler != null) {
                callBackHandler.updateProgress(total, current, true);
            }
        } finally {
        	utils.IOUtils.closeQuietly(bis);
        	utils.IOUtils.closeQuietly(bos);
        }

        if (targetFile.exists() && !TextUtils.isEmpty(responseFileName)) {
        	java.io.File newFile = new java.io.File(targetFile.getParent(), responseFileName);
            while (newFile.exists()) {
                newFile = new java.io.File(targetFile.getParent(), System.currentTimeMillis() + responseFileName);
            }
            return targetFile.renameTo(newFile) ? newFile : targetFile;
        } else {
            return targetFile;
        }
    }

}
