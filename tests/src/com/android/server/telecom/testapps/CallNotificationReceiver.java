/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.telecom.testapps;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.CallState;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

/**
 * This class receives the notification callback intents used to update call states for
 * {@link TestConnectionService}.
 */
public class CallNotificationReceiver extends BroadcastReceiver {

    static final String TAG = CallNotificationReceiver.class.getSimpleName();
    /**
     * Exit intent action is sent when the user clicks the "exit" action of the
     * TestConnectionService notification. Used to cancel (remove) the notification.
     */
    static final String ACTION_CALL_SERVICE_EXIT =
            "com.android.server.telecom.testapps.ACTION_CALL_SERVICE_EXIT";
    static final String ACTION_REGISTER_PHONE_ACCOUNT =
            "com.android.server.telecom.testapps.ACTION_REGISTER_PHONE_ACCOUNT";
    static final String ACTION_SHOW_ALL_PHONE_ACCOUNTS =
            "com.android.server.telecom.testapps.ACTION_SHOW_ALL_PHONE_ACCOUNTS";
    static final String ACTION_VIDEO_CALL =
            "com.android.server.telecom.testapps.ACTION_VIDEO_CALL";
    static final String ACTION_AUDIO_CALL =
            "com.android.server.telecom.testapps.ACTION_AUDIO_CALL";

    /** {@inheritDoc} */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_CALL_SERVICE_EXIT.equals(action)) {
            CallServiceNotifier.getInstance().cancelNotifications(context);
        } else if (ACTION_REGISTER_PHONE_ACCOUNT.equals(action)) {
            CallServiceNotifier.getInstance().registerPhoneAccount(context);
        } else if (ACTION_SHOW_ALL_PHONE_ACCOUNTS.equals(action)) {
            CallServiceNotifier.getInstance().showAllPhoneAccounts(context);
        } else if (ACTION_VIDEO_CALL.equals(action)) {
            sendIncomingCallIntent(context, null, true);
        } else if (ACTION_AUDIO_CALL.equals(action)) {
            sendIncomingCallIntent(context, null, false);
        }
    }

    /**
     * Creates and sends the intent to add an incoming call through Telecom.
     *
     * @param context The current context.
     * @param isVideoCall {@code True} if this is a video call.
     */
    public static void sendIncomingCallIntent(Context context, Uri handle, boolean isVideoCall) {
        PhoneAccountHandle phoneAccount = new PhoneAccountHandle(
                new ComponentName(context, TestConnectionService.class),
                CallServiceNotifier.SIM_SUBSCRIPTION_ID);

        // For the purposes of testing, indicate whether the incoming call is a video call by
        // stashing an indicator in the EXTRA_INCOMING_CALL_EXTRAS.
        Bundle extras = new Bundle();
        extras.putBoolean(TestConnectionService.EXTRA_IS_VIDEO_CALL, isVideoCall);
        if (handle != null) {
            extras.putParcelable(TestConnectionService.EXTRA_HANDLE, handle);
        }

        TelecomManager.from(context).addNewIncomingCall(phoneAccount, extras);
    }

    public static void addNewUnknownCall(Context context, Uri handle, Bundle extras) {
        Log.i(TAG, "Adding new unknown call with handle " + handle);
        PhoneAccountHandle phoneAccount = new PhoneAccountHandle(
                new ComponentName(context, TestConnectionService.class),
                CallServiceNotifier.SIM_SUBSCRIPTION_ID);

        if (extras == null) {
            extras = new Bundle();
        }

        if (handle != null) {
            extras.putParcelable(TelecomManager.EXTRA_UNKNOWN_CALL_HANDLE, handle);
            extras.putParcelable(TestConnectionService.EXTRA_HANDLE, handle);
        }

        TelecomManager.from(context).addNewUnknownCall(phoneAccount, extras);
    }
}
