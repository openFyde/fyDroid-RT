package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class NetworkConfiguration extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String arcIpv4Address;
    public String arcIpv4Gateway;
    public int arcIpv4PrefixLength;
    public String arcNetworkInterface;
    public int connectionState;
    public IpConfiguration[] deprecatedIpConfigs;
    public String deprecatedMacAddress;
    public int deprecatedTetheringClientState;
    public String[] dnsProxyAddresses;
    public String[] excludeRoutes;
    public String guid;
    public String[] hostDnsAddresses;
    public String hostIpv4Address;
    public String hostIpv4Gateway;
    public int hostIpv4PrefixLength;
    public String hostIpv6Gateway;
    public String[] hostIpv6GlobalAddresses;
    public int hostIpv6PrefixLength;
    public int hostMtu;
    public String[] hostSearchDomains;
    public String[] includeRoutes;
    public boolean isDefaultNetwork;
    public boolean isMetered;
    public LinkSpeed linkSpeed;
    public String networkInterface;
    public String serviceName;
    public int type;
    public WiFi wifi;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(48, 0), new DataHeader(56, 8), new DataHeader(64, 10), new DataHeader(64, 11), new DataHeader(72, 12), new DataHeader(160, 13), new DataHeader(160, 14), new DataHeader(176, 15), new DataHeader(184, 19), new DataHeader(192, 28)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[9];
    }

    public NetworkConfiguration() {
        super(192, 28);
    }

    public static NetworkConfiguration decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            NetworkConfiguration networkConfiguration = new NetworkConfiguration(i);
            int i2 = decoder.readInt(8);
            networkConfiguration.connectionState = i2;
            ConnectionStateType.validate(i2);
            int i3 = decoder.readInt(12);
            networkConfiguration.type = i3;
            NetworkType.validate(i3);
            networkConfiguration.guid = decoder.readString(16, false);
            Decoder pointer = decoder.readPointer(24, true);
            if (pointer == null) {
                networkConfiguration.deprecatedIpConfigs = null;
            } else {
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                networkConfiguration.deprecatedIpConfigs = new IpConfiguration[dataHeaderForPointerArray.elementsOrVersion];
                for (int i4 = 0; i4 < dataHeaderForPointerArray.elementsOrVersion; i4++) {
                    networkConfiguration.deprecatedIpConfigs[i4] = IpConfiguration.decode(GeneratedOutlineSupport.outline2(i4, 8, 8, pointer, false));
                }
            }
            networkConfiguration.deprecatedMacAddress = decoder.readString(32, true);
            networkConfiguration.wifi = WiFi.decode(decoder.readPointer(40, true));
            if (i >= 8) {
                int i5 = decoder.readInt(48);
                networkConfiguration.deprecatedTetheringClientState = i5;
                TetheringClientState.validate(i5);
            }
            if (i >= 11) {
                networkConfiguration.isDefaultNetwork = decoder.readBoolean(52, 0);
            }
            if (i >= 14) {
                networkConfiguration.isMetered = decoder.readBoolean(52, 1);
            }
            if (i >= 10) {
                networkConfiguration.networkInterface = decoder.readString(56, true);
            }
            if (i >= 12) {
                networkConfiguration.serviceName = decoder.readString(64, true);
            }
            if (i >= 13) {
                networkConfiguration.hostMtu = decoder.readInt(72);
                networkConfiguration.hostIpv4PrefixLength = decoder.readInt(76);
                networkConfiguration.hostIpv4Address = decoder.readString(80, true);
                networkConfiguration.hostIpv4Gateway = decoder.readString(88, true);
                networkConfiguration.hostIpv6PrefixLength = decoder.readInt(96);
                networkConfiguration.arcIpv4PrefixLength = decoder.readInt(100);
                Decoder pointer2 = decoder.readPointer(104, true);
                if (pointer2 == null) {
                    networkConfiguration.hostIpv6GlobalAddresses = null;
                } else {
                    DataHeader dataHeaderForPointerArray2 = pointer2.readDataHeaderForPointerArray(-1);
                    networkConfiguration.hostIpv6GlobalAddresses = new String[dataHeaderForPointerArray2.elementsOrVersion];
                    for (int i6 = 0; i6 < dataHeaderForPointerArray2.elementsOrVersion; i6++) {
                        networkConfiguration.hostIpv6GlobalAddresses[i6] = GeneratedOutlineSupport.outline1(i6, 8, 8, pointer2, false);
                    }
                }
                networkConfiguration.hostIpv6Gateway = decoder.readString(112, true);
                Decoder pointer3 = decoder.readPointer(120, true);
                if (pointer3 == null) {
                    networkConfiguration.hostDnsAddresses = null;
                } else {
                    DataHeader dataHeaderForPointerArray3 = pointer3.readDataHeaderForPointerArray(-1);
                    networkConfiguration.hostDnsAddresses = new String[dataHeaderForPointerArray3.elementsOrVersion];
                    for (int i7 = 0; i7 < dataHeaderForPointerArray3.elementsOrVersion; i7++) {
                        networkConfiguration.hostDnsAddresses[i7] = GeneratedOutlineSupport.outline1(i7, 8, 8, pointer3, false);
                    }
                }
                Decoder pointer4 = decoder.readPointer(128, true);
                if (pointer4 == null) {
                    networkConfiguration.hostSearchDomains = null;
                } else {
                    DataHeader dataHeaderForPointerArray4 = pointer4.readDataHeaderForPointerArray(-1);
                    networkConfiguration.hostSearchDomains = new String[dataHeaderForPointerArray4.elementsOrVersion];
                    for (int i8 = 0; i8 < dataHeaderForPointerArray4.elementsOrVersion; i8++) {
                        networkConfiguration.hostSearchDomains[i8] = GeneratedOutlineSupport.outline1(i8, 8, 8, pointer4, false);
                    }
                }
                networkConfiguration.arcIpv4Address = decoder.readString(136, true);
                networkConfiguration.arcIpv4Gateway = decoder.readString(144, true);
                networkConfiguration.arcNetworkInterface = decoder.readString(152, true);
            }
            if (i >= 15) {
                Decoder pointer5 = decoder.readPointer(160, true);
                if (pointer5 == null) {
                    networkConfiguration.includeRoutes = null;
                } else {
                    DataHeader dataHeaderForPointerArray5 = pointer5.readDataHeaderForPointerArray(-1);
                    networkConfiguration.includeRoutes = new String[dataHeaderForPointerArray5.elementsOrVersion];
                    for (int i9 = 0; i9 < dataHeaderForPointerArray5.elementsOrVersion; i9++) {
                        networkConfiguration.includeRoutes[i9] = GeneratedOutlineSupport.outline1(i9, 8, 8, pointer5, false);
                    }
                }
                Decoder pointer6 = decoder.readPointer(168, true);
                if (pointer6 == null) {
                    networkConfiguration.excludeRoutes = null;
                } else {
                    DataHeader dataHeaderForPointerArray6 = pointer6.readDataHeaderForPointerArray(-1);
                    networkConfiguration.excludeRoutes = new String[dataHeaderForPointerArray6.elementsOrVersion];
                    for (int i10 = 0; i10 < dataHeaderForPointerArray6.elementsOrVersion; i10++) {
                        networkConfiguration.excludeRoutes[i10] = GeneratedOutlineSupport.outline1(i10, 8, 8, pointer6, false);
                    }
                }
            }
            if (i >= 19) {
                Decoder pointer7 = decoder.readPointer(176, true);
                if (pointer7 == null) {
                    networkConfiguration.dnsProxyAddresses = null;
                } else {
                    DataHeader dataHeaderForPointerArray7 = pointer7.readDataHeaderForPointerArray(-1);
                    networkConfiguration.dnsProxyAddresses = new String[dataHeaderForPointerArray7.elementsOrVersion];
                    for (int i11 = 0; i11 < dataHeaderForPointerArray7.elementsOrVersion; i11++) {
                        networkConfiguration.dnsProxyAddresses[i11] = GeneratedOutlineSupport.outline1(i11, 8, 8, pointer7, false);
                    }
                }
            }
            if (i >= 28) {
                networkConfiguration.linkSpeed = LinkSpeed.decode(decoder.readPointer(184, true));
            }
            return networkConfiguration;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static NetworkConfiguration deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.connectionState, 8);
        encoderAtDataOffset.encode(this.type, 12);
        encoderAtDataOffset.encode(this.guid, 16, false);
        IpConfiguration[] ipConfigurationArr = this.deprecatedIpConfigs;
        if (ipConfigurationArr != null) {
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(ipConfigurationArr.length, 24, -1);
            int i = 0;
            while (true) {
                IpConfiguration[] ipConfigurationArr2 = this.deprecatedIpConfigs;
                if (i >= ipConfigurationArr2.length) {
                    break;
                }
                encoderEncodePointerArray.encode((Struct) ipConfigurationArr2[i], (i * 8) + 8, false);
                i++;
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(24, true);
        }
        encoderAtDataOffset.encode(this.deprecatedMacAddress, 32, true);
        encoderAtDataOffset.encode((Struct) this.wifi, 40, true);
        encoderAtDataOffset.encode(this.deprecatedTetheringClientState, 48);
        encoderAtDataOffset.encode(this.isDefaultNetwork, 52, 0);
        encoderAtDataOffset.encode(this.isMetered, 52, 1);
        encoderAtDataOffset.encode(this.networkInterface, 56, true);
        encoderAtDataOffset.encode(this.serviceName, 64, true);
        encoderAtDataOffset.encode(this.hostMtu, 72);
        encoderAtDataOffset.encode(this.hostIpv4PrefixLength, 76);
        encoderAtDataOffset.encode(this.hostIpv4Address, 80, true);
        encoderAtDataOffset.encode(this.hostIpv4Gateway, 88, true);
        encoderAtDataOffset.encode(this.hostIpv6PrefixLength, 96);
        encoderAtDataOffset.encode(this.arcIpv4PrefixLength, 100);
        String[] strArr = this.hostIpv6GlobalAddresses;
        if (strArr != null) {
            Encoder encoderEncodePointerArray2 = encoderAtDataOffset.encodePointerArray(strArr.length, 104, -1);
            int iOutline0 = 0;
            while (true) {
                String[] strArr2 = this.hostIpv6GlobalAddresses;
                if (iOutline0 >= strArr2.length) {
                    break;
                }
                iOutline0 = GeneratedOutlineSupport.outline0(iOutline0, 8, 8, encoderEncodePointerArray2, strArr2[iOutline0], false, iOutline0, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(104, true);
        }
        encoderAtDataOffset.encode(this.hostIpv6Gateway, 112, true);
        String[] strArr3 = this.hostDnsAddresses;
        if (strArr3 != null) {
            Encoder encoderEncodePointerArray3 = encoderAtDataOffset.encodePointerArray(strArr3.length, 120, -1);
            int iOutline02 = 0;
            while (true) {
                String[] strArr4 = this.hostDnsAddresses;
                if (iOutline02 >= strArr4.length) {
                    break;
                }
                iOutline02 = GeneratedOutlineSupport.outline0(iOutline02, 8, 8, encoderEncodePointerArray3, strArr4[iOutline02], false, iOutline02, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(120, true);
        }
        String[] strArr5 = this.hostSearchDomains;
        if (strArr5 != null) {
            Encoder encoderEncodePointerArray4 = encoderAtDataOffset.encodePointerArray(strArr5.length, 128, -1);
            int iOutline03 = 0;
            while (true) {
                String[] strArr6 = this.hostSearchDomains;
                if (iOutline03 >= strArr6.length) {
                    break;
                }
                iOutline03 = GeneratedOutlineSupport.outline0(iOutline03, 8, 8, encoderEncodePointerArray4, strArr6[iOutline03], false, iOutline03, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(128, true);
        }
        encoderAtDataOffset.encode(this.arcIpv4Address, 136, true);
        encoderAtDataOffset.encode(this.arcIpv4Gateway, 144, true);
        encoderAtDataOffset.encode(this.arcNetworkInterface, 152, true);
        String[] strArr7 = this.includeRoutes;
        if (strArr7 != null) {
            Encoder encoderEncodePointerArray5 = encoderAtDataOffset.encodePointerArray(strArr7.length, 160, -1);
            int iOutline04 = 0;
            while (true) {
                String[] strArr8 = this.includeRoutes;
                if (iOutline04 >= strArr8.length) {
                    break;
                }
                iOutline04 = GeneratedOutlineSupport.outline0(iOutline04, 8, 8, encoderEncodePointerArray5, strArr8[iOutline04], false, iOutline04, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(160, true);
        }
        String[] strArr9 = this.excludeRoutes;
        if (strArr9 != null) {
            Encoder encoderEncodePointerArray6 = encoderAtDataOffset.encodePointerArray(strArr9.length, 168, -1);
            int iOutline05 = 0;
            while (true) {
                String[] strArr10 = this.excludeRoutes;
                if (iOutline05 >= strArr10.length) {
                    break;
                }
                iOutline05 = GeneratedOutlineSupport.outline0(iOutline05, 8, 8, encoderEncodePointerArray6, strArr10[iOutline05], false, iOutline05, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(168, true);
        }
        String[] strArr11 = this.dnsProxyAddresses;
        if (strArr11 != null) {
            Encoder encoderEncodePointerArray7 = encoderAtDataOffset.encodePointerArray(strArr11.length, 176, -1);
            int iOutline06 = 0;
            while (true) {
                String[] strArr12 = this.dnsProxyAddresses;
                if (iOutline06 >= strArr12.length) {
                    break;
                }
                iOutline06 = GeneratedOutlineSupport.outline0(iOutline06, 8, 8, encoderEncodePointerArray7, strArr12[iOutline06], false, iOutline06, 1);
            }
        } else {
            encoderAtDataOffset.encodeNullPointer(176, true);
        }
        encoderAtDataOffset.encode((Struct) this.linkSpeed, 184, true);
    }

    private NetworkConfiguration(int i) {
        super(192, i);
    }

    public static NetworkConfiguration deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
