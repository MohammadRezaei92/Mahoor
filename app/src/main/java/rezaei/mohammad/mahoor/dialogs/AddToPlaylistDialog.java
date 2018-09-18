package rezaei.mohammad.mahoor.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import rezaei.mohammad.mahoor.model.Playlist;
import rezaei.mohammad.mahoor.model.Song;
import rezaei.mohammad.mahoor.util.PlaylistsUtil;
import rezaei.mohammad.mahoor.R;
import rezaei.mohammad.mahoor.loader.PlaylistLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid), Aidan Follestad (afollestad)
 */
public class AddToPlaylistDialog extends DialogFragment {

    @NonNull
    public static AddToPlaylistDialog create(Song song) {
        ArrayList<Song> list = new ArrayList<>();
        list.add(song);
        return create(list);
    }

    @NonNull
    public static AddToPlaylistDialog create(ArrayList<Song> songs) {
        AddToPlaylistDialog dialog = new AddToPlaylistDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("songs", songs);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<Playlist> playlists = PlaylistLoader.getAllPlaylists(getActivity());
        CharSequence[] playlistNames = new CharSequence[playlists.size() + 1];
        playlistNames[0] = getActivity().getResources().getString(R.string.action_new_playlist);
        for (int i = 1; i < playlistNames.length; i++) {
            playlistNames[i] = playlists.get(i - 1).name;
        }
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.add_playlist_title)
                .items(playlistNames)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(@NonNull MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        //noinspection unchecked
                        final ArrayList<Song> songs = getArguments().getParcelableArrayList("songs");
                        if (songs == null) return;
                        if (i == 0) {
                            materialDialog.dismiss();
                            CreatePlaylistDialog.create(songs).show(getActivity().getSupportFragmentManager(), "ADD_TO_PLAYLIST");
                        } else {
                            materialDialog.dismiss();
                            PlaylistsUtil.addToPlaylist(getActivity(), songs, playlists.get(i - 1).id, true);
                        }
                    }
                })
                .typeface(ResourcesCompat.getFont(requireActivity(),R.font.iran_sans)
                        ,ResourcesCompat.getFont(requireActivity(),R.font.iran_sans))
                .build();
    }
}