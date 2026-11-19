// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class ArcNotificationData extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String accessibleName;
    public String appDisplayName;
    public ArcBitmap bigPicture;
    public ArcNotificationButton[] buttons;
    public ArcNotificationData[] childrenData;
    public byte[] deprecatedIconData;
    public int expandState;
    public ArcNotificationFlags flags;
    public String groupKey;
    public boolean indeterminateProgress;
    public boolean isActionEnabled;
    public boolean isCustomNotification;
    public boolean isInlineReplyEnabled;
    public boolean isMediaNotification;
    public String key;
    public String message;
    public ArcNotificationMessage[] messages;
    public boolean noClear;
    public boolean ongoingEvent;
    public String packageName;
    public int priority;
    public int progressCurrent;
    public int progressMax;
    public int remoteInputState;
    public boolean renderOnChrome;
    public int replyButtonIndex;
    public int shownContents;
    public ArcBitmap smallIcon;
    public ArcBitmap snapshotImage;
    public ArcBitmap snapshotImagePublic;
    public float snapshotImageScale;
    public int style;
    public Rect swipeInputRect;
    public String[] texts;
    public long time;
    public String title;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(80, 0), new DataHeader(88, 1), new DataHeader(104, 3), new DataHeader(104, 5), new DataHeader(112, 6), new DataHeader(120, 7), new DataHeader(128, 8), new DataHeader(136, 10), new DataHeader(136, 11), new DataHeader(144, 12), new DataHeader(152, 14), new DataHeader(160, 15), new DataHeader(168, 17), new DataHeader(176, 21), new DataHeader(176, 23), new DataHeader(176, 26), new DataHeader(176, 27), new DataHeader(176, 28), new DataHeader(184, 29), new DataHeader(192, 30), new DataHeader(200, 34), new DataHeader(208, 35)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[21];
    }

    public ArcNotificationData() {
        super(208, 35);
    }

    public static ArcNotificationData decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            ArcNotificationData arcNotificationData = new ArcNotificationData(i);
            arcNotificationData.key = decoder.readString(8, false);
            int i2 = decoder.readInt(16);
            arcNotificationData.type = i2;
            ArcNotificationType.validate(i2);
            int i3 = decoder.readInt(20);
            arcNotificationData.priority = i3;
            ArcNotificationPriority.validate(i3);
            arcNotificationData.message = decoder.readString(24, false);
            arcNotificationData.title = decoder.readString(32, false);
            arcNotificationData.appDisplayName = decoder.readString(40, true);
            arcNotificationData.deprecatedIconData = decoder.readBytes(48, 1, -1);
            arcNotificationData.time = decoder.readLong(56);
            arcNotificationData.progressCurrent = decoder.readInt(64);
            arcNotificationData.progressMax = decoder.readInt(68);
            Decoder pointer = decoder.readPointer(72, true);
            if (pointer == null) {
                arcNotificationData.buttons = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                arcNotificationData.buttons = new ArcNotificationButton[dataHeaderForPointerArray.elementsOrVersion];
                for (int i4 = 0; i4 < dataHeaderForPointerArray.elementsOrVersion; i4++) {
                    arcNotificationData.buttons[i4] = ArcNotificationButton.decode(GeneratedOutlineSupport.outline2(i4, 8, 8, pointer, false));
                }
            }
            if (i >= 1) {
                arcNotificationData.noClear = decoder.readBoolean(80, 0);
                arcNotificationData.ongoingEvent = decoder.readBoolean(80, 1);
            }
            if (i >= 5) {
                arcNotificationData.isCustomNotification = decoder.readBoolean(80, 2);
            }
            if (i >= 21) {
                arcNotificationData.indeterminateProgress = decoder.readBoolean(80, 3);
            }
            if (i >= 23) {
                arcNotificationData.isMediaNotification = decoder.readBoolean(80, 4);
            }
            if (i >= 26) {
                arcNotificationData.isActionEnabled = decoder.readBoolean(80, 5);
            }
            if (i >= 27) {
                arcNotificationData.isInlineReplyEnabled = decoder.readBoolean(80, 6);
            }
            if (i >= 28) {
                arcNotificationData.renderOnChrome = decoder.readBoolean(80, 7);
            }
            if (i >= 7) {
                arcNotificationData.snapshotImageScale = decoder.readFloat(84);
            }
            if (i >= 3) {
                Decoder pointer2 = decoder.readPointer(88, true);
                if (pointer2 == null) {
                    arcNotificationData.texts = null;
                } else {
                    DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                    arcNotificationData.texts = new String[dataHeaderForPointerArray2.elementsOrVersion];
                    for (int i5 = 0; i5 < dataHeaderForPointerArray2.elementsOrVersion; i5++) {
                        arcNotificationData.texts[i5] = GeneratedOutlineSupport.outline1(i5, 8, 8, pointer2, false);
                    }
                }
                arcNotificationData.bigPicture = ArcBitmap.decode(decoder.readPointer(96, true));
            }
            if (i >= 6) {
                arcNotificationData.smallIcon = ArcBitmap.decode(decoder.readPointer(104, true));
            }
            if (i >= 7) {
                arcNotificationData.snapshotImage = ArcBitmap.decode(decoder.readPointer(112, true));
            }
            if (i >= 8) {
                arcNotificationData.accessibleName = decoder.readString(120, true);
            }
            if (i >= 10) {
                int i6 = decoder.readInt(128);
                arcNotificationData.expandState = i6;
                ArcNotificationExpandState.validate(i6);
            }
            if (i >= 11) {
                int i7 = decoder.readInt(132);
                arcNotificationData.shownContents = i7;
                ArcNotificationShownContents.validate(i7);
            }
            if (i >= 12) {
                int i8 = decoder.readInt(136);
                arcNotificationData.remoteInputState = i8;
                ArcNotificationRemoteInputState.validate(i8);
            }
            if (i >= 26) {
                int i9 = decoder.readInt(140);
                arcNotificationData.style = i9;
                ArcNotificationStyle.validate(i9);
            }
            if (i >= 14) {
                arcNotificationData.swipeInputRect = Rect.decode(decoder.readPointer(144, true));
            }
            if (i >= 15) {
                arcNotificationData.packageName = decoder.readString(152, true);
            }
            if (i >= 17) {
                arcNotificationData.flags = ArcNotificationFlags.decode(decoder.readPointer(160, true));
            }
            if (i >= 21) {
                arcNotificationData.snapshotImagePublic = ArcBitmap.decode(decoder.readPointer(168, true));
            }
            if (i >= 29) {
                arcNotificationData.groupKey = decoder.readString(176, true);
            }
            if (i >= 30) {
                arcNotificationData.replyButtonIndex = decoder.readInt(184);
            }
            if (i >= 34) {
                Decoder pointer3 = decoder.readPointer(192, true);
                if (pointer3 == null) {
                    arcNotificationData.childrenData = null;
                } else {
                    DataHeader dataHeaderForPointerArray3 = pointer3.readDataHeaderForPointerArray(-1);
                    arcNotificationData.childrenData = new ArcNotificationData[dataHeaderForPointerArray3.elementsOrVersion];
                    for (int i10 = 0; i10 < dataHeaderForPointerArray3.elementsOrVersion; i10++) {
                        arcNotificationData.childrenData[i10] = decode(GeneratedOutlineSupport.outline2(i10, 8, 8, pointer3, false));
                    }
                }
            }
            if (i >= 35) {
                Decoder pointer4 = decoder.readPointer(200, true);
                if (pointer4 == null) {
                    arcNotificationData.messages = null;
                } else {
                    DataHeader dataHeaderForPointerArray4 = pointer4.readDataHeaderForPointerArray(-1);
                    arcNotificationData.messages = new ArcNotificationMessage[dataHeaderForPointerArray4.elementsOrVersion];
                    for (int i11 = 0; i11 < dataHeaderForPointerArray4.elementsOrVersion; i11++) {
                        arcNotificationData.messages[i11] = ArcNotificationMessage.decode(GeneratedOutlineSupport.outline2(i11, 8, 8, pointer4, false));
                    }
                }
            }
            return arcNotificationData;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ArcNotificationData deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.key, 8, false);
        encoderAtDataOffset.encode(this.type, 16);
        encoderAtDataOffset.encode(this.priority, 20);
        encoderAtDataOffset.encode(this.message, 24, false);
        encoderAtDataOffset.encode(this.title, 32, false);
        encoderAtDataOffset.encode(this.appDisplayName, 40, true);
        encoderAtDataOffset.encode(this.deprecatedIconData, 48, 1, -1);
        encoderAtDataOffset.encode(this.time, 56);
        encoderAtDataOffset.encode(this.progressCurrent, 64);
        encoderAtDataOffset.encode(this.progressMax, 68);
        ArcNotificationButton[] arcNotificationButtonArr = this.buttons;
        if (arcNotificationButtonArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(arcNotificationButtonArr.length, 72, -1);
            int i = 0;
            while (true) {
                ArcNotificationButton[] arcNotificationButtonArr2 = this.buttons;
                if (i >= arcNotificationButtonArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) arcNotificationButtonArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(72, true);
        }
        encoderAtDataOffset.encode(this.noClear, 80, 0);
        encoderAtDataOffset.encode(this.ongoingEvent, 80, 1);
        encoderAtDataOffset.encode(this.isCustomNotification, 80, 2);
        encoderAtDataOffset.encode(this.indeterminateProgress, 80, 3);
        encoderAtDataOffset.encode(this.isMediaNotification, 80, 4);
        encoderAtDataOffset.encode(this.isActionEnabled, 80, 5);
        encoderAtDataOffset.encode(this.isInlineReplyEnabled, 80, 6);
        encoderAtDataOffset.encode(this.renderOnChrome, 80, 7);
        encoderAtDataOffset.encode(this.snapshotImageScale, 84);
        String[] strArr = this.texts;
        if (strArr != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(strArr.length, 88, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.texts;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray2, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(88, true);
        }
        encoderAtDataOffset.encode((Struct) this.bigPicture, 96, true);
        encoderAtDataOffset.encode((Struct) this.smallIcon, 104, true);
        encoderAtDataOffset.encode((Struct) this.snapshotImage, 112, true);
        encoderAtDataOffset.encode(this.accessibleName, 120, true);
        encoderAtDataOffset.encode(this.expandState, 128);
        encoderAtDataOffset.encode(this.shownContents, 132);
        encoderAtDataOffset.encode(this.remoteInputState, 136);
        encoderAtDataOffset.encode(this.style, 140);
        encoderAtDataOffset.encode((Struct) this.swipeInputRect, 144, true);
        encoderAtDataOffset.encode(this.packageName, 152, true);
        encoderAtDataOffset.encode((Struct) this.flags, 160, true);
        encoderAtDataOffset.encode((Struct) this.snapshotImagePublic, 168, true);
        encoderAtDataOffset.encode(this.groupKey, 176, true);
        encoderAtDataOffset.encode(this.replyButtonIndex, 184);
        ArcNotificationData[] arcNotificationDataArr = this.childrenData;
        if (arcNotificationDataArr != null) {
            Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(arcNotificationDataArr.length, 192, -1);
            int i2 = 0;
            while (true) {
                ArcNotificationData[] arcNotificationDataArr2 = this.childrenData;
                if (i2 >= arcNotificationDataArr2.length) {
                    break;
                }
                encoderEncodePointerArray3.encode((Struct) arcNotificationDataArr2[i2], (i2 * 8) + 8, false);
                i2++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(192, true);
        }
        ArcNotificationMessage[] arcNotificationMessageArr = this.messages;
        if (arcNotificationMessageArr == null) {
            encoderAtDataOffset.encodeNullPointer(200, true);
            return;
        }
        Encoder encoderEncodePointerArray4 = encoderAtDataOffset.encodePointerArray(arcNotificationMessageArr.length, 200, -1);
        int i3 = 0;
        while (true) {
            ArcNotificationMessage[] arcNotificationMessageArr2 = this.messages;
            if (i3 >= arcNotificationMessageArr2.length) {
                return;
            }
            encoderEncodePointerArray4.encode((Struct) arcNotificationMessageArr2[i3], (i3 * 8) + 8, false);
            i3++;
        }
    }

    private ArcNotificationData(int i) {
        super(208, i);
    }

    public static ArcNotificationData deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
