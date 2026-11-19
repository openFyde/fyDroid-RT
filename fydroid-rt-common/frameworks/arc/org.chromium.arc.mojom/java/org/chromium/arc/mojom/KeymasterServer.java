// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface KeymasterServer extends Interface {
    public static final Interface.Manager<KeymasterServer, Proxy> MANAGER = KeymasterServer_Internal.MANAGER;

    public interface AbortResponse extends Callbacks.Callback1<Integer> {
    }

    public interface AddRngEntropyResponse extends Callbacks.Callback1<Integer> {
    }

    public interface AttestKeyResponse extends Callbacks.Callback1<AttestKeyResult> {
    }

    public interface BeginResponse extends Callbacks.Callback1<BeginResult> {
    }

    public interface DeleteAllKeysResponse extends Callbacks.Callback1<Integer> {
    }

    public interface DeleteKeyResponse extends Callbacks.Callback1<Integer> {
    }

    public interface ExportKeyResponse extends Callbacks.Callback1<ExportKeyResult> {
    }

    public interface FinishResponse extends Callbacks.Callback1<FinishResult> {
    }

    public interface GenerateKeyResponse extends Callbacks.Callback1<GenerateKeyResult> {
    }

    public interface GetKeyCharacteristicsResponse extends Callbacks.Callback1<GetKeyCharacteristicsResult> {
    }

    public interface ImportKeyResponse extends Callbacks.Callback1<ImportKeyResult> {
    }

    public interface Proxy extends KeymasterServer, Interface.Proxy {
    }

    public interface UpdateResponse extends Callbacks.Callback1<UpdateResult> {
    }

    public interface UpgradeKeyResponse extends Callbacks.Callback1<UpgradeKeyResult> {
    }

    void abort(long j, AbortResponse abortResponse);

    void addRngEntropy(byte[] bArr, AddRngEntropyResponse addRngEntropyResponse);

    void attestKey(AttestKeyRequest attestKeyRequest, AttestKeyResponse attestKeyResponse);

    void begin(BeginRequest beginRequest, BeginResponse beginResponse);

    void deleteAllKeys(DeleteAllKeysResponse deleteAllKeysResponse);

    void deleteKey(byte[] bArr, DeleteKeyResponse deleteKeyResponse);

    void exportKey(ExportKeyRequest exportKeyRequest, ExportKeyResponse exportKeyResponse);

    void finish(FinishRequest finishRequest, FinishResponse finishResponse);

    void generateKey(KeyParameter[] keyParameterArr, GenerateKeyResponse generateKeyResponse);

    void getKeyCharacteristics(GetKeyCharacteristicsRequest getKeyCharacteristicsRequest, GetKeyCharacteristicsResponse getKeyCharacteristicsResponse);

    void importKey(ImportKeyRequest importKeyRequest, ImportKeyResponse importKeyResponse);

    void setSystemVersion(int i, int i2);

    void update(UpdateRequest updateRequest, UpdateResponse updateResponse);

    void upgradeKey(UpgradeKeyRequest upgradeKeyRequest, UpgradeKeyResponse upgradeKeyResponse);
}
