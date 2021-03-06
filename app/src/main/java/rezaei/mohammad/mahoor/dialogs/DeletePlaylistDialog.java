package rezaei.mohammad.mahoor.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import rezaei.mohammad.mahoor.model.Playlist;
import rezaei.mohammad.mahoor.util.PlaylistsUtil;
import rezaei.mohammad.mahoor.R;

import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class DeletePlaylistDialog extends DialogFragment {

    @NonNull
    public static DeletePlaylistDialog create(Playlist playlist) {
        ArrayList<Playlist> list = new ArrayList<>();
        list.add(playlist);
        return create(list);
    }

    @NonNull
    public static DeletePlaylistDialog create(ArrayList<Playlist> playlists) {
        DeletePlaylistDialog dialog = new DeletePlaylistDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("playlists", playlists);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //noinspection unchecked
        final ArrayList<Playlist> playlists = getArguments().getParcelableArrayList("playlists");
        int title;
        CharSequence content;
        //noinspection ConstantConditions
        if (playlists.size() > 1) {
            title = R.string.delete_playlists_title;
            content = Html.fromHtml(getString(R.string.delete_x_playlists, playlists.size()));
        } else {
            title = R.string.delete_playlist_title;
            content = Html.fromHtml(getString(R.string.delete_playlist_x, playlists.get(0).name));
        }
        return new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(content)
                .positiveText(R.string.delete_action)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (getActivity() == null)
                            return;
                        PlaylistsUtil.deletePlaylists(getActivity(), playlists);
                    }
                })
                .typeface(ResourcesCompat.getFont(requireActivity(),R.font.iran_sans)
                        ,ResourcesCompat.getFont(requireActivity(),R.font.iran_sans))
                .build();
    }
}