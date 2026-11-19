package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface OemCryptoService extends Interface {
    public static final Interface.Manager<OemCryptoService, Proxy> MANAGER = OemCryptoService_Internal.MANAGER;

    public interface BuildInformationResponse extends Callbacks.Callback1<String> {
    }

    public interface CloseSessionResponse extends Callbacks.Callback1<Integer> {
    }

    public interface CopyBufferResponse extends Callbacks.Callback1<Integer> {
    }

    public interface CopyBufferV14Response extends Callbacks.Callback1<Integer> {
    }

    public interface CopyOldUsageEntryResponse extends Callbacks.Callback1<Integer> {
    }

    public interface CreateNewUsageEntryResponse extends Callbacks.Callback2<Integer, Integer> {
    }

    public interface CreateOldUsageEntryResponse extends Callbacks.Callback1<Integer> {
    }

    public interface CreateUsageTableHeaderResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface DeactivateUsageEntryResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DeactivateUsageEntryV12Response extends Callbacks.Callback1<Integer> {
    }

    public interface DecryptCencResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface DecryptCencV15Response extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface DeleteOldUsageTableResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DeleteUsageEntryResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DeriveKeysFromSessionKeyResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ForceDeleteUsageEntryResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GenerateDerivedKeysResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GenerateNonceResponse extends Callbacks.Callback2<Integer, Integer> {
    }

    public interface GenerateRsaSignatureResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GenerateSignatureResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GenericDecryptResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GenericEncryptResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GenericSignResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GenericVerifyResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetAnalogOutputFlagsResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetCurrentSrmVersionResponse extends Callbacks.Callback2<Integer, Short> {
    }

    public interface GetDeviceIdResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GetHdcpCapabilityResponse extends Callbacks.Callback3<Integer, Integer, Integer> {
    }

    public interface GetKeyDataResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GetMaxNumberOfSessionsResponse extends Callbacks.Callback2<Integer, Integer> {
    }

    public interface GetNumberOfOpenSessionsResponse extends Callbacks.Callback2<Integer, Integer> {
    }

    public interface GetOemPublicCertificateResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface GetProvisioningMethodResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetRandomResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface InitializeDeprecatedResponse extends Callbacks.Callback1<Integer> {
    }

    public interface InitializeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface IsAntiRollbackHwPresentResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface IsRootKeyCertificateValidResponse extends Callbacks.Callback1<Integer> {
    }

    public interface IsSrmUpdateSupportedResponse extends Callbacks.Callback1<Boolean> {
    }

    public interface LoadDeviceRsaKeyResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadDrmPrivateKeyResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadEntitledContentKeysResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadEntitledContentKeysV14Response extends Callbacks.Callback1<Integer> {
    }

    public interface LoadKeysResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadKeysV11OrV12Response extends Callbacks.Callback1<Integer> {
    }

    public interface LoadKeysV14Response extends Callbacks.Callback1<Integer> {
    }

    public interface LoadLicenseResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadOemPrivateKeyResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadProvisioningResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface LoadRenewalResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadSrmResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadTestKeyboxResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadTestKeyboxV13Response extends Callbacks.Callback1<Integer> {
    }

    public interface LoadUsageEntryResponse extends Callbacks.Callback1<Integer> {
    }

    public interface LoadUsageTableHeaderResponse extends Callbacks.Callback1<Integer> {
    }

    public interface MaximumUsageTableHeaderSizeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface MinorApiVersionResponse extends Callbacks.Callback1<Integer> {
    }

    public interface MoveEntryResponse extends Callbacks.Callback1<Integer> {
    }

    public interface OpenSessionResponse extends Callbacks.Callback2<Integer, Integer> {
    }

    public interface PrepAndSignLicenseRequestResponse extends Callbacks.Callback4<Integer, Integer, byte[], byte[]> {
    }

    public interface PrepAndSignProvisioningRequestResponse extends Callbacks.Callback4<Integer, Integer, byte[], byte[]> {
    }

    public interface PrepAndSignRenewalRequestResponse extends Callbacks.Callback4<Integer, Integer, byte[], byte[]> {
    }

    public interface Proxy extends OemCryptoService, Interface.Proxy {
    }

    public interface QueryKeyControlResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface RefreshKeysResponse extends Callbacks.Callback1<Integer> {
    }

    public interface RefreshKeysV14Response extends Callbacks.Callback1<Integer> {
    }

    public interface RemoveSrmResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ReportUsageResponse extends Callbacks.Callback2<Integer, OemCryptoPstReport> {
    }

    public interface ResourceRatingTierResponse extends Callbacks.Callback1<Integer> {
    }

    public interface RewrapDeviceRsaKeyResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface SecurityPatchLevelResponse extends Callbacks.Callback1<Byte> {
    }

    public interface SelectKeyResponse extends Callbacks.Callback1<Integer> {
    }

    public interface SelectKeyV13Response extends Callbacks.Callback1<Integer> {
    }

    public interface ShrinkUsageTableHeaderResponse extends Callbacks.Callback2<Integer, byte[]> {
    }

    public interface SupportedCertificatesResponse extends Callbacks.Callback1<Integer> {
    }

    public interface TerminateResponse extends Callbacks.Callback1<Integer> {
    }

    public interface UpdateUsageEntryResponse extends Callbacks.Callback3<Integer, byte[], byte[]> {
    }

    public interface UpdateUsageTableResponse extends Callbacks.Callback1<Integer> {
    }

    void buildInformation(BuildInformationResponse buildInformationResponse);

    void closeSession(int i, CloseSessionResponse closeSessionResponse);

    void copyBuffer(int i, byte[] bArr, OemCryptoSecureBuffer oemCryptoSecureBuffer, byte b, CopyBufferResponse copyBufferResponse);

    void copyBufferV14(byte[] bArr, OemCryptoSecureBuffer oemCryptoSecureBuffer, CopyBufferV14Response copyBufferV14Response);

    void copyOldUsageEntry(int i, byte[] bArr, CopyOldUsageEntryResponse copyOldUsageEntryResponse);

    void createNewUsageEntry(int i, CreateNewUsageEntryResponse createNewUsageEntryResponse);

    void createOldUsageEntry(long j, long j2, long j3, int i, byte[] bArr, byte[] bArr2, byte[] bArr3, CreateOldUsageEntryResponse createOldUsageEntryResponse);

    void createUsageTableHeader(int i, CreateUsageTableHeaderResponse createUsageTableHeaderResponse);

    void deactivateUsageEntry(int i, byte[] bArr, DeactivateUsageEntryResponse deactivateUsageEntryResponse);

    void deactivateUsageEntryV12(byte[] bArr, DeactivateUsageEntryV12Response deactivateUsageEntryV12Response);

    void decryptCenc(int i, byte[] bArr, byte[] bArr2, SubSampleDescription[] subSampleDescriptionArr, OemCryptoCencEncryptPatternDesc oemCryptoCencEncryptPatternDesc, OemCryptoSecureBuffer oemCryptoSecureBuffer, DecryptCencResponse decryptCencResponse);

    void decryptCencV15(int i, byte[] bArr, boolean z, byte[] bArr2, int i2, OemCryptoSecureBuffer oemCryptoSecureBuffer, OemCryptoCencEncryptPatternDesc oemCryptoCencEncryptPatternDesc, DecryptCencV15Response decryptCencV15Response);

    void deleteOldUsageTable(DeleteOldUsageTableResponse deleteOldUsageTableResponse);

    void deleteUsageEntry(int i, int i2, int i3, byte[] bArr, byte[] bArr2, DeleteUsageEntryResponse deleteUsageEntryResponse);

    void deriveKeysFromSessionKey(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, DeriveKeysFromSessionKeyResponse deriveKeysFromSessionKeyResponse);

    void forceDeleteUsageEntry(byte[] bArr, ForceDeleteUsageEntryResponse forceDeleteUsageEntryResponse);

    void generateDerivedKeys(int i, byte[] bArr, byte[] bArr2, GenerateDerivedKeysResponse generateDerivedKeysResponse);

    void generateNonce(int i, GenerateNonceResponse generateNonceResponse);

    void generateRsaSignature(int i, byte[] bArr, int i2, GenerateRsaSignatureResponse generateRsaSignatureResponse);

    void generateSignature(int i, byte[] bArr, GenerateSignatureResponse generateSignatureResponse);

    void genericDecrypt(int i, byte[] bArr, byte[] bArr2, int i2, GenericDecryptResponse genericDecryptResponse);

    void genericEncrypt(int i, byte[] bArr, byte[] bArr2, int i2, GenericEncryptResponse genericEncryptResponse);

    void genericSign(int i, byte[] bArr, int i2, GenericSignResponse genericSignResponse);

    void genericVerify(int i, byte[] bArr, int i2, byte[] bArr2, GenericVerifyResponse genericVerifyResponse);

    void getAnalogOutputFlags(GetAnalogOutputFlagsResponse getAnalogOutputFlagsResponse);

    void getCurrentSrmVersion(GetCurrentSrmVersionResponse getCurrentSrmVersionResponse);

    void getDeviceId(GetDeviceIdResponse getDeviceIdResponse);

    void getHdcpCapability(GetHdcpCapabilityResponse getHdcpCapabilityResponse);

    void getKeyData(GetKeyDataResponse getKeyDataResponse);

    void getMaxNumberOfSessions(GetMaxNumberOfSessionsResponse getMaxNumberOfSessionsResponse);

    void getNumberOfOpenSessions(GetNumberOfOpenSessionsResponse getNumberOfOpenSessionsResponse);

    void getOemPublicCertificate(GetOemPublicCertificateResponse getOemPublicCertificateResponse);

    void getProvisioningMethod(GetProvisioningMethodResponse getProvisioningMethodResponse);

    void getRandom(int i, GetRandomResponse getRandomResponse);

    void initialize(int i, InitializeResponse initializeResponse);

    void initializeDeprecated(InitializeDeprecatedResponse initializeDeprecatedResponse);

    void isAntiRollbackHwPresent(IsAntiRollbackHwPresentResponse isAntiRollbackHwPresentResponse);

    void isRootKeyCertificateValid(IsRootKeyCertificateValidResponse isRootKeyCertificateValidResponse);

    void isSrmUpdateSupported(IsSrmUpdateSupportedResponse isSrmUpdateSupportedResponse);

    void loadDeviceRsaKey(int i, byte[] bArr, LoadDeviceRsaKeyResponse loadDeviceRsaKeyResponse);

    void loadDrmPrivateKey(int i, int i2, byte[] bArr, LoadDrmPrivateKeyResponse loadDrmPrivateKeyResponse);

    void loadEntitledContentKeys(int i, byte[] bArr, OemCryptoEntitledContentKeyObject[] oemCryptoEntitledContentKeyObjectArr, LoadEntitledContentKeysResponse loadEntitledContentKeysResponse);

    void loadEntitledContentKeysV14(int i, OemCryptoEntitledContentKeyObjectV14[] oemCryptoEntitledContentKeyObjectV14Arr, LoadEntitledContentKeysV14Response loadEntitledContentKeysV14Response);

    void loadKeys(int i, byte[] bArr, byte[] bArr2, OemCryptoSubstring oemCryptoSubstring, OemCryptoSubstring oemCryptoSubstring2, OemCryptoKeyObject[] oemCryptoKeyObjectArr, OemCryptoSubstring oemCryptoSubstring3, OemCryptoSubstring oemCryptoSubstring4, int i2, LoadKeysResponse loadKeysResponse);

    void loadKeysV11OrV12(int i, byte[] bArr, byte[] bArr2, boolean z, int i2, int i3, OemCryptoKeyObjectV14[] oemCryptoKeyObjectV14Arr, int i4, int i5, LoadKeysV11OrV12Response loadKeysV11OrV12Response);

    void loadKeysV14(int i, byte[] bArr, byte[] bArr2, boolean z, int i2, int i3, OemCryptoKeyObjectV14[] oemCryptoKeyObjectV14Arr, int i4, int i5, byte[] bArr3, int i6, LoadKeysV14Response loadKeysV14Response);

    void loadLicense(int i, byte[] bArr, int i2, byte[] bArr2, LoadLicenseResponse loadLicenseResponse);

    void loadOemPrivateKey(int i, LoadOemPrivateKeyResponse loadOemPrivateKeyResponse);

    void loadProvisioning(int i, byte[] bArr, int i2, byte[] bArr2, int i3, LoadProvisioningResponse loadProvisioningResponse);

    void loadRenewal(int i, byte[] bArr, int i2, byte[] bArr2, LoadRenewalResponse loadRenewalResponse);

    void loadSrm(byte[] bArr, LoadSrmResponse loadSrmResponse);

    void loadTestKeybox(byte[] bArr, LoadTestKeyboxResponse loadTestKeyboxResponse);

    void loadTestKeyboxV13(LoadTestKeyboxV13Response loadTestKeyboxV13Response);

    void loadUsageEntry(int i, int i2, byte[] bArr, LoadUsageEntryResponse loadUsageEntryResponse);

    void loadUsageTableHeader(byte[] bArr, LoadUsageTableHeaderResponse loadUsageTableHeaderResponse);

    void maximumUsageTableHeaderSize(MaximumUsageTableHeaderSizeResponse maximumUsageTableHeaderSizeResponse);

    void minorApiVersion(MinorApiVersionResponse minorApiVersionResponse);

    void moveEntry(int i, int i2, MoveEntryResponse moveEntryResponse);

    void openSession(OpenSessionResponse openSessionResponse);

    void prepAndSignLicenseRequest(int i, byte[] bArr, int i2, int i3, PrepAndSignLicenseRequestResponse prepAndSignLicenseRequestResponse);

    void prepAndSignProvisioningRequest(int i, byte[] bArr, int i2, int i3, PrepAndSignProvisioningRequestResponse prepAndSignProvisioningRequestResponse);

    void prepAndSignRenewalRequest(int i, byte[] bArr, int i2, int i3, PrepAndSignRenewalRequestResponse prepAndSignRenewalRequestResponse);

    void queryKeyControl(int i, byte[] bArr, QueryKeyControlResponse queryKeyControlResponse);

    void refreshKeys(int i, byte[] bArr, byte[] bArr2, OemCryptoKeyRefreshObject[] oemCryptoKeyRefreshObjectArr, RefreshKeysResponse refreshKeysResponse);

    void refreshKeysV14(int i, byte[] bArr, byte[] bArr2, OemCryptoKeyRefreshObjectV14[] oemCryptoKeyRefreshObjectV14Arr, RefreshKeysV14Response refreshKeysV14Response);

    void removeSrm(RemoveSrmResponse removeSrmResponse);

    void reportUsage(int i, byte[] bArr, ReportUsageResponse reportUsageResponse);

    void resourceRatingTier(ResourceRatingTierResponse resourceRatingTierResponse);

    void rewrapDeviceRsaKey(int i, byte[] bArr, byte[] bArr2, int i2, int i3, int i4, int i5, RewrapDeviceRsaKeyResponse rewrapDeviceRsaKeyResponse);

    void securityPatchLevel(SecurityPatchLevelResponse securityPatchLevelResponse);

    void selectKey(int i, byte[] bArr, int i2, SelectKeyResponse selectKeyResponse);

    void selectKeyV13(int i, byte[] bArr, SelectKeyV13Response selectKeyV13Response);

    void shrinkUsageTableHeader(int i, int i2, ShrinkUsageTableHeaderResponse shrinkUsageTableHeaderResponse);

    void supportedCertificates(SupportedCertificatesResponse supportedCertificatesResponse);

    void terminate(TerminateResponse terminateResponse);

    void updateUsageEntry(int i, int i2, int i3, UpdateUsageEntryResponse updateUsageEntryResponse);

    void updateUsageTable(UpdateUsageTableResponse updateUsageTableResponse);
}
