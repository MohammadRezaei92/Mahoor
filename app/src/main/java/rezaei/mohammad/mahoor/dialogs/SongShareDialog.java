package rezaei.mohammad.mahoor.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import rezaei.mohammad.mahoor.model.Song;
import rezaei.mohammad.mahoor.util.MusicUtil;
import rezaei.mohammad.mahoor.R;
import rezaei.mohammad.mahoor.util.Util;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class SongShareDialog extends DialogFragment {
    @NonNull
    public static SongShareDialog create(final Song song) {
        final SongShareDialog dialog = new SongShareDialog();
        final Bundle args = new Bundle();
        args.putParcelable("song", song);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Song song = getArguments().getParcelable("song");
        final String currentlyListening = getString(R.string.currently_listening_to_x_by_x, song.title, song.artistName);
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.what_do_you_want_to_share)
                .items(getString(R.string.the_audio_file), "\u201C" + currentlyListening + "\u201D")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        switch (i) {
                            case 0:
                                Util.enableStrictMode();
                                startActivity(Intent.createChooser(MusicUtil.createShareSongFileIntent(song), null));
                                break;
                            case 1:
                                getActivity().startActivity(
                                        Intent.createChooser(
                                                new Intent()
                                                        .setAction(Intent.ACTION_SEND)
                                                        .putExtra(Intent.EXTRA_TEXT, currentlyListening)
                                                        .setType("text/plain"),
                                                null
                                        )
                                );
                                break;
                        }
                    }
                })
                .typeface(ResourcesCompat.getFont(requireActivity(),R.font.iran_sans)
                        ,ResourcesCompat.getFont(requireActivity(),R.font.iran_sans))
                .build();
    }
}
