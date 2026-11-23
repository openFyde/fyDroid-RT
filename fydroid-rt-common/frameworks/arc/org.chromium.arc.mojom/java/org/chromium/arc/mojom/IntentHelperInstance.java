// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface IntentHelperInstance extends Interface {
    public static final Interface.Manager<IntentHelperInstance, Proxy> MANAGER = IntentHelperInstance_Internal.MANAGER;

    public interface HandleCameraResultResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface InitResponse extends Callbacks.Callback0 {
    }

    public interface Proxy extends IntentHelperInstance, Interface.Proxy {
    }

    public interface RequestActivityIconsResponse extends Callbacks.Callback1<ActivityIcon[]> {
    }

    public interface RequestIntentHandlerListResponse extends Callbacks.Callback1<IntentHandlerInfo[]> {
    }

    public interface RequestTextSelectionActionsResponse extends Callbacks.Callback1<TextSelectionAction[]> {
    }

    public interface RequestUrlHandlerListResponse extends Callbacks.Callback1<IntentHandlerInfo[]> {
    }

    public interface RequestUrlListHandlerListResponse extends Callbacks.Callback1<IntentHandlerInfo[]> {
    }

    void addPreferredPackage(String str);

    void enableAccessibilityFeatures(AccessibilityFeatures accessibilityFeatures);

    void handleCameraResult(int i, int i2, byte[] bArr, HandleCameraResultResponse handleCameraResultResponse);

    void handleIntent(IntentInfo intentInfo, ActivityName activityName);

    void handleIntentWithWindowInfo(IntentInfo intentInfo, ActivityName activityName, WindowInfo windowInfo);

    void handleUrl(String str, String str2);

    void init(IntentHelperHost intentHelperHost, InitResponse initResponse);

    void requestActivityIcons(ActivityName[] activityNameArr, int i, RequestActivityIconsResponse requestActivityIconsResponse);

    void requestDomainVerificationStatusUpdate();

    void requestIntentHandlerList(IntentInfo intentInfo, RequestIntentHandlerListResponse requestIntentHandlerListResponse);

    void requestTextSelectionActions(String str, int i, RequestTextSelectionActionsResponse requestTextSelectionActionsResponse);

    void requestUrlHandlerList(String str, RequestUrlHandlerListResponse requestUrlHandlerListResponse);

    void requestUrlListHandlerList(UrlWithMimeType[] urlWithMimeTypeArr, RequestUrlListHandlerListResponse requestUrlListHandlerListResponse);

    void sendBroadcast(String str, String str2, String str3, String str4);

    void setCaptionStyle(CaptionStyle captionStyle);

    void setVerifiedLinks(String[] strArr, boolean z);
}
