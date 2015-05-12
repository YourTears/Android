package apprtc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.fanxin.app.R;

import apprtc.CallActivity;

/**
 * Created by tiazh on 4/14/2015.
 */
public class RoomConnector {
    private Activity m_activity = null;
    private Context m_context = null;

    public RoomConnector(Activity activity, Context context)
    {
        m_activity = activity;
        m_context =context;
    }

    public void connectToRoom(String roomId, int runTimeMs) {
        String roomUrl = getString(R.string.pref_room_server_url_default);

        // Video call enabled flag.
        boolean videoCallEnabled = Boolean.valueOf(getString(R.string.pref_videocall_default));

        // Get default codecs.
        String videoCodec = getString(R.string.pref_videocodec_default);
        String audioCodec = getString(R.string.pref_audiocodec_default);

        // Check HW codec flag.
        boolean hwCodec = Boolean.valueOf(getString(R.string.pref_hwcodec_default));

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        String resolution = getString(R.string.pref_resolution_default);
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        String fps = getString(R.string.pref_fps_default);
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {
            }
        }

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        String bitrateTypeDefault = getString(
                R.string.pref_startvideobitrate_default);
        String bitrateType = bitrateTypeDefault;
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = getString(R.string.pref_startvideobitratevalue_default);
            videoStartBitrate = Integer.parseInt(bitrateValue);
        }
        int audioStartBitrate = 0;
        bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
        bitrateType = bitrateTypeDefault;
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = getString(R.string.pref_startaudiobitratevalue_default);
            audioStartBitrate = Integer.parseInt(bitrateValue);
        }

        // Test if CpuOveruseDetection should be disabled. By default is on.
        boolean cpuOveruseDetection = Boolean.valueOf(getString(R.string.pref_cpu_usage_detection_default));

        // Check statistics display option.
        boolean displayHud = Boolean.valueOf(getString(R.string.pref_displayhud_default));

        Uri uri = Uri.parse(roomUrl);
        Intent intent = new Intent(m_context, CallActivity.class);
        intent.setData(uri);
        intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
        intent.putExtra(CallActivity.EXTRA_LOOPBACK, false);
        intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
        intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
        intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
        intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
        intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
        intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
        intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
        intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
        intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
        intent.putExtra(CallActivity.EXTRA_CPUOVERUSE_DETECTION,
                cpuOveruseDetection);
        intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
        intent.putExtra(CallActivity.EXTRA_CMDLINE, false);
        intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);

        m_activity.startActivityForResult(intent, 1);
    }

    private String getString(int id)
    {
        return m_context.getString(id);
    }
}
