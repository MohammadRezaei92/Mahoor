package rezaei.mohammad.mahoor.helper.menu;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import rezaei.mohammad.mahoor.helper.MusicPlayerRemote;
import rezaei.mohammad.mahoor.model.Song;
import rezaei.mohammad.mahoor.R;
import rezaei.mohammad.mahoor.dialogs.AddToPlaylistDialog;
import rezaei.mohammad.mahoor.dialogs.DeleteSongsDialog;

import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class SongsMenuHelper {
    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull ArrayList<Song> songs, int menuItemId) {
        switch (menuItemId) {
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(songs).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(songs).show(activity.getSupportFragmentManager(), "DELETE_SONGS");
                return true;
        }
        return false;
    }
}
