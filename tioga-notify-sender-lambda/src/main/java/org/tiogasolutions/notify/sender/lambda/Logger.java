package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;

import java.text.SimpleDateFormat;

public class Logger {

    private final Context context;

    public Logger(Context context) {
        this.context = context;
    }

    public void log(String msg) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.sql.Timestamp(System.currentTimeMillis()));
        context.getLogger().log("[" + timestamp + "] " + msg.trim() + "\n");
    }
}
