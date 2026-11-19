package org.chromium.cros.mojom;

import org.chromium.mojo.bindings.Callbacks;
import org.chromium.mojo.bindings.Interface;

public interface VendorTagOps extends Interface {
    public static final Interface.Manager<VendorTagOps, Proxy> MANAGER = VendorTagOps_Internal.MANAGER;

    public interface GetAllTagsResponse extends Callbacks.Callback1<int[]> {
    }

    public interface GetSectionNameResponse extends Callbacks.Callback1<String> {
    }

    public interface GetTagCountResponse extends Callbacks.Callback1<Integer> {
    }

    public interface GetTagNameResponse extends Callbacks.Callback1<String> {
    }

    public interface GetTagTypeResponse extends Callbacks.Callback1<Integer> {
    }

    public interface Proxy extends VendorTagOps, Interface.Proxy {
    }

    void getAllTags(GetAllTagsResponse getAllTagsResponse);

    void getSectionName(int i, GetSectionNameResponse getSectionNameResponse);

    void getTagCount(GetTagCountResponse getTagCountResponse);

    void getTagName(int i, GetTagNameResponse getTagNameResponse);

    void getTagType(int i, GetTagTypeResponse getTagTypeResponse);
}
