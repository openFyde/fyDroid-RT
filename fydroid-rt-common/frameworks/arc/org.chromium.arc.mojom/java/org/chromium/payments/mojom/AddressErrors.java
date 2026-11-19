package org.chromium.payments.mojom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AddressErrors extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO;
    private static final DataHeader[] VERSION_ARRAY;
    public String addressLine;
    public String city;
    public String country;
    public String dependentLocality;
    public String organization;
    public String phone;
    public String postalCode;
    public String recipient;
    public String region;
    public String sortingCode;

    static {
        DataHeader[] dataHeaderArr = {new DataHeader(88, 0)};
        VERSION_ARRAY = dataHeaderArr;
        DEFAULT_STRUCT_INFO = dataHeaderArr[0];
    }

    public AddressErrors() {
        super(88, 0);
    }

    public static AddressErrors decode(Decoder decoder) {
        if (decoder == null) {
            return null;
        }
        decoder.increaseStackDepth();
        try {
            AddressErrors addressErrors = new AddressErrors(decoder.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
            addressErrors.addressLine = decoder.readString(8, false);
            addressErrors.city = decoder.readString(16, false);
            addressErrors.country = decoder.readString(24, false);
            addressErrors.dependentLocality = decoder.readString(32, false);
            addressErrors.organization = decoder.readString(40, false);
            addressErrors.phone = decoder.readString(48, false);
            addressErrors.postalCode = decoder.readString(56, false);
            addressErrors.recipient = decoder.readString(64, false);
            addressErrors.region = decoder.readString(72, false);
            addressErrors.sortingCode = decoder.readString(80, false);
            return addressErrors;
        } finally {
            decoder.decreaseStackDepth();
        }
    }

    public static AddressErrors deserialize(Message message) {
        return decode(new Decoder(message));
    }

    @Override // org.chromium.mojo.bindings.Struct
    protected final void encode(Encoder encoder) {
        Encoder encoderAtDataOffset = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoderAtDataOffset.encode(this.addressLine, 8, false);
        encoderAtDataOffset.encode(this.city, 16, false);
        encoderAtDataOffset.encode(this.country, 24, false);
        encoderAtDataOffset.encode(this.dependentLocality, 32, false);
        encoderAtDataOffset.encode(this.organization, 40, false);
        encoderAtDataOffset.encode(this.phone, 48, false);
        encoderAtDataOffset.encode(this.postalCode, 56, false);
        encoderAtDataOffset.encode(this.recipient, 64, false);
        encoderAtDataOffset.encode(this.region, 72, false);
        encoderAtDataOffset.encode(this.sortingCode, 80, false);
    }

    private AddressErrors(int i) {
        super(88, i);
    }

    public static AddressErrors deserialize(ByteBuffer byteBuffer) {
        return deserialize(new Message(byteBuffer, new ArrayList()));
    }
}
