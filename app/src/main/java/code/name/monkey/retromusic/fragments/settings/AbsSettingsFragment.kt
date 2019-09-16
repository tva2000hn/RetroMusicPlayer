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

package code.name.monkey.retromusic.fragments.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreferenceFragmentCompat
import code.name.monkey.retromusic.preferences.*
import code.name.monkey.retromusic.util.NavigationUtil

/**
 * @author Hemanth S (h4h13).
 */

abstract class AbsSettingsFragment : ATEPreferenceFragmentCompat() {

    internal fun showProToastAndNavigate(message: String) {
        Toast.makeText(requireContext(), "$message is Pro version feature.", Toast.LENGTH_SHORT).show()
        NavigationUtil.goToProVersion(requireActivity())
    }

    internal fun setSummary(preference: Preference, value: Any) {
        val stringValue = value.toString()
        if (preference is ListPreference) {
            val index = preference.findIndexOfValue(stringValue)
            preference.setSummary(if (index >= 0) preference.entries[index] else null)
        } else {
            preference.summary = stringValue
        }
    }

    abstract fun invalidateSettings()

    protected fun setSummary(preference: Preference) {
        setSummary(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.context)
                .getString(preference.key, "")!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDivider(ColorDrawable(Color.TRANSPARENT))
        listView.setBackgroundColor(ThemeStore.primaryColor(requireContext()))
        listView.overScrollMode = View.OVER_SCROLL_NEVER
        listView.setPadding(0, 0, 0, 0)
        listView.setPaddingRelative(0, 0, 0, 0)
        invalidateSettings()
    }

    override fun onCreatePreferenceDialog(preference: Preference): DialogFragment? {
        return when (preference) {
            is LibraryPreference -> LibraryPreferenceDialog.newInstance()
            is NowPlayingScreenPreference -> NowPlayingScreenPreferenceDialog.newInstance()
            is AlbumCoverStylePreference -> AlbumCoverStylePreferenceDialog.newInstance()
            is BlacklistPreference -> BlacklistPreferenceDialog.newInstance()
            else -> super.onCreatePreferenceDialog(preference)
        }
    }
}
