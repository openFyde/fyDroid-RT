package org.chromium.payments.mojom;

import com.android.tools.r8.GeneratedOutlineSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;
import org.chromium.url.mojom.Url;

public final class ItemDetails extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String description;
    public String freeTrialPeriod;
    public Url[] iconUrls;
    public PaymentCurrencyAmount introductoryPrice;
    public int introductoryPriceCycles;
    public String introductoryPricePeriod;
    public String itemId;
    public PaymentCurrencyAmount price;
    public String subscriptionPeriod;
    public String title;
    public int type;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(72, 0), new DataHeader(88, 1)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[1];
    }

    public ItemDetails() {
        super(88, 1);
    }

    public static ItemDetails decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            int i = decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion;
            ItemDetails itemDetails = new ItemDetails(i);
            itemDetails.itemId = decoder.readString(8, false);
            itemDetails.title = decoder.readString(16, false);
            itemDetails.description = decoder.readString(24, false);
            itemDetails.price = PaymentCurrencyAmount.decode(decoder.readPointer(32, false));
            itemDetails.subscriptionPeriod = decoder.readString(40, true);
            itemDetails.freeTrialPeriod = decoder.readString(48, true);
            itemDetails.introductoryPrice = PaymentCurrencyAmount.decode(decoder.readPointer(56, true));
            itemDetails.introductoryPricePeriod = decoder.readString(64, true);
            if (i >= 1) {
                itemDetails.introductoryPriceCycles = decoder.readInt(72);
                int i2 = decoder.readInt(76);
                itemDetails.type = i2;
                ItemType.validate(i2);
                Decoder pointer = decoder.readPointer(80, true);
                if (pointer == null) {
                    itemDetails.iconUrls = null;
                } else {
                    DataHeader dataHeaderForPointerArray = pointer.readDataHeaderForPointerArray(-1);
                    itemDetails.iconUrls = new Url[dataHeaderForPointerArray.elementsOrVersion];
                    for (int i3 = 0; i3 < dataHeaderForPointerArray.elementsOrVersion; i3++) {
                        itemDetails.iconUrls[i3] = Url.decode(GeneratedOutlineSupport.outline2(i3, 8, 8, pointer, false));
                    }
                }
            }
            return itemDetails;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static ItemDetails deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.itemId, 8, false);
        encoderAtDataOffset.encode(this.title, 16, false);
        encoderAtDataOffset.encode(this.description, 24, false);
        encoderAtDataOffset.encode((Struct) this.price, 32, false);
        encoderAtDataOffset.encode(this.subscriptionPeriod, 40, true);
        encoderAtDataOffset.encode(this.freeTrialPeriod, 48, true);
        encoderAtDataOffset.encode((Struct) this.introductoryPrice, 56, true);
        encoderAtDataOffset.encode(this.introductoryPricePeriod, 64, true);
        encoderAtDataOffset.encode(this.introductoryPriceCycles, 72);
        encoderAtDataOffset.encode(this.type, 76);
        Url[] urlArr = this.iconUrls;
        if (urlArr == null) {
            encoderAtDataOffset.encodeNullPointer(80, true);
            return;
        }
        Encoder encoderEncodePointerArray = encoderAtDataOffset.encodePointerArray(urlArr.length, 80, -1);
        int i = 0;
        while (true) {
            Url[] urlArr2 = this.iconUrls;
            if (i >= urlArr2.length) {
                return;
            }
            encoderEncodePointerArray.encode((Struct) urlArr2[i], (i * 8) + 8, false);
            i++;
        }
    }

    private ItemDetails(int i) {
        super(88, i);
    }

    public static ItemDetails deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
