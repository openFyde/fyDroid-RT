package io.fydeos.arc.gateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstallReceiver extends BroadcastReceiver {
    private static final String TAG = "ArcGateway";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received install broadcast: " + intent.getAction()
                + " data=" + intent.getData());
        Intent serviceIntent = new Intent(context, ArcGatewayService.class);
        serviceIntent.setAction(intent.getAction());
        serviceIntent.setDataAndType(intent.getData(), intent.getType());
        if (intent.getExtras() != null) {
            serviceIntent.putExtras(intent.getExtras());
        }
        context.startService(serviceIntent);
    }
}
