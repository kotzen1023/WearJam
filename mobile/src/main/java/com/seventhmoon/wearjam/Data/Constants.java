package com.seventhmoon.wearjam.Data;



public class Constants {
    public interface ACTION {
        String ADD_SONG_LIST_COMPLETE = "com.seventhmoon.WearJam.AddSongListComplete";
        String ADD_SONG_LIST_CHANGE = "com.seventhmoon.WearJam.AddSongListChange";
        String GET_PLAY_COMPLETE = "com.seventhmoon.WearJam.GetPlayComplete";
        String GET_SONGLIST_ACTION = "com.seventhmoon.WearJam.GetSongListAction";

        String GET_SEARCHLIST_ACTION = "com.seventhmoon.WearJam.GetSearchListAction";

        String GET_SONGLIST_FROM_RECORD_FILE_COMPLETE = "com.seventhmoon.WearJam.GetSongListFromRecordFileComplete";
        String SAVE_SONGLIST_ACTION = "com.seventhmoon.WearJam.SaveSongListAction";
        String SAVE_SONGLIST_TO_FILE_COMPLETE = "com.seventhmoon.WearJam.SaveSongListToFileComplete";

        String MEDIAPLAYER_STATE_PLAYED = "com.seventhmoon.WearJam.MediaPlayerStatePlayed";
        String MEDIAPLAYER_STATE_PAUSED = "com.seventhmoon.WearJam.MediaPlayerStatePaused";

        //for upload to watch
        String UPLOAD_SONGS_TO_WATCH_ACTION = "com.seventhmoon.UploadSongsToWatchAction";
        String GET_UPLOAD_SONG_COMPLETE = "com.seventhmoon.GetUpLoadSongComplete";
        String UPLOAD_SONGS_DIALOG_ACTION = "com.seventhmoon.UploadSongsDialogAction";
    }

    public enum STATE {
        Created,
        Idle,
        Initialized,
        Preparing,
        Prepared,
        Started,
        Paused,
        Stopped,
        PlaybackCompleted,
        End,
        Error,

    }
}
