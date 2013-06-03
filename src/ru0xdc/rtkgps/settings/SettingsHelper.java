package ru0xdc.rtkgps.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru0xdc.rtklib.ProcessingOptions;
import ru0xdc.rtklib.RtkServerSettings;
import ru0xdc.rtklib.RtkServerSettings.InputStream;
import ru0xdc.rtklib.RtkServerSettings.LogStream;
import ru0xdc.rtklib.RtkServerSettings.OutputStream;
import ru0xdc.rtklib.RtkServerSettings.TransportSettings;
import ru0xdc.rtklib.SolutionOptions;
import ru0xdc.rtklib.constants.SolutionFormat;
import ru0xdc.rtklib.constants.StreamFormat;
import ru0xdc.rtklib.constants.StreamType;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

public class SettingsHelper {


    static class StreamDefaultsBase {

        protected boolean enable;
        protected StreamType type;
        protected StreamFileClientFragment.Value fileClientDefaults;
        protected StreamNtripClientFragment.Value ntripClientDefaults;
        protected StreamTcpClientFragment.Value tcpClientDefaults;
        protected StreamBluetoothFragment.Value bluetoothDefaults;

        public StreamDefaultsBase() {
            enable = true;
            type = StreamType.NTRIPCLI;
            fileClientDefaults = new StreamFileClientFragment.Value();
            ntripClientDefaults = new StreamNtripClientFragment.Value();
            tcpClientDefaults = new StreamTcpClientFragment.Value();
            bluetoothDefaults = new StreamBluetoothFragment.Value();
        }

        public StreamDefaultsBase setEnabled(boolean enabled) {
            enable = enabled;
            return this;
        }

        public StreamDefaultsBase setType(StreamType type) {
            this.type = type;
            return this;
        }

        public StreamDefaultsBase setFileClientDefaults(StreamFileClientFragment.Value defaults) {
            this.fileClientDefaults = defaults;
            return this;
        }

        public StreamDefaultsBase setNtripClientDEfaults(StreamNtripClientFragment.Value defaults) {
            this.ntripClientDefaults = defaults;
            return this;
        }

        public StreamDefaultsBase setTcpClientDefaults(StreamTcpClientFragment.Value defaults) {
            this.tcpClientDefaults = defaults;
            return this;
        }
    }

    static class InputStreamDefaults extends StreamDefaultsBase {

        private StreamFormat format;
        private String receiverOption;
        protected StationPositionActivity.Value positionDefaults;

        public InputStreamDefaults() {
            super();
            format = StreamFormat.RTCM3;
            receiverOption = "";
            positionDefaults = new StationPositionActivity.Value();
        }

        public InputStreamDefaults setFormat(StreamFormat format) {
            this.format = format;
            return this;
        }

        public StreamDefaultsBase setPositionDefaults(StationPositionActivity.Value defaults) {
            this.positionDefaults = defaults;
            return this;
        }
    }

    static class OutputStreamDefaults extends StreamDefaultsBase {

        private SolutionFormat format;

        public OutputStreamDefaults() {
            super();
            type = StreamType.FILE;
            format = SolutionFormat.LLH;
        }

        public OutputStreamDefaults setFormat(SolutionFormat format) {
            this.format = format;
            return this;
        }
    }

    static class LogStreamDefaults extends StreamDefaultsBase {

        public LogStreamDefaults() {
            super();
            type = StreamType.FILE;
        }
    }


    public static void setDefaultValues(Context ctx, boolean force) {

        ProcessingOptions1Fragment.setDefaultValues(ctx, force);
        InputRoverFragment.setDefaultValues(ctx, force);
        InputBaseFragment.setDefaultValues(ctx, force);
        InputCorrectionFragment.setDefaultValues(ctx, force);
        OutputSolution1Fragment.setDefaultValues(ctx, force);
        OutputSolution2Fragment.setDefaultValues(ctx, force);
        LogRoverFragment.setDefaultValues(ctx, force);
        LogBaseFragment.setDefaultValues(ctx, force);
        LogCorrectionFragment.setDefaultValues(ctx, force);
    }


    public static RtkServerSettings loadSettings(Context ctx) {
        final RtkServerSettings settings;
        ProcessingOptions procOpts;
        SolutionOptions solOptsBase;

        settings = new RtkServerSettings();

        procOpts = ProcessingOptions1Fragment.readPrefs(ctx);
        settings.setProcessingOptions(procOpts);

        solOptsBase = SolutionOutputSettingsFragment.readPrefs(ctx);

        settings
        .setInputRover(InputRoverFragment.readPrefs(ctx))
        .setInputBase(InputBaseFragment.readPrefs(ctx))
        .setInputCorrection(InputCorrectionFragment.readPrefs(ctx))
        .setOutputSolution1(OutputSolution1Fragment.readPrefs(ctx, solOptsBase))
        .setOutputSolution2(OutputSolution2Fragment.readPrefs(ctx, solOptsBase))
        .setLogRover(LogRoverFragment.readPrefs(ctx))
        .setLogBase(LogBaseFragment.readPrefs(ctx))
        .setLogCorrection(LogCorrectionFragment.readPrefs(ctx))
        ;



        // TODO: send NMEA to base setting

        return settings;
    }

    static void setInputStreamDefaultValues(Context ctx,
            String sharedPrefsName,
            boolean force,
            InputStreamDefaults defaults
            ) {
        final SharedPreferences prefs;

        prefs = ctx.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);

        final boolean needUpdate = force || !prefs.contains(InputRoverFragment.KEY_ENABLE);

        if (needUpdate) {
            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean(InputRoverFragment.KEY_ENABLE, defaults.enable)
            .putString(InputRoverFragment.KEY_TYPE, defaults.type.name())
            .putString(InputRoverFragment.KEY_FORMAT, defaults.format.name())
            .putString(InputRoverFragment.KEY_RECEIVER_OPTION, defaults.receiverOption)
            .commit();

            StartupShutdownSettingsActivity.setDefaultValue(ctx, sharedPrefsName);
            StationPositionActivity.setDefaultValue(ctx, sharedPrefsName, defaults.positionDefaults);
            StreamFileClientFragment.setDefaultValue(ctx, sharedPrefsName, defaults.fileClientDefaults);
            StreamNtripClientFragment.setDefaultValue(ctx, sharedPrefsName, defaults.ntripClientDefaults);
            StreamTcpClientFragment.setDefaultValue(ctx, sharedPrefsName, defaults.tcpClientDefaults);
            StreamBluetoothFragment.setDefaultValue(ctx, sharedPrefsName, defaults.bluetoothDefaults);
        }
    }

    static void setOutputStreamDefaultValues(Context ctx, String sharedPrefsName, boolean force,
            OutputStreamDefaults defaults) {
        final SharedPreferences prefs;

        prefs = ctx.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);

        final boolean needUpdate = force || !prefs.contains(OutputSolution1Fragment.KEY_ENABLE);

        if (needUpdate) {
            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean(OutputSolution1Fragment.KEY_ENABLE, defaults.enable)
            .putString(OutputSolution1Fragment.KEY_TYPE, defaults.type.name())
            .putString(OutputSolution1Fragment.KEY_FORMAT, defaults.format.name())
            ;
            e.commit();
            StreamFileClientFragment.setDefaultValue(ctx, sharedPrefsName, defaults.fileClientDefaults);
            StreamNtripClientFragment.setDefaultValue(ctx, sharedPrefsName, defaults.ntripClientDefaults);
            StreamTcpClientFragment.setDefaultValue(ctx, sharedPrefsName,  defaults.tcpClientDefaults);
            StreamBluetoothFragment.setDefaultValue(ctx, sharedPrefsName, defaults.bluetoothDefaults);
        }
    }

    static void setLogStreamDefaultValues(Context ctx, String sharedPrefsName, boolean force, LogStreamDefaults defaults) {
        final SharedPreferences prefs;

        prefs = ctx.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);

        final boolean needUpdate = force || !prefs.contains(LogRoverFragment.KEY_ENABLE);

        if (needUpdate) {
            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean(LogRoverFragment.KEY_ENABLE, defaults.enable)
            .putString(LogRoverFragment.KEY_TYPE, defaults.type.name())
            ;
            e.commit();
            StreamFileClientFragment.setDefaultValue(ctx, sharedPrefsName, defaults.fileClientDefaults);
            StreamNtripClientFragment.setDefaultValue(ctx, sharedPrefsName, defaults.ntripClientDefaults);
            StreamTcpClientFragment.setDefaultValue(ctx, sharedPrefsName, defaults.tcpClientDefaults);
        }

    }

    @Nonnull
    static InputStream readInputStreamPrefs(Context ctx, String sharedPrefsName) {
        final SharedPreferences prefs;
        final InputStream stream;
        StreamType type;

        stream = new InputStream();
        prefs = ctx.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);

        if (!prefs.contains(InputRoverFragment.KEY_TYPE)) throw new IllegalStateException();

        if (!prefs.getBoolean(InputRoverFragment.KEY_ENABLE, false)) {
            stream.setTransportSettings(RtkServerSettings.TRANSPORT_DUMMY);
            return stream;
        }

        type = StreamType.valueOf(prefs.getString(InputRoverFragment.KEY_TYPE, null));
        stream
        .setFormat(StreamFormat.valueOf(prefs.getString(InputRoverFragment.KEY_FORMAT, StreamFormat.RTCM3.name())))
        .setCommandsAtStartup(
                prefs.getBoolean(StartupShutdownSettingsActivity.SHARED_PREFS_KEY_SEND_COMMANDS_AT_STARTUP, false),
                prefs.getString(StartupShutdownSettingsActivity.SHARED_PREFS_KEY_COMMANDS_AT_STARTUP, ""))
        .setCommandsAtShutdown(
                prefs.getBoolean(StartupShutdownSettingsActivity.SHARED_PREFS_KEY_SEND_COMMANDS_AT_SHUTDOWN, false),
                prefs.getString(StartupShutdownSettingsActivity.SHARED_PREFS_KEY_COMMANDS_AT_SHUTDOWN, ""))
        .setReceiverOption(prefs.getString(InputRoverFragment.KEY_RECEIVER_OPTION, ""))
        ;

        stream.setTransportSettings(readTransportSettings(ctx, type, prefs, sharedPrefsName));

        final StationPositionActivity.Value posSettings = StationPositionActivity.readSettings(prefs);
        stream.setStationPos(posSettings.getType(), posSettings.getPosition());

        return stream;
    }

    public static OutputStream readOutputStreamPrefs(Context ctx,
            String sharedPrefsName, SolutionOptions base) {
        final SharedPreferences prefs;
        final OutputStream stream;
        StreamType type;

        stream = new OutputStream();
        stream.setSolutionOptions(base);

        prefs = ctx.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);

        if (!prefs.contains(OutputSolution1Fragment.KEY_TYPE)) throw new IllegalStateException();

        if (!prefs.getBoolean(OutputSolution1Fragment.KEY_ENABLE, false)) {
            stream.setTransportSettings(RtkServerSettings.TRANSPORT_DUMMY);
            return stream;
        }

        type = StreamType.valueOf(prefs.getString(OutputSolution1Fragment.KEY_TYPE, null));
        stream
        .setSolutionFormat(SolutionFormat.valueOf(prefs.getString(OutputSolution1Fragment.KEY_FORMAT,SolutionFormat.NMEA.name())))
        .setTransportSettings(readTransportSettings(ctx, type, prefs, sharedPrefsName))
        ;

        return stream;
    }

    public static LogStream readLogStreamPrefs(Context ctx, String sharedPrefsName) {
        final SharedPreferences prefs;
        final LogStream stream;
        StreamType type;

        stream = new LogStream();

        prefs = ctx.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);

        if (!prefs.contains(LogRoverFragment.KEY_TYPE)) throw new IllegalStateException();

        if (!prefs.getBoolean(LogRoverFragment.KEY_ENABLE, false)) {
            stream.setTransportSettings(RtkServerSettings.TRANSPORT_DUMMY);
            return stream;
        }

        type = StreamType.valueOf(prefs.getString(LogRoverFragment.KEY_TYPE, null));
        stream.setTransportSettings(readTransportSettings(ctx, type, prefs, sharedPrefsName));

        return stream;
    }

    @Nonnull
    static TransportSettings readTransportSettings(Context context, StreamType type, SharedPreferences prefs, String stream) {
        TransportSettings settings;

        switch(type) {
        case FILE:
            settings = StreamFileClientFragment.readSettings(prefs);
            break;
        case NTRIPCLI:
            settings = StreamNtripClientFragment.readSettings(prefs);
            break;
        case TCPCLI:
            settings = StreamTcpClientFragment.readSettings(prefs);
            break;
        case BLUETOOTH:
            settings = StreamBluetoothFragment.readSettings(context, prefs, stream);
            break;
        case USB:
            settings = StreamUsbFragment.readSettings(context, prefs, stream);
            break;
        case NONE:
            settings = RtkServerSettings.TRANSPORT_DUMMY;
            break;
        default:
            throw new IllegalArgumentException();
        }
        return settings;
    }

    @Nonnull
    static String readInputStreamSumary(Resources resources, SharedPreferences prefs) {
        StreamType type;
        type = StreamType.valueOf(prefs.getString(InputRoverFragment.KEY_TYPE, StreamType.NONE.name()));
        return readStreamSummary(resources,type, prefs);
    }

    @Nonnull
    static String readOutputStreamSumary(Resources resources, SharedPreferences prefs) {
        StreamType type;
        type = StreamType.valueOf(prefs.getString(OutputSolution1Fragment.KEY_TYPE, StreamType.NONE.name()));
        return readStreamSummary(resources, type, prefs);
    }

    @Nonnull
    static String readLogStreamSumary(Resources resources, SharedPreferences prefs) {
        StreamType type;
        type = StreamType.valueOf(prefs.getString(LogRoverFragment.KEY_TYPE, StreamType.NONE.name()));
        return readStreamSummary(resources, type, prefs);
    }

    @Nonnull
    static String readStreamSummary(Resources resources, StreamType type, SharedPreferences prefs) {
        String summary;
        switch(type) {
        case FILE:
            summary = StreamFileClientFragment.readSummary(prefs);
            break;
        case NTRIPCLI:
            summary = StreamNtripClientFragment.readSummary(prefs);
            break;
        case TCPCLI:
            summary = StreamTcpClientFragment.readSummary(prefs);
            break;
        case BLUETOOTH:
            summary = StreamBluetoothFragment.readSummary(resources, prefs);
            break;
        case USB:
            summary = StreamUsbFragment.readSummary(resources, prefs);
            break;
        case NONE:
            summary="";
            break;
        default:
            throw new IllegalArgumentException();
        }
        return summary;
    }

    /**
    *
    * @return [user[:passwd]@]addr[:port][/mntpnt[:str]])
    */
   @Nonnull
   static String encodeNtripTcpPath(
           @Nullable String user,
           @Nullable String passwd,
           @Nullable String host,
           @Nullable String port,
           @Nullable String mountpoint,
           @Nullable String str) {
       StringBuilder path;
       path = new StringBuilder();

       final boolean emptyUser, emptyPasswd;

       emptyUser = TextUtils.isEmpty(user);
       emptyPasswd = TextUtils.isEmpty(passwd);

       if (!emptyUser) {
           path.append(Uri.encode(user));
       }

       if (!emptyPasswd) {
           if (!emptyUser) path.append(':');
           path.append(Uri.encode(passwd));
       }

       if (!emptyUser || !emptyPasswd) {
           path.append('@');
       }

       if (TextUtils.isEmpty(host)) host = "localhost";

       path.append(host);
       if (!TextUtils.isEmpty(port)) {
           path.append(':').append(port);
       }

       path.append('/');

       if (!TextUtils.isEmpty(mountpoint)) path.append(mountpoint);

       if (!TextUtils.isEmpty(str)) {
           path.append(':').append(str);
       }

       return path.toString();
   }


}
