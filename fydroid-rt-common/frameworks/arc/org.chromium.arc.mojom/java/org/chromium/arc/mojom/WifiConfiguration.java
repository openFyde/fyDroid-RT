package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.bindings.Union;

public final class WifiConfiguration extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String bssid;
    public String[] bssidAllowlist;
    public NetworkDetails details;
    public String[] dnsServers;
    public String[] domains;
    public EapCredentials eap;
    public int frequency;
    public String guid;
    public String hexssid;
    public ArcProxyInfo httpProxy;
    public int meteredOverride;
    public String security;
    public int signalStrength;
    public String ssid;
    public StaticIpv4Configuration staticIpv4Config;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(40, 0), new DataHeader(48, 1), new DataHeader(72, 2), new DataHeader(80, 16), new DataHeader(128, 20), new DataHeader(136, 26)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[5];
    }

    public WifiConfiguration() {
        super(136, 26);
    }

    public static WifiConfiguration decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            WifiConfiguration wifiConfiguration = new WifiConfiguration(i);
            wifiConfiguration.ssid = decoder.readString(8, false);
            wifiConfiguration.frequency = decoder.readInt(16);
            wifiConfiguration.signalStrength = decoder.readInt(20);
            wifiConfiguration.bssid = decoder.readString(24, false);
            wifiConfiguration.security = decoder.readString(32, false);
            if (i >= 1) {
                wifiConfiguration.guid = decoder.readString(40, true);
            }
            if (i >= 2) {
                wifiConfiguration.hexssid = decoder.readString(48, true);
                wifiConfiguration.details = NetworkDetails.decode(decoder, 56);
            }
            if (i >= 16) {
                wifiConfiguration.eap = EapCredentials.decode(decoder.readPointer(72, true));
            }
            if (i >= 20) {
                int i2 = decoder.readInt(80);
                wifiConfiguration.meteredOverride = i2;
                MeteredOverride.validate(i2);
                wifiConfiguration.httpProxy = ArcProxyInfo.decode(decoder, 88);
                wifiConfiguration.staticIpv4Config = StaticIpv4Configuration.decode(decoder.readPointer(104, true));
                Decoder pointer = decoder.readPointer(112, true);
                if (pointer == null) {
                    wifiConfiguration.domains = null;
                } else {
                    DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                    wifiConfiguration.domains = new String[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                        wifiConfiguration.domains[i3] = GeneratedOutlineSupport.outline1(i3, 8, 8, pointer, false);
                    }
                }
                Decoder pointer2 = decoder.readPointer(120, true);
                if (pointer2 == null) {
                    wifiConfiguration.dnsServers = null;
                } else {
                    DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                    wifiConfiguration.dnsServers = new String[dataHeaderForPointerArray2.elementsOrVersion];
                    for (int i4 = 0; i4 < dataHeaderForPointerArray2.elementsOrVersion; i4++) {
                        wifiConfiguration.dnsServers[i4] = GeneratedOutlineSupport.outline1(i4, 8, 8, pointer2, false);
                    }
                }
            }
            if (i >= 26) {
                Decoder pointer3 = decoder.readPointer(128, true);
                if (pointer3 == null) {
                    wifiConfiguration.bssidAllowlist = null;
                } else {
                    DataHeader dataHeaderForPointerArray3 = pointer3.readDataHeaderForPointerArray(-1);
                    wifiConfiguration.bssidAllowlist = new String[dataHeaderForPointerArray3.elementsOrVersion];
                    for (int i5 = 0; i5 < dataHeaderForPointerArray3.elementsOrVersion; i5++) {
                        wifiConfiguration.bssidAllowlist[i5] = GeneratedOutlineSupport.outline1(i5, 8, 8, pointer3, false);
                    }
                }
            }
            return wifiConfiguration;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static WifiConfiguration deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.ssid, 8, false);
        encoderAtDataOffset.encode(this.frequency, 16);
        encoderAtDataOffset.encode(this.signalStrength, 20);
        encoderAtDataOffset.encode(this.bssid, 24, false);
        encoderAtDataOffset.encode(this.security, 32, false);
        encoderAtDataOffset.encode(this.guid, 40, true);
        encoderAtDataOffset.encode(this.hexssid, 48, true);
        encoderAtDataOffset.encode((Union) this.details, 56, true);
        encoderAtDataOffset.encode((Struct) this.eap, 72, true);
        encoderAtDataOffset.encode(this.meteredOverride, 80);
        encoderAtDataOffset.encode((Union) this.httpProxy, 88, true);
        encoderAtDataOffset.encode((Struct) this.staticIpv4Config, 104, true);
        String[] strArr = this.domains;
        if (strArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(strArr.length, 112, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.domains;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(112, true);
        }
        String[] strArr3 = this.dnsServers;
        if (strArr3 != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(strArr3.length, 120, -1);
            int iOutline02 = 0;
            while (true) {
                String[] strArr4 = this.dnsServers;
                if (iOutline02 >= strArr4.length) {
                    break;
                }
                iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray2, strArr4[iOutline02], false, iOutline02, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(120, true);
        }
        String[] strArr5 = this.bssidAllowlist;
        if (strArr5 == null) {
            encoderAtDataOffset.encodeNullPointer(128, true);
            return;
        }
        Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(strArr5.length, 128, -1);
        int iOutline03 = 0;
        while (true) {
            String[] strArr6 = this.bssidAllowlist;
            if (iOutline03 >= strArr6.length) {
                return;
            }
            iOutline03 = GeneratedOutlineSupport.outline0(iOutline03, 8, 8, encoderEncodePointerArray3, strArr6[iOutline03], false, iOutline03, 1);
        }
    }

    private WifiConfiguration(int i) {
        super(136, i);
    }

    public static WifiConfiguration deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
