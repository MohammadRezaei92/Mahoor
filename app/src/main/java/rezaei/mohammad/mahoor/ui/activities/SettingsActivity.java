package rezaei.mohammad.mahoor.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.prefs.supportv7.ATEPreferenceFragmentCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import rezaei.mohammad.mahoor.R;
import rezaei.mohammad.mahoor.appshortcuts.DynamicShortcutManager;
import rezaei.mohammad.mahoor.preferences.NowPlayingScreenPreference;
import rezaei.mohammad.mahoor.preferences.NowPlayingScreenPreferenceDialog;
import rezaei.mohammad.mahoor.ui.activities.base.AbsBaseActivity;
import rezaei.mohammad.mahoor.util.NavigationUtil;
import rezaei.mohammad.mahoor.util.PreferenceUtil;

public class SettingsActivity extends AbsBaseActivity implements ColorChooserDialog.ColorCallback {
    public static final String TAG = SettingsActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setDrawUnderStatusbar(true);
        ButterKnife.bind(this);

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        } else {
            SettingsFragment frag = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (frag != null) frag.invalidateSettings();
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        switch (dialog.getTitle()) {
            case R.string.primary_color:
                ThemeStore.editTheme(this)
                        .primaryColor(selectedColor)
                        .commit();
                break;
            case R.string.accent_color:
                ThemeStore.editTheme(this)
                        .accentColor(selectedColor)
                        .commit();
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            new DynamicShortcutManager(this).updateDynamicShortcuts();
        }
        recreate();
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends ATEPreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static void setSummary(@NonNull Preference preference) {
            setSummary(preference, PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), ""));
        }

        private static void setSummary(Preference preference, @NonNull Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                preference.setSummary(stringValue);
            }
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.pref_general);
            //addPreferencesFromResource(R.xml.pref_colors);
            //addPreferencesFromResource(R.xml.pref_notification);
            addPreferencesFromResource(R.xml.pref_now_playing_screen);
            addPreferencesFromResource(R.xml.pref_images);
            addPreferencesFromResource(R.xml.pref_lockscreen);
            addPreferencesFromResource(R.xml.pref_audio);
            addPreferencesFromResource(R.xml.pref_playlists);
        }

        @Nullable
        @Override
        public DialogFragment onCreatePreferenceDialog(Preference preference) {
            if (preference instanceof NowPlayingScreenPreference) {
                return NowPlayingScreenPreferenceDialog.newInstance();
            }
            return super.onCreatePreferenceDialog(preference);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getListView().setPadding(0, 0, 0, 0);
            invalidateSettings();
            PreferenceUtil.getInstance(getActivity()).registerOnSharedPreferenceChangedListener(this);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            PreferenceUtil.getInstance(getActivity()).unregisterOnSharedPreferenceChangedListener(this);
        }

        private void invalidateSettings() {
            final Preference defaultStartPage = findPreference("default_start_page");
            setSummary(defaultStartPage);
            defaultStartPage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, @NonNull Object o) {
                    setSummary(defaultStartPage, o);
                    return true;
                }
            });

            final Preference generalTheme = findPreference("general_theme");
            setSummary(generalTheme);
            generalTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, @NonNull Object o) {
                    setSummary(generalTheme, o);
                    ThemeStore.editTheme(getActivity())
                            .activityTheme(PreferenceUtil.getThemeResFromPrefValue((String) o))
                            .commit();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        // Set the new theme so that updateAppShortcuts can pull it
                        getActivity().setTheme(PreferenceUtil.getThemeResFromPrefValue((String) o));
                        new DynamicShortcutManager(getActivity()).updateDynamicShortcuts();
                    }

                    getActivity().recreate();
                    return true;
                }
            });

            final Preference autoDownloadImagesPolicy = findPreference("auto_download_images_policy");
            setSummary(autoDownloadImagesPolicy);
            autoDownloadImagesPolicy.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, @NonNull Object o) {
                    setSummary(autoDownloadImagesPolicy, o);
                    return true;
                }
            });

            /*final ATEColorPreference primaryColorPref = (ATEColorPreference) findPreference("primary_color");
            final int primaryColor = ThemeStore.primaryColor(getActivity());
            primaryColorPref.setColor(primaryColor, ColorUtil.darkenColor(primaryColor));
            primaryColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    new ColorChooserDialog.Builder(((SettingsActivity) getActivity()), R.string.primary_color)
                            .accentMode(false)
                            .allowUserColorInput(true)
                            .allowUserColorInputAlpha(false)
                            .preselect(primaryColor)
                            .show();
                    return true;
                }
            });

            final ATEColorPreference accentColorPref = (ATEColorPreference) findPreference("accent_color");
            final int accentColor = ThemeStore.accentColor(getActivity());
            accentColorPref.setColor(accentColor, ColorUtil.darkenColor(accentColor));
            accentColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    new ColorChooserDialog.Builder(((SettingsActivity) getActivity()), R.string.accent_color)
                            .accentMode(true)
                            .allowUserColorInput(true)
                            .allowUserColorInputAlpha(false)
                            .preselect(accentColor)
                            .show();
                    return true;
                }
            });

            TwoStatePreference colorNavBar = (TwoStatePreference) findPreference("should_color_navigation_bar");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                colorNavBar.setVisible(false);
            } else {
                colorNavBar.setChecked(ThemeStore.coloredNavigationBar(getActivity()));
                colorNavBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        ThemeStore.editTheme(getActivity())
                                .coloredNavigationBar((Boolean) newValue)
                                .commit();
                        getActivity().recreate();
                        return true;
                    }
                });
            }

            final TwoStatePreference classicNotification = (TwoStatePreference) findPreference("classic_notification");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                classicNotification.setVisible(false);
            } else {
                classicNotification.setChecked(PreferenceUtil.getInstance(getActivity()).classicNotification());
                classicNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        // Save preference
                        PreferenceUtil.getInstance(getActivity()).setClassicNotification((Boolean) newValue);

                        final MusicService service = MusicPlayerRemote.musicService;
                        if (service != null) {
                            service.initNotification();
                            service.updateNotification();
                        }

                        return true;
                    }
                });
            }

            final TwoStatePreference colorAppShortcuts = (TwoStatePreference) findPreference("should_color_app_shortcuts");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                colorAppShortcuts.setVisible(false);
            } else {
                colorAppShortcuts.setChecked(PreferenceUtil.getInstance(getActivity()).coloredAppShortcuts());
                colorAppShortcuts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        // Save preference
                        PreferenceUtil.getInstance(getActivity()).setColoredAppShortcuts((Boolean) newValue);

                        // Update app shortcuts
                        new DynamicShortcutManager(getActivity()).updateDynamicShortcuts();

                        return true;
                    }
                });
            }*/

            final Preference equalizer = findPreference("equalizer");
            if (!hasEqualizer()) {
                equalizer.setEnabled(false);
                equalizer.setSummary(getResources().getString(R.string.no_equalizer));
            }
            equalizer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    NavigationUtil.openEqualizer(getActivity());
                    return true;
                }
            });

            updateNowPlayingScreenSummary();
        }

        private boolean hasEqualizer() {
            final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            PackageManager pm = getActivity().getPackageManager();
            ResolveInfo ri = pm.resolveActivity(effects, 0);
            return ri != null;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PreferenceUtil.NOW_PLAYING_SCREEN_ID:
                    updateNowPlayingScreenSummary();
                    break;
            }
        }

        private void updateNowPlayingScreenSummary() {
            findPreference("now_playing_screen_id").setSummary(PreferenceUtil.getInstance(getActivity()).getNowPlayingScreen().titleRes);
        }
    }
}
