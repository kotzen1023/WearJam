package com.seventhmoon.wearjam.Data;



public class Constants {

    public interface ACTION {
        String ADD_SONG_LIST_COMPLETE = "com.seventhmoon.WearJam.AddSongListComplete";
        String GET_SEARCHLIST_ACTION = "com.seventhmoon.WearJam.GetSearchListAction";
        String GET_PLAY_COMPLETE = "com.seventhmoon.WearJam.GetPlayComplete";

        String GET_SONGLIST_ACTION = "com.seventhmoon.WearJam.GetSongListAction";
        String GET_SONGLIST_FROM_RECORD_FILE_COMPLETE = "com.seventhmoon.WearJam.GetSongListFromRecordFileComplete";

        String SAVE_SONGLIST_ACTION = "com.seventhmoon.WearJam.SaveSongListAction";
        String SAVE_SONGLIST_TO_FILE_COMPLETE = "com.seventhmoon.WearJam.SaveSongListToFileComplete";

        String RECEIVE_UPLOAD_COMPLETE = "com.seventhmoon.WearJam.ReceiveUploadComplete";

        String GET_UPDATE_VIEW_ACTION = "com.seventhmoon.WearJam.GetUpdateViewAction";

        String MEDIAPLAYER_STATE_PLAYED = "com.seventhmoon.WearJam.MediaPlayerStatePlayed";
        String MEDIAPLAYER_STATE_PAUSED = "com.seventhmoon.WearJam.MediaPlayerStatePaused";

        String GET_AVAILABLE_SPACE = "com.seventhmoon.WearJam.GetAvailableSpace";
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
