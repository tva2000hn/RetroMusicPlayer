/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RoundedCornerTreatment;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import code.name.monkey.retromusic.R;
import code.name.monkey.retromusic.activities.saf.SAFGuideActivity;
import code.name.monkey.retromusic.misc.DialogAsyncTask;
import code.name.monkey.retromusic.model.Song;
import code.name.monkey.retromusic.util.SAFUtil;

/**
 * Created by hemanths on 2019-07-31.
 */
public class DeleteSongsAsyncTask extends DialogAsyncTask<DeleteSongsAsyncTask.LoadingInfo, Integer, Void> {
    private WeakReference<DeleteSongsDialog> dialogReference;
    private WeakReference<FragmentActivity> activityWeakReference;


    public DeleteSongsAsyncTask(@NonNull DeleteSongsDialog dialog) {
        super(dialog.getActivity());
        this.dialogReference = new WeakReference<>(dialog);
        this.activityWeakReference = new WeakReference<>(dialog.getActivity());
    }

    @NonNull
    @Override
    protected Dialog createDialog(@NonNull Context context) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.deleting_songs)
                .setView(R.layout.loading)
                .setCancelable(false);
        MaterialShapeDrawable alertBackground = (MaterialShapeDrawable) materialAlertDialogBuilder.getBackground();
        if (alertBackground != null) {
            alertBackground.setShapeAppearanceModel(
                    alertBackground.getShapeAppearanceModel()
                            .toBuilder()
                            .setAllCorners(new RoundedCornerTreatment(10.0f))
                            .build());
        }
        return materialAlertDialogBuilder.create();
    }

    @Nullable
    @Override
    protected Void doInBackground(@NonNull LoadingInfo... loadingInfos) {
        try {
            LoadingInfo info = loadingInfos[0];
            DeleteSongsDialog dialog = this.dialogReference.get();
            FragmentActivity fragmentActivity = this.activityWeakReference.get();

            if (dialog == null || fragmentActivity == null) {
                return null;
            }

            if (!info.isIntent) {
                if (!SAFUtil.isSAFRequiredForSongs(info.songs)) {
                    dialog.deleteSongs(info.songs, null);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (SAFUtil.isSDCardAccessGranted(fragmentActivity)) {
                            dialog.deleteSongs(info.songs, null);
                        } else {
                            dialog.startActivityForResult(new Intent(fragmentActivity, SAFGuideActivity.class), SAFGuideActivity.REQUEST_CODE_SAF_GUIDE);
                        }
                    } else {
                        Log.i("Hmm", "doInBackground: kitkat delete songs");
                    }
                }
            } else {
                switch (info.requestCode) {
                    case SAFUtil.REQUEST_SAF_PICK_TREE:
                        if (info.resultCode == Activity.RESULT_OK) {
                            SAFUtil.saveTreeUri(fragmentActivity, info.intent);
                            if (dialog.songsToRemove != null) {
                                dialog.deleteSongs(dialog.songsToRemove, null);
                            }
                        }
                        break;
                    case SAFUtil.REQUEST_SAF_PICK_FILE:
                        if (info.resultCode == Activity.RESULT_OK) {
                            dialog.deleteSongs(Collections.singletonList(dialog.currentSong), Collections.singletonList(info.intent.getData()));
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class LoadingInfo {
        public boolean isIntent;

        public List<Song> songs;
        public List<Uri> safUris;

        public int requestCode;
        public int resultCode;
        public Intent intent;

        public LoadingInfo(List<Song> songs, List<Uri> safUris) {
            this.isIntent = false;
            this.songs = songs;
            this.safUris = safUris;
        }

        public LoadingInfo(int requestCode, int resultCode, Intent intent) {
            this.isIntent = true;
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.intent = intent;
        }
    }
}
