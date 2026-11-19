package org.chromium.arc.mojom;

public final class ChromePage {
    public static final int ABOUTBLANK = 30;
    public static final int ABOUTDOWNLOADS = 31;
    public static final int ABOUTHISTORY = 32;
    public static final int ACCOUNTS = 10;
    public static final int APPEARANCE = 11;
    public static final int AUDIO = 80;
    public static final int AUDIOANDCAPTIONS = 84;
    public static final int AUTOFILL = 12;
    public static final int BLUETOOTH = 3;
    public static final int BLUETOOTHDEVICES = 13;
    public static final int CHANGEPICTURE = 14;
    public static final int CLEARBROWSERDATA = 15;
    public static final int CUPSPRINTERS = 17;
    public static final int CURSORANDTOUCHPAD = 83;
    public static final int DATETIME = 4;
    public static final int DISPLAY = 5;
    public static final int DISPLAYANDMAGNFICATION = 81;
    public static final int DOWNLOADS = 18;
    public static final int HELP = 9;
    public static final int KEYBOARDANDTEXTINPUT = 82;
    public static final int KEYBOARDOVERLAY = 19;
    public static final int LANGUAGES = 7;
    public static final int LOCKSCREEN = 20;
    public static final int MAIN = 1;
    public static final int MANAGEACCESSIBILITY = 21;
    public static final int MANAGEACCESSIBILITYTTS = 49;
    public static final int MULTIDEVICE = 0;
    public static final int NETWORKS = 90;
    public static final int NETWORKSTYPEVPN = 22;
    public static final int ONSTARTUP = 23;
    public static final int OSLANGUAGESINPUT = 71;
    public static final int OSLANGUAGESLANGUAGES = 72;
    public static final int PASSWORDS = 24;
    public static final int PERDEVICEKEYBOARD = 85;
    public static final int PERDEVICEMOUSE = 86;
    public static final int PERDEVICEPOINTINGSTICK = 88;
    public static final int PERDEVICETOUCHPAD = 87;
    public static final int POINTEROVERLAY = 25;
    public static final int POWER = 2;
    public static final int PRIVACY = 8;
    public static final int PRIVACYHUB = 78;
    public static final int RESET = 26;
    public static final int SEARCH = 27;
    public static final int SMARTPRIVACY = 77;
    public static final int STORAGE = 28;
    public static final int SYNCSETUP = 29;
    public static final int TEXTTOSPEECH = 79;
    public static final int WIFI = 6;

    private ChromePage() {
    }

    public static boolean isKnownValue(int i) {
        if (i == 49 || i == 90 || i == 71 || i == 72) {
            return true;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return true;
            default:
                switch (i) {
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                        return true;
                    default:
                        switch (i) {
                            case SMARTPRIVACY /* 77 */:
                            case PRIVACYHUB /* 78 */:
                            case TEXTTOSPEECH /* 79 */:
                            case AUDIO /* 80 */:
                            case DISPLAYANDMAGNFICATION /* 81 */:
                            case KEYBOARDANDTEXTINPUT /* 82 */:
                            case CURSORANDTOUCHPAD /* 83 */:
                            case AUDIOANDCAPTIONS /* 84 */:
                            case PERDEVICEKEYBOARD /* 85 */:
                            case PERDEVICEMOUSE /* 86 */:
                            case PERDEVICETOUCHPAD /* 87 */:
                            case PERDEVICEPOINTINGSTICK /* 88 */:
                                return true;
                            default:
                                return false;
                        }
                }
        }
    }

    public static void validate(int i) {
    }
}
