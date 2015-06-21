/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
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
package com.welove.activity;

import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.welove.app.R;
import common.photoview.PhotoView;

import appLogic.AppConstant;
import common.ImageLoaderManager;

/**
 * 下载显示大图
 */
public class BigImageActivity extends Activity {

	private ProgressDialog pd;
	private ProgressBar loadLocalPb;

	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_big_image);
		super.onCreate(savedInstanceState);

		PhotoView photoView = (PhotoView) findViewById(R.id.image);
		loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        String cacheId = getIntent().getExtras().getString("cacheId");
        String imageUrl = getIntent().getExtras().getString("imageUrl");

        AppConstant.imageLoaderManager.loadImage(photoView, cacheId, imageUrl, ImageLoaderManager.CacheMode.CacheAndSave, true);

        photoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}

	private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("下载图片: 0%");
		pd.show();
	}
}
