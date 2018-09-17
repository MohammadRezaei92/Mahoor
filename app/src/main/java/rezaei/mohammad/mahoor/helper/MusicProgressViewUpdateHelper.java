package rezaei.mohammad.mahoor.helper;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class MusicProgressViewUpdateHelper extends Handler {
    private static final int CMD_REFRESH_PROGRESS_VIEWS = 1;

    private static final int MIN_INTERVAL = 20;
    private static final int UPDATE_INTERVAL_PLAYING = 1000;
    private static final int UPDATE_INTERVAL_PAUSED = 500;

    private Callback callback;

    public void start() {
        queueNextRefresh(1);
    }

    public void stop() {
        removeMessages(CMD_REFRESH_PROGRESS_VIEWS);
    }

    public MusicProgressViewUpdateHelper(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (msg.what == CMD_REFRESH_PROGRESS_VIEWS) {
            queueNextRefresh(refreshProgressViews());
        }
    }

    private int refreshProgressViews() {
        final int progressMillis = MusicPlayerRemote.getSongProgressMillis();
        final int totalMillis = MusicPlayerRemote.getSongDurationMillis();

        callback.onUpdateProgressViews(progressMillis, totalMillis);

        if (!MusicPlayerRemote.isPlaying()) {
            return UPDATE_INTERVAL_PAUSED;
        }

        final int remainingMillis = UPDATE_INTERVAL_PLAYING - progressMillis % UPDATE_INTERVAL_PLAYING;

        return Math.max(MIN_INTERVAL, remainingMillis);
    }

    private void queueNextRefresh(final long delay) {
        final Message message = obtainMessage(CMD_REFRESH_PROGRESS_VIEWS);
        removeMessages(CMD_REFRESH_PROGRESS_VIEWS);
        sendMessageDelayed(message, delay);
    }

    public interface Callback {
        void onUpdateProgressViews(int progress, int total);
    }
}