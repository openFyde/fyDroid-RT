/* Open source replacement for org.chromium.arc.applauncher.AppShortcutItemInfo
 * from the proprietary com.google.android.chromeos jar. */
package org.chromium.arc.applauncher;

// Hand-written Java Parcelable (NOT a stable AIDL parcelable): the
// proprietary wire format is flat (no writeInt(0)/backfill size prefix),
// and it also uses Parcel.writeValue(Boolean)/readValue(null) for the
// isAdaptiveIcon field, which the stable AIDL generator cannot produce.
// The actual Parcelable logic lives in
//   java/org/chromium/arc/applauncher/AppShortcutItemInfo.java
// and mirrors the proprietary field order bit-for-bit so the binder
// traffic stays interoperable with the proprietary implementation.
parcelable AppShortcutItemInfo;
