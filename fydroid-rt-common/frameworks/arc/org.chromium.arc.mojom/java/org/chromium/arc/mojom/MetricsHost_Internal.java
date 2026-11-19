package org.chromium.arc.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import org.chromium.arc.mojom.MetricsHost;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojo_base.mojom.TimeDelta;

class MetricsHost_Internal {
    public static final Interface.Manager<MetricsHost, MetricsHost.Proxy> MANAGER = new Interface.Manager<MetricsHost, MetricsHost.Proxy>() { // from class: org.chromium.arc.mojom.MetricsHost_Internal.1
        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MetricsHost[] buildArray(int i) {
            return new MetricsHost[i];
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public MetricsHost.Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            return new Proxy(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public Interface.Stub<MetricsHost> buildStub(Core core, MetricsHost r2) {
            return new Stub(core, r2);
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public String getName() {
            return "arc.mojom.MetricsHost";
        }

        @Override // org.chromium.mojo.bindings.Interface.Manager
        public int getVersion() {
            return 34;
        }
    };

    static final class MetricsHostReportAnrParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public Anr anr;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportAnrParams() {
            super(16, 0);
        }

        public static MetricsHostReportAnrParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportAnrParams metricsHostReportAnrParams = new MetricsHostReportAnrParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportAnrParams.anr = Anr.decode(decoder.readPointer(8, false));
                return metricsHostReportAnrParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.anr, 8, false);
        }

        private MetricsHostReportAnrParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportApkCacheHitParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public boolean hit;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportApkCacheHitParams() {
            super(16, 0);
        }

        public static MetricsHostReportApkCacheHitParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportApkCacheHitParams metricsHostReportApkCacheHitParams = new MetricsHostReportApkCacheHitParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportApkCacheHitParams.hit = decoder.readBoolean(8, 0);
                return metricsHostReportApkCacheHitParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.hit, 8, 0);
        }

        private MetricsHostReportApkCacheHitParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportAppKillParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public AppKill appKill;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportAppKillParams() {
            super(16, 0);
        }

        public static MetricsHostReportAppKillParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportAppKillParams metricsHostReportAppKillParams = new MetricsHostReportAppKillParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportAppKillParams.appKill = AppKill.decode(decoder.readPointer(8, false));
                return metricsHostReportAppKillParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.appKill, 8, false);
        }

        private MetricsHostReportAppKillParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportAppPrimaryAbiParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int abi;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportAppPrimaryAbiParams() {
            super(16, 0);
        }

        public static MetricsHostReportAppPrimaryAbiParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportAppPrimaryAbiParams metricsHostReportAppPrimaryAbiParams = new MetricsHostReportAppPrimaryAbiParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportAppPrimaryAbiParams.abi = i;
                AppPrimaryAbi.validate(i);
                return metricsHostReportAppPrimaryAbiParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.abi, 8);
        }

        private MetricsHostReportAppPrimaryAbiParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportArcCorePriAbiMigBootTimeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeDelta duration;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportArcCorePriAbiMigBootTimeParams() {
            super(16, 0);
        }

        public static MetricsHostReportArcCorePriAbiMigBootTimeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportArcCorePriAbiMigBootTimeParams metricsHostReportArcCorePriAbiMigBootTimeParams = new MetricsHostReportArcCorePriAbiMigBootTimeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportArcCorePriAbiMigBootTimeParams.duration = TimeDelta.decode(decoder.readPointer(8, false));
                return metricsHostReportArcCorePriAbiMigBootTimeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.duration, 8, false);
        }

        private MetricsHostReportArcCorePriAbiMigBootTimeParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportArcCorePriAbiMigDowngradeDelayParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeDelta delay;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportArcCorePriAbiMigDowngradeDelayParams() {
            super(16, 0);
        }

        public static MetricsHostReportArcCorePriAbiMigDowngradeDelayParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportArcCorePriAbiMigDowngradeDelayParams metricsHostReportArcCorePriAbiMigDowngradeDelayParams = new MetricsHostReportArcCorePriAbiMigDowngradeDelayParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportArcCorePriAbiMigDowngradeDelayParams.delay = TimeDelta.decode(decoder.readPointer(8, false));
                return metricsHostReportArcCorePriAbiMigDowngradeDelayParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.delay, 8, false);
        }

        private MetricsHostReportArcCorePriAbiMigDowngradeDelayParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportArcCorePriAbiMigEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int event;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportArcCorePriAbiMigEventParams() {
            super(16, 0);
        }

        public static MetricsHostReportArcCorePriAbiMigEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportArcCorePriAbiMigEventParams metricsHostReportArcCorePriAbiMigEventParams = new MetricsHostReportArcCorePriAbiMigEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportArcCorePriAbiMigEventParams.event = i;
                ArcCorePriAbiMigEvent.validate(i);
                return metricsHostReportArcCorePriAbiMigEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.event, 8);
        }

        private MetricsHostReportArcCorePriAbiMigEventParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportArcCorePriAbiMigFailedTriesParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int failedAttempts;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportArcCorePriAbiMigFailedTriesParams() {
            super(16, 0);
        }

        public static MetricsHostReportArcCorePriAbiMigFailedTriesParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportArcCorePriAbiMigFailedTriesParams metricsHostReportArcCorePriAbiMigFailedTriesParams = new MetricsHostReportArcCorePriAbiMigFailedTriesParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportArcCorePriAbiMigFailedTriesParams.failedAttempts = decoder.readInt(8);
                return metricsHostReportArcCorePriAbiMigFailedTriesParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.failedAttempts, 8);
        }

        private MetricsHostReportArcCorePriAbiMigFailedTriesParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportArcKeyMintErrorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportArcKeyMintErrorParams() {
            super(16, 0);
        }

        public static MetricsHostReportArcKeyMintErrorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportArcKeyMintErrorParams metricsHostReportArcKeyMintErrorParams = new MetricsHostReportArcKeyMintErrorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportArcKeyMintErrorParams.error = i;
                ArcKeyMintError.validate(i);
                return metricsHostReportArcKeyMintErrorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private MetricsHostReportArcKeyMintErrorParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportArcNetworkErrorParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int error;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportArcNetworkErrorParams() {
            super(16, 0);
        }

        public static MetricsHostReportArcNetworkErrorParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportArcNetworkErrorParams metricsHostReportArcNetworkErrorParams = new MetricsHostReportArcNetworkErrorParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportArcNetworkErrorParams.error = i;
                ArcNetworkError.validate(i);
                return metricsHostReportArcNetworkErrorParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.error, 8);
        }

        private MetricsHostReportArcNetworkErrorParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportArcNetworkEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int event;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportArcNetworkEventParams() {
            super(16, 0);
        }

        public static MetricsHostReportArcNetworkEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportArcNetworkEventParams metricsHostReportArcNetworkEventParams = new MetricsHostReportArcNetworkEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportArcNetworkEventParams.event = i;
                ArcNetworkEvent.validate(i);
                return metricsHostReportArcNetworkEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.event, 8);
        }

        private MetricsHostReportArcNetworkEventParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportArcSystemHealthUpgradeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeDelta duration;
        public boolean packagesDeleted;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportArcSystemHealthUpgradeParams() {
            super(24, 0);
        }

        public static MetricsHostReportArcSystemHealthUpgradeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportArcSystemHealthUpgradeParams metricsHostReportArcSystemHealthUpgradeParams = new MetricsHostReportArcSystemHealthUpgradeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportArcSystemHealthUpgradeParams.duration = TimeDelta.decode(decoder.readPointer(8, false));
                metricsHostReportArcSystemHealthUpgradeParams.packagesDeleted = decoder.readBoolean(16, 0);
                return metricsHostReportArcSystemHealthUpgradeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode((Struct) this.duration, 8, false);
            encoderAtDataOffset.encode(this.packagesDeleted, 16, 0);
        }

        private MetricsHostReportArcSystemHealthUpgradeParams(int i) {
            super(24, i);
        }
    }

    static final class MetricsHostReportBootProgressParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int bootType;
        public BootProgressEvent[] events;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0), new DataHeader(24, 1)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[1];
        }

        public MetricsHostReportBootProgressParams() {
            super(24, 1);
        }

        public static MetricsHostReportBootProgressParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                int i = decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
                MetricsHostReportBootProgressParams metricsHostReportBootProgressParams = new MetricsHostReportBootProgressParams(i);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                metricsHostReportBootProgressParams.events = new BootProgressEvent[dataHeaderForPointerArray.elementsOrVersion];
                for (int i2 = 0; i2 < dataHeaderForPointerArray.elementsOrVersion; i2++) {
                    metricsHostReportBootProgressParams.events[i2] = BootProgressEvent.decode(GeneratedOutlineSupport.outline2(i2, 8, 8, pointer, false));
                }
                if (i >= 1) {
                    int i3 = decoderOutline3.readInt(16);
                    metricsHostReportBootProgressParams.bootType = i3;
                    BootType.validate(i3);
                }
                return metricsHostReportBootProgressParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            BootProgressEvent[] bootProgressEventArr = this.events;
            if (bootProgressEventArr != null) {
                Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(bootProgressEventArr.length, 8, -1);
                int i = 0;
                while (true) {
                    BootProgressEvent[] bootProgressEventArr2 = this.events;
                    if (i >= bootProgressEventArr2.length) {
                        break;
                    }
                    encoderEncodePointerArray.encode((Struct) bootProgressEventArr2[i], (i * 8) + 8, false);
                    i++;
                }
            } else {
                encoderAtDataOffset.encodeNullPointer(8, false);
            }
            encoderAtDataOffset.encode(this.bootType, 16);
        }

        private MetricsHostReportBootProgressParams(int i) {
            super(24, i);
        }
    }

    static final class MetricsHostReportCompanionLibApiUsageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int apiId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportCompanionLibApiUsageParams() {
            super(16, 0);
        }

        public static MetricsHostReportCompanionLibApiUsageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportCompanionLibApiUsageParams metricsHostReportCompanionLibApiUsageParams = new MetricsHostReportCompanionLibApiUsageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportCompanionLibApiUsageParams.apiId = i;
                CompanionLibApiId.validate(i);
                return metricsHostReportCompanionLibApiUsageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.apiId, 8);
        }

        private MetricsHostReportCompanionLibApiUsageParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportDataRestoreParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public long durationMs;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportDataRestoreParams() {
            super(24, 0);
        }

        public static MetricsHostReportDataRestoreParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportDataRestoreParams metricsHostReportDataRestoreParams = new MetricsHostReportDataRestoreParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportDataRestoreParams.status = i;
                DataRestoreStatus.validate(i);
                metricsHostReportDataRestoreParams.durationMs = decoder.readLong(16);
                return metricsHostReportDataRestoreParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.status, 8);
            encoderAtDataOffset.encode(this.durationMs, 16);
        }

        private MetricsHostReportDataRestoreParams(int i) {
            super(24, i);
        }
    }

    static final class MetricsHostReportDnsQueryResultParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int query;
        public boolean success;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportDnsQueryResultParams() {
            super(16, 0);
        }

        public static MetricsHostReportDnsQueryResultParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportDnsQueryResultParams metricsHostReportDnsQueryResultParams = new MetricsHostReportDnsQueryResultParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportDnsQueryResultParams.query = i;
                ArcDnsQuery.validate(i);
                metricsHostReportDnsQueryResultParams.success = decoder.readBoolean(12, 0);
                return metricsHostReportDnsQueryResultParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.query, 8);
            encoderAtDataOffset.encode(this.success, 12, 0);
        }

        private MetricsHostReportDnsQueryResultParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportDragResizeLatencyParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeDelta[] durations;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportDragResizeLatencyParams() {
            super(16, 0);
        }

        public static MetricsHostReportDragResizeLatencyParams deserialize(Message message) {
            Decoder decoderOutline3 = GeneratedOutlineSupport.outline3(message);
            try {
                MetricsHostReportDragResizeLatencyParams metricsHostReportDragResizeLatencyParams = new MetricsHostReportDragResizeLatencyParams(decoderOutline3.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                Decoder pointer = decoderOutline3.readPointer(8, false);
                DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                metricsHostReportDragResizeLatencyParams.durations = new TimeDelta[dataHeaderForPointerArray.elementsOrVersion];
                for (int i = 0; i < dataHeaderForPointerArray.elementsOrVersion; i++) {
                    metricsHostReportDragResizeLatencyParams.durations[i] = TimeDelta.decode(GeneratedOutlineSupport.outline2(i, 8, 8, pointer, false));
                }
                return metricsHostReportDragResizeLatencyParams;
            } finally {
                decoderOutline3.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            TimeDelta[] timeDeltaArr = this.durations;
            if (timeDeltaArr == null) {
                encoderAtDataOffset.encodeNullPointer(8, false);
                return;
            }
            Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(timeDeltaArr.length, 8, -1);
            int i = 0;
            while (true) {
                TimeDelta[] timeDeltaArr2 = this.durations;
                if (i >= timeDeltaArr2.length) {
                    return;
                }
                encoderEncodePointerArray.encode((Struct) timeDeltaArr2[i], (i * 8) + 8, false);
                i++;
            }
        }

        private MetricsHostReportDragResizeLatencyParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportLowLatencyStylusLibApiUsageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int apiId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportLowLatencyStylusLibApiUsageParams() {
            super(16, 0);
        }

        public static MetricsHostReportLowLatencyStylusLibApiUsageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportLowLatencyStylusLibApiUsageParams metricsHostReportLowLatencyStylusLibApiUsageParams = new MetricsHostReportLowLatencyStylusLibApiUsageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportLowLatencyStylusLibApiUsageParams.apiId = i;
                LowLatencyStylusLibApiId.validate(i);
                return metricsHostReportLowLatencyStylusLibApiUsageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.apiId, 8);
        }

        private MetricsHostReportLowLatencyStylusLibApiUsageParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportLowLatencyStylusLibPredictionTargetParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public LowLatencyStylusLibPredictionTarget predictionTarget;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportLowLatencyStylusLibPredictionTargetParams() {
            super(16, 0);
        }

        public static MetricsHostReportLowLatencyStylusLibPredictionTargetParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportLowLatencyStylusLibPredictionTargetParams metricsHostReportLowLatencyStylusLibPredictionTargetParams = new MetricsHostReportLowLatencyStylusLibPredictionTargetParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportLowLatencyStylusLibPredictionTargetParams.predictionTarget = LowLatencyStylusLibPredictionTarget.decode(decoder.readPointer(8, false));
                return metricsHostReportLowLatencyStylusLibPredictionTargetParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode((Struct) this.predictionTarget, 8, false);
        }

        private MetricsHostReportLowLatencyStylusLibPredictionTargetParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportMainAccountHashMigrationMetricsParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int status;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportMainAccountHashMigrationMetricsParams() {
            super(16, 0);
        }

        public static MetricsHostReportMainAccountHashMigrationMetricsParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportMainAccountHashMigrationMetricsParams metricsHostReportMainAccountHashMigrationMetricsParams = new MetricsHostReportMainAccountHashMigrationMetricsParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportMainAccountHashMigrationMetricsParams.status = i;
                MainAccountHashMigrationStatus.validate(i);
                return metricsHostReportMainAccountHashMigrationMetricsParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8);
        }

        private MetricsHostReportMainAccountHashMigrationMetricsParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportMemoryPressureParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public byte[] psiFileContents;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportMemoryPressureParams() {
            super(16, 0);
        }

        public static MetricsHostReportMemoryPressureParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportMemoryPressureParams metricsHostReportMemoryPressureParams = new MetricsHostReportMemoryPressureParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportMemoryPressureParams.psiFileContents = decoder.readBytes(8, 0, -1);
                return metricsHostReportMemoryPressureParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.psiFileContents, 8, 0, -1);
        }

        private MetricsHostReportMemoryPressureParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportNativeBridgeParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int nativeBridgeType;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportNativeBridgeParams() {
            super(16, 0);
        }

        public static MetricsHostReportNativeBridgeParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportNativeBridgeParams metricsHostReportNativeBridgeParams = new MetricsHostReportNativeBridgeParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportNativeBridgeParams.nativeBridgeType = i;
                NativeBridgeType.validate(i);
                return metricsHostReportNativeBridgeParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.nativeBridgeType, 8);
        }

        private MetricsHostReportNativeBridgeParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportNewQosSocketCountParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int count;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportNewQosSocketCountParams() {
            super(16, 0);
        }

        public static MetricsHostReportNewQosSocketCountParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportNewQosSocketCountParams metricsHostReportNewQosSocketCountParams = new MetricsHostReportNewQosSocketCountParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportNewQosSocketCountParams.count = decoder.readInt(8);
                return metricsHostReportNewQosSocketCountParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.count, 8);
        }

        private MetricsHostReportNewQosSocketCountParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportProvisioningPreSignInParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportProvisioningPreSignInParams() {
            super(8, 0);
        }

        public static MetricsHostReportProvisioningPreSignInParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MetricsHostReportProvisioningPreSignInParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MetricsHostReportProvisioningPreSignInParams(int i) {
            super(8, i);
        }
    }

    static final class MetricsHostReportQosSocketPercentageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int perc;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportQosSocketPercentageParams() {
            super(16, 0);
        }

        public static MetricsHostReportQosSocketPercentageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportQosSocketPercentageParams metricsHostReportQosSocketPercentageParams = new MetricsHostReportQosSocketPercentageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                metricsHostReportQosSocketPercentageParams.perc = decoder.readInt(8);
                return metricsHostReportQosSocketPercentageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.perc, 8);
        }

        private MetricsHostReportQosSocketPercentageParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportVpnServiceBuilderCompatApiUsageParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public int apiId;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(16, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportVpnServiceBuilderCompatApiUsageParams() {
            super(16, 0);
        }

        public static MetricsHostReportVpnServiceBuilderCompatApiUsageParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportVpnServiceBuilderCompatApiUsageParams metricsHostReportVpnServiceBuilderCompatApiUsageParams = new MetricsHostReportVpnServiceBuilderCompatApiUsageParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportVpnServiceBuilderCompatApiUsageParams.apiId = i;
                VpnServiceBuilderCompatApiId.validate(i);
                return metricsHostReportVpnServiceBuilderCompatApiUsageParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.apiId, 8);
        }

        private MetricsHostReportVpnServiceBuilderCompatApiUsageParams(int i) {
            super(16, i);
        }
    }

    static final class MetricsHostReportWaylandLateTimingEventParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;
        public TimeDelta duration;
        public int event;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(24, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportWaylandLateTimingEventParams() {
            super(24, 0);
        }

        public static MetricsHostReportWaylandLateTimingEventParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                MetricsHostReportWaylandLateTimingEventParams metricsHostReportWaylandLateTimingEventParams = new MetricsHostReportWaylandLateTimingEventParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
                int i = decoder.readInt(8);
                metricsHostReportWaylandLateTimingEventParams.event = i;
                WaylandTimingEvent.validate(i);
                metricsHostReportWaylandLateTimingEventParams.duration = TimeDelta.decode(decoder.readPointer(16, false));
                return metricsHostReportWaylandLateTimingEventParams;
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
            encoderAtDataOffset.encode(this.event, 8);
            encoderAtDataOffset.encode((Struct) this.duration, 16, false);
        }

        private MetricsHostReportWaylandLateTimingEventParams(int i) {
            super(24, i);
        }
    }

    static final class MetricsHostReportWebViewProcessStartedParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO;
        private static final DataHeader[] VERSION_ARRAY;

        static {
            DataHeader[] dataHeaderArr = {new DataHeader(8, 0)};
            VERSION_ARRAY = dataHeaderArr;
            DEFAULT_STRUCT_INFO = dataHeaderArr[0];
        }

        public MetricsHostReportWebViewProcessStartedParams() {
            super(8, 0);
        }

        public static MetricsHostReportWebViewProcessStartedParams deserialize(Message message) {
            Decoder decoder = new Decoder(message);
            decoder.increaseStackDepth();
            try {
                return new MetricsHostReportWebViewProcessStartedParams(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            } finally {
                decoder.decreaseStackDepth();
            }
        }

        @Override // org.chromium.mojo.bindings.Struct
        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        }

        private MetricsHostReportWebViewProcessStartedParams(int i) {
            super(8, i);
        }
    }

    static final class Proxy extends Interface.AbstractProxy implements MetricsHost.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder) {
            super(core, messageReceiverWithResponder);
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportAnr(Anr anr) {
            MetricsHostReportAnrParams metricsHostReportAnrParams = new MetricsHostReportAnrParams();
            metricsHostReportAnrParams.anr = anr;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportAnrParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(10)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportApkCacheHit(boolean z) {
            MetricsHostReportApkCacheHitParams metricsHostReportApkCacheHitParams = new MetricsHostReportApkCacheHitParams();
            metricsHostReportApkCacheHitParams.hit = z;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportApkCacheHitParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(38)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportAppKill(AppKill appKill) {
            MetricsHostReportAppKillParams metricsHostReportAppKillParams = new MetricsHostReportAppKillParams();
            metricsHostReportAppKillParams.appKill = appKill;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportAppKillParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(3)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportAppPrimaryAbi(int i) {
            MetricsHostReportAppPrimaryAbiParams metricsHostReportAppPrimaryAbiParams = new MetricsHostReportAppPrimaryAbiParams();
            metricsHostReportAppPrimaryAbiParams.abi = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportAppPrimaryAbiParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(21)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportArcCorePriAbiMigBootTime(TimeDelta timeDelta) {
            MetricsHostReportArcCorePriAbiMigBootTimeParams metricsHostReportArcCorePriAbiMigBootTimeParams = new MetricsHostReportArcCorePriAbiMigBootTimeParams();
            metricsHostReportArcCorePriAbiMigBootTimeParams.duration = timeDelta;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportArcCorePriAbiMigBootTimeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(7)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportArcCorePriAbiMigDowngradeDelay(TimeDelta timeDelta) {
            MetricsHostReportArcCorePriAbiMigDowngradeDelayParams metricsHostReportArcCorePriAbiMigDowngradeDelayParams = new MetricsHostReportArcCorePriAbiMigDowngradeDelayParams();
            metricsHostReportArcCorePriAbiMigDowngradeDelayParams.delay = timeDelta;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportArcCorePriAbiMigDowngradeDelayParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(6)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportArcCorePriAbiMigEvent(int i) {
            MetricsHostReportArcCorePriAbiMigEventParams metricsHostReportArcCorePriAbiMigEventParams = new MetricsHostReportArcCorePriAbiMigEventParams();
            metricsHostReportArcCorePriAbiMigEventParams.event = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportArcCorePriAbiMigEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(4)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportArcCorePriAbiMigFailedTries(int i) {
            MetricsHostReportArcCorePriAbiMigFailedTriesParams metricsHostReportArcCorePriAbiMigFailedTriesParams = new MetricsHostReportArcCorePriAbiMigFailedTriesParams();
            metricsHostReportArcCorePriAbiMigFailedTriesParams.failedAttempts = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportArcCorePriAbiMigFailedTriesParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(5)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportArcKeyMintError(int i) {
            MetricsHostReportArcKeyMintErrorParams metricsHostReportArcKeyMintErrorParams = new MetricsHostReportArcKeyMintErrorParams();
            metricsHostReportArcKeyMintErrorParams.error = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportArcKeyMintErrorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(35)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportArcNetworkError(int i) {
            MetricsHostReportArcNetworkErrorParams metricsHostReportArcNetworkErrorParams = new MetricsHostReportArcNetworkErrorParams();
            metricsHostReportArcNetworkErrorParams.error = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportArcNetworkErrorParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(20)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportArcNetworkEvent(int i) {
            MetricsHostReportArcNetworkEventParams metricsHostReportArcNetworkEventParams = new MetricsHostReportArcNetworkEventParams();
            metricsHostReportArcNetworkEventParams.event = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportArcNetworkEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(19)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportArcSystemHealthUpgrade(TimeDelta timeDelta, boolean z) {
            MetricsHostReportArcSystemHealthUpgradeParams metricsHostReportArcSystemHealthUpgradeParams = new MetricsHostReportArcSystemHealthUpgradeParams();
            metricsHostReportArcSystemHealthUpgradeParams.duration = timeDelta;
            metricsHostReportArcSystemHealthUpgradeParams.packagesDeleted = z;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportArcSystemHealthUpgradeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(11)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportBootProgress(BootProgressEvent[] bootProgressEventArr, int i) {
            MetricsHostReportBootProgressParams metricsHostReportBootProgressParams = new MetricsHostReportBootProgressParams();
            metricsHostReportBootProgressParams.events = bootProgressEventArr;
            metricsHostReportBootProgressParams.bootType = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportBootProgressParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportCompanionLibApiUsage(int i) {
            MetricsHostReportCompanionLibApiUsageParams metricsHostReportCompanionLibApiUsageParams = new MetricsHostReportCompanionLibApiUsageParams();
            metricsHostReportCompanionLibApiUsageParams.apiId = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportCompanionLibApiUsageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(2)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportDataRestore(int i, long j) {
            MetricsHostReportDataRestoreParams metricsHostReportDataRestoreParams = new MetricsHostReportDataRestoreParams();
            metricsHostReportDataRestoreParams.status = i;
            metricsHostReportDataRestoreParams.durationMs = j;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportDataRestoreParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(22)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportDnsQueryResult(int i, boolean z) {
            MetricsHostReportDnsQueryResultParams metricsHostReportDnsQueryResultParams = new MetricsHostReportDnsQueryResultParams();
            metricsHostReportDnsQueryResultParams.query = i;
            metricsHostReportDnsQueryResultParams.success = z;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportDnsQueryResultParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(16)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportDragResizeLatency(TimeDelta[] timeDeltaArr) {
            MetricsHostReportDragResizeLatencyParams metricsHostReportDragResizeLatencyParams = new MetricsHostReportDragResizeLatencyParams();
            metricsHostReportDragResizeLatencyParams.durations = timeDeltaArr;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportDragResizeLatencyParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(36)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportLowLatencyStylusLibApiUsage(int i) {
            MetricsHostReportLowLatencyStylusLibApiUsageParams metricsHostReportLowLatencyStylusLibApiUsageParams = new MetricsHostReportLowLatencyStylusLibApiUsageParams();
            metricsHostReportLowLatencyStylusLibApiUsageParams.apiId = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportLowLatencyStylusLibApiUsageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(12)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportLowLatencyStylusLibPredictionTarget(LowLatencyStylusLibPredictionTarget lowLatencyStylusLibPredictionTarget) {
            MetricsHostReportLowLatencyStylusLibPredictionTargetParams metricsHostReportLowLatencyStylusLibPredictionTargetParams = new MetricsHostReportLowLatencyStylusLibPredictionTargetParams();
            metricsHostReportLowLatencyStylusLibPredictionTargetParams.predictionTarget = lowLatencyStylusLibPredictionTarget;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportLowLatencyStylusLibPredictionTargetParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(13)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportMainAccountHashMigrationMetrics(int i) {
            MetricsHostReportMainAccountHashMigrationMetricsParams metricsHostReportMainAccountHashMigrationMetricsParams = new MetricsHostReportMainAccountHashMigrationMetricsParams();
            metricsHostReportMainAccountHashMigrationMetricsParams.status = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportMainAccountHashMigrationMetricsParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(17)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportMemoryPressure(byte[] bArr) {
            MetricsHostReportMemoryPressureParams metricsHostReportMemoryPressureParams = new MetricsHostReportMemoryPressureParams();
            metricsHostReportMemoryPressureParams.psiFileContents = bArr;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportMemoryPressureParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(23)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportNativeBridge(int i) {
            MetricsHostReportNativeBridgeParams metricsHostReportNativeBridgeParams = new MetricsHostReportNativeBridgeParams();
            metricsHostReportNativeBridgeParams.nativeBridgeType = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportNativeBridgeParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportNewQosSocketCount(int i) {
            MetricsHostReportNewQosSocketCountParams metricsHostReportNewQosSocketCountParams = new MetricsHostReportNewQosSocketCountParams();
            metricsHostReportNewQosSocketCountParams.count = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportNewQosSocketCountParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(33)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportProvisioningPreSignIn() {
            getProxyHandler().getMessageReceiver().accept(new MetricsHostReportProvisioningPreSignInParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(24)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportQosSocketPercentage(int i) {
            MetricsHostReportQosSocketPercentageParams metricsHostReportQosSocketPercentageParams = new MetricsHostReportQosSocketPercentageParams();
            metricsHostReportQosSocketPercentageParams.perc = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportQosSocketPercentageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(34)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportVpnServiceBuilderCompatApiUsage(int i) {
            MetricsHostReportVpnServiceBuilderCompatApiUsageParams metricsHostReportVpnServiceBuilderCompatApiUsageParams = new MetricsHostReportVpnServiceBuilderCompatApiUsageParams();
            metricsHostReportVpnServiceBuilderCompatApiUsageParams.apiId = i;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportVpnServiceBuilderCompatApiUsageParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(32)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportWaylandLateTimingEvent(int i, TimeDelta timeDelta) {
            MetricsHostReportWaylandLateTimingEventParams metricsHostReportWaylandLateTimingEventParams = new MetricsHostReportWaylandLateTimingEventParams();
            metricsHostReportWaylandLateTimingEventParams.event = i;
            metricsHostReportWaylandLateTimingEventParams.duration = timeDelta;
            getProxyHandler().getMessageReceiver().accept(metricsHostReportWaylandLateTimingEventParams.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(30)));
        }

        @Override // org.chromium.arc.mojom.MetricsHost
        public void reportWebViewProcessStarted() {
            getProxyHandler().getMessageReceiver().accept(new MetricsHostReportWebViewProcessStartedParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(31)));
        }
    }

    static final class Stub extends Interface.Stub<MetricsHost> {
        Stub(Core core, MetricsHost metricsHost) {
            super(core, metricsHost);
        }

        @Override // org.chromium.mojo.bindings.MessageReceiver
        public boolean accept(Message message) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (!header.validateHeader(0)) {
                    return false;
                }
                switch (header.getType()) {
                    case 0:
                        MetricsHostReportBootProgressParams metricsHostReportBootProgressParamsDeserialize = MetricsHostReportBootProgressParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reportBootProgress(metricsHostReportBootProgressParamsDeserialize.events, metricsHostReportBootProgressParamsDeserialize.bootType);
                        return true;
                    case 1:
                        getImpl().reportNativeBridge(MetricsHostReportNativeBridgeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).nativeBridgeType);
                        return true;
                    case 2:
                        getImpl().reportCompanionLibApiUsage(MetricsHostReportCompanionLibApiUsageParams.deserialize(serviceMessageAsServiceMessage.getPayload()).apiId);
                        return true;
                    case 3:
                        getImpl().reportAppKill(MetricsHostReportAppKillParams.deserialize(serviceMessageAsServiceMessage.getPayload()).appKill);
                        return true;
                    case 4:
                        getImpl().reportArcCorePriAbiMigEvent(MetricsHostReportArcCorePriAbiMigEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).event);
                        return true;
                    case 5:
                        getImpl().reportArcCorePriAbiMigFailedTries(MetricsHostReportArcCorePriAbiMigFailedTriesParams.deserialize(serviceMessageAsServiceMessage.getPayload()).failedAttempts);
                        return true;
                    case 6:
                        getImpl().reportArcCorePriAbiMigDowngradeDelay(MetricsHostReportArcCorePriAbiMigDowngradeDelayParams.deserialize(serviceMessageAsServiceMessage.getPayload()).delay);
                        return true;
                    case 7:
                        getImpl().reportArcCorePriAbiMigBootTime(MetricsHostReportArcCorePriAbiMigBootTimeParams.deserialize(serviceMessageAsServiceMessage.getPayload()).duration);
                        return true;
                    case 10:
                        getImpl().reportAnr(MetricsHostReportAnrParams.deserialize(serviceMessageAsServiceMessage.getPayload()).anr);
                        return true;
                    case 11:
                        MetricsHostReportArcSystemHealthUpgradeParams metricsHostReportArcSystemHealthUpgradeParamsDeserialize = MetricsHostReportArcSystemHealthUpgradeParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reportArcSystemHealthUpgrade(metricsHostReportArcSystemHealthUpgradeParamsDeserialize.duration, metricsHostReportArcSystemHealthUpgradeParamsDeserialize.packagesDeleted);
                        return true;
                    case 12:
                        getImpl().reportLowLatencyStylusLibApiUsage(MetricsHostReportLowLatencyStylusLibApiUsageParams.deserialize(serviceMessageAsServiceMessage.getPayload()).apiId);
                        return true;
                    case 13:
                        getImpl().reportLowLatencyStylusLibPredictionTarget(MetricsHostReportLowLatencyStylusLibPredictionTargetParams.deserialize(serviceMessageAsServiceMessage.getPayload()).predictionTarget);
                        return true;
                    case 16:
                        MetricsHostReportDnsQueryResultParams metricsHostReportDnsQueryResultParamsDeserialize = MetricsHostReportDnsQueryResultParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reportDnsQueryResult(metricsHostReportDnsQueryResultParamsDeserialize.query, metricsHostReportDnsQueryResultParamsDeserialize.success);
                        return true;
                    case 17:
                        getImpl().reportMainAccountHashMigrationMetrics(MetricsHostReportMainAccountHashMigrationMetricsParams.deserialize(serviceMessageAsServiceMessage.getPayload()).status);
                        return true;
                    case 19:
                        getImpl().reportArcNetworkEvent(MetricsHostReportArcNetworkEventParams.deserialize(serviceMessageAsServiceMessage.getPayload()).event);
                        return true;
                    case 20:
                        getImpl().reportArcNetworkError(MetricsHostReportArcNetworkErrorParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error);
                        return true;
                    case 21:
                        getImpl().reportAppPrimaryAbi(MetricsHostReportAppPrimaryAbiParams.deserialize(serviceMessageAsServiceMessage.getPayload()).abi);
                        return true;
                    case 22:
                        MetricsHostReportDataRestoreParams metricsHostReportDataRestoreParamsDeserialize = MetricsHostReportDataRestoreParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reportDataRestore(metricsHostReportDataRestoreParamsDeserialize.status, metricsHostReportDataRestoreParamsDeserialize.durationMs);
                        return true;
                    case 23:
                        getImpl().reportMemoryPressure(MetricsHostReportMemoryPressureParams.deserialize(serviceMessageAsServiceMessage.getPayload()).psiFileContents);
                        return true;
                    case 24:
                        MetricsHostReportProvisioningPreSignInParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reportProvisioningPreSignIn();
                        return true;
                    case 30:
                        MetricsHostReportWaylandLateTimingEventParams metricsHostReportWaylandLateTimingEventParamsDeserialize = MetricsHostReportWaylandLateTimingEventParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reportWaylandLateTimingEvent(metricsHostReportWaylandLateTimingEventParamsDeserialize.event, metricsHostReportWaylandLateTimingEventParamsDeserialize.duration);
                        return true;
                    case 31:
                        MetricsHostReportWebViewProcessStartedParams.deserialize(serviceMessageAsServiceMessage.getPayload());
                        getImpl().reportWebViewProcessStarted();
                        return true;
                    case 32:
                        getImpl().reportVpnServiceBuilderCompatApiUsage(MetricsHostReportVpnServiceBuilderCompatApiUsageParams.deserialize(serviceMessageAsServiceMessage.getPayload()).apiId);
                        return true;
                    case 33:
                        getImpl().reportNewQosSocketCount(MetricsHostReportNewQosSocketCountParams.deserialize(serviceMessageAsServiceMessage.getPayload()).count);
                        return true;
                    case 34:
                        getImpl().reportQosSocketPercentage(MetricsHostReportQosSocketPercentageParams.deserialize(serviceMessageAsServiceMessage.getPayload()).perc);
                        return true;
                    case 35:
                        getImpl().reportArcKeyMintError(MetricsHostReportArcKeyMintErrorParams.deserialize(serviceMessageAsServiceMessage.getPayload()).error);
                        return true;
                    case 36:
                        getImpl().reportDragResizeLatency(MetricsHostReportDragResizeLatencyParams.deserialize(serviceMessageAsServiceMessage.getPayload()).durations);
                        return true;
                    case 38:
                        getImpl().reportApkCacheHit(MetricsHostReportApkCacheHitParams.deserialize(serviceMessageAsServiceMessage.getPayload()).hit);
                        return true;
                }
                    return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }

        @Override // org.chromium.mojo.bindings.MessageReceiverWithResponder
        public boolean acceptWithResponder(Message message, MessageReceiver messageReceiver) {
            try {
                ServiceMessage serviceMessageAsServiceMessage = message.asServiceMessage();
                MessageHeader header = serviceMessageAsServiceMessage.getHeader();
                if (header.validateHeader(1) && header.getType() == -1) {
                    return InterfaceControlMessagesHelper.handleRun(getCore(), MetricsHost_Internal.MANAGER, serviceMessageAsServiceMessage, messageReceiver);
                }
                return false;
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }
}
