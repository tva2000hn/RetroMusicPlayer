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

package code.name.monkey.retromusic.dialogs

import android.app.Dialog
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import code.name.monkey.appthemehelper.util.MaterialUtil
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.extensions.appHandleColor
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.PlaylistsUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class CreatePlaylistDialog : DialogFragment() {

    private lateinit var playlistView: TextInputEditText
    private lateinit var actionNewPlaylistContainer: TextInputLayout

    override fun onCreateDialog(
            savedInstanceState: Bundle?
    ): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_playlist, null)
        playlistView = view.findViewById(R.id.actionNewPlaylist)
        actionNewPlaylistContainer = view.findViewById(R.id.actionNewPlaylistContainer)

        val dialog = MaterialDialog.Builder(requireActivity())
                .title(R.string.new_playlist_title)
                .positiveText(R.string.create_action)
                .negativeText(android.R.string.cancel)
                .customView(view, false)
                .onPositive { _, _ ->
                    val name = playlistView.text.toString().trim()
                    if (name.isNotEmpty()) {
                        if (!PlaylistsUtil.doesPlaylistExist(requireContext(), name)) {
                            val songs = arguments?.getParcelableArrayList<Song>("songs")
                                    ?: return@onPositive
                            if (playlistView.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                                val playlistId = PlaylistsUtil.createPlaylist(requireContext(), playlistView.text.toString())
                                if (playlistId != -1 && activity != null) {
                                    PlaylistsUtil.addToPlaylist(requireContext(), songs, playlistId, true)
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.playlist_exists, name), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .build()

        MaterialUtil.setTint(actionNewPlaylistContainer, false)

        val playlistId = arguments!!.getLong(MediaStore.Audio.Playlists.Members.PLAYLIST_ID)
        playlistView.appHandleColor().setText(PlaylistsUtil.getNameForPlaylist(context!!, playlistId), TextView.BufferType.EDITABLE)
        return dialog
        /*val materialDialog = MaterialDialog(activity!!, BottomSheet())
                .show {
                    title(string.new_playlist_title)
                    customView(layout.dialog_playlist)
                    negativeButton(android.R.string.cancel)
                    positiveButton(string.create_action) {
                        if (activity == null) {
                            return@positiveButton
                        }
                        val songs = arguments?.getParcelableArrayList<Song>("songs")
                                ?: return@positiveButton

                        if (playlistView.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                            val playlistId = PlaylistsUtil.createPlaylist(requireContext(), playlistView.text.toString())
                            if (playlistId != -1 && activity != null) {
                                PlaylistsUtil.addToPlaylist(requireContext(), songs, playlistId, true)
                            }
                        }
                    }
                    getActionButton(WhichButton.POSITIVE).updateTextColor(ThemeStore.accentColor(context))
                }*/

        /*val dialogView = materialDialog.getCustomView()
        playlistView = dialogView.findViewById(R.id.actionNewPlaylist)
        actionNewPlaylistContainer = dialogView.findViewById(R.id.actionNewPlaylistContainer)

        MaterialUtil.setTint(actionNewPlaylistContainer, false)

        val playlistId = arguments!!.getLong(MediaStore.Audio.Playlists.Members.PLAYLIST_ID)
        playlistView.appHandleColor().setText(PlaylistsUtil.getNameForPlaylist(context!!, playlistId), TextView.BufferType.EDITABLE)
        return materialDialog*/
    }

    companion object {
        private const val SONGS = "songs"
        @JvmOverloads
        fun create(song: Song? = null): CreatePlaylistDialog {
            val list = ArrayList<Song>()
            if (song != null) {
                list.add(song)
            }
            return create(list)
        }

        fun create(songs: ArrayList<Song>): CreatePlaylistDialog {
            val dialog = CreatePlaylistDialog()
            val args = Bundle()
            args.putParcelableArrayList("songs", songs)
            dialog.arguments = args
            return dialog
        }
    }
}