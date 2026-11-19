package org.chromium.arc.util;

import android.content.Context;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;

import org.chromium.arc.mojom.RawIconPngData;

/**
 * Icon utility for ARC app launcher.
 */
public class IconUtil {
    private static final String TAG = "IconUtil";
    private static final byte[] EMPTY_ICON_CONTENT = new byte[0];
    private static final IconNormalizer sIconNormalizer = new IconNormalizer();

    public static byte[] convertBitmapToPng(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    public static RawIconPngData createIconAsPng(Drawable drawable, IconParameters params) {
        RawIconPngData data;
        synchronized (sIconNormalizer) {
            data = (RawIconPngData) createIconLocked(drawable, params, 1.0f).first;
        }
        return data;
    }

    public static Pair<RawIconPngData, Bitmap> createNormalizedIcon(
            Drawable drawable, IconParameters params) {
        return createNormalizedIcon(drawable, -1, params);
    }

    public static Pair<RawIconPngData, Bitmap> createNormalizedIcon(
            Drawable drawable, int iconTypeHint, IconParameters params) {
        Pair<RawIconPngData, Bitmap> result;
        synchronized (sIconNormalizer) {
            float[] scale = new float[1];
            Drawable normalized = sIconNormalizer.normalizeIcon(
                    drawable, iconTypeHint, params.iconDimension, scale);
            result = createIconLocked(normalized, params, scale[0]);
        }
        return result;
    }

    public static RawIconPngData getEmptyIconData() {
        RawIconPngData data = new RawIconPngData();
        data.isAdaptiveIcon = false;
        data.iconPngData = EMPTY_ICON_CONTENT;
        return data;
    }

    public static Pair<RawIconPngData, Bitmap> getIconData(
            String name, int resId, Resources resources, IconParameters params) {
        if (resId == 0) {
            Log.e(TAG, "Icon id is not valid: " + name);
            return new Pair<>(getEmptyIconData(), null);
        }
        try {
            int density = params.androidDensity;
            TypedValue tv = new TypedValue();
            resources.getValueForDensity(resId, density, tv, true);
            if (tv.density > 0 && tv.density != 65535 && tv.density != 65534) {
                density = tv.density;
            }
            Drawable d = resources.getDrawableForDensity(resId, density, null);
            if (d != null) {
                return createNormalizedIcon(d, params);
            }
            Log.e(TAG, "Cannot get icon data for " + name);
            return new Pair<>(getEmptyIconData(), null);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Failed to find icon " + name + "(" + resId + ")");
            return new Pair<>(getEmptyIconData(), null);
        } catch (OutOfMemoryError | RuntimeException e) {
            Log.e(TAG, "Failed to decode icon " + name + "(" + resId + ")", e);
            return new Pair<>(getEmptyIconData(), null);
        }
    }

    public static RawIconPngData getIconDataAsPng(
            String name, int resId, Resources resources, IconParameters params) {
        return (RawIconPngData) getIconData(name, resId, resources, params).first;
    }

    public static RawIconPngData getIconDataAsPng(
            Context context, ComponentInfo info, IconParameters params)
            throws PackageManager.NameNotFoundException {
        Resources res = context.getPackageManager()
                .getResourcesForApplication(info.applicationInfo);
        RawIconPngData data = getIconDataAsPng(
                info.toString(), info.getIconResource(), res, params);
        byte[] png = data.iconPngData;
        return (png == null || png.length == 0)
                ? getPackageIconAsPng(context, info.packageName, params)
                : data;
    }

    public static RawIconPngData getIconDataAsPng(
            Context context, String packageName, String resourceName, IconParameters params) {
        try {
            Resources res = context.getPackageManager()
                    .getResourcesForApplication(packageName);
            if (res != null) {
                int resId = res.getIdentifier(resourceName, null, null);
                return getIconDataAsPng(resourceName, resId, res, params);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Cannot get icon data for " + packageName + ":" + resourceName);
        }
        return getPackageIconAsPng(context, packageName, params);
    }

    public static RawIconPngData getPackageIconAsPng(
            Context context, String packageName, IconParameters params) {
        try {
            Drawable icon = context.getPackageManager().getApplicationIcon(packageName);
            return (RawIconPngData) createNormalizedIcon(icon, params).first;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Cannot get icon data for " + packageName);
            return getEmptyIconData();
        }
    }

    private static Pair<RawIconPngData, Bitmap> createIconLocked(
            Drawable drawable, IconParameters params, float scale) {
        int dim = params.iconDimension;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        int outW = dim;
        int outH = dim;
        if (w > 0 && h > 0) {
            float aspect = (float) w / h;
            if (w > h) {
                outH = (int) (dim / aspect);
            } else if (h > w) {
                outW = (int) (dim * aspect);
            }
        }

        int left = (dim - outW) / 2;
        int top = (dim - outH) / 2;

        RawIconPngData data = new RawIconPngData();
        data.isAdaptiveIcon = false;

        if (drawable instanceof android.graphics.drawable.AdaptiveIconDrawable) {
            android.graphics.drawable.AdaptiveIconDrawable aid =
                    (android.graphics.drawable.AdaptiveIconDrawable) drawable;
            data.isAdaptiveIcon = true;

            Drawable fg = aid.getForeground();
            if (fg == null) {
                fg = new android.graphics.drawable.ColorDrawable(0);
            }
            data.foregroundIconPngData = renderToPng(fg, params, scale);

            Drawable bg = aid.getBackground();
            if (bg == null) {
                bg = new android.graphics.drawable.ColorDrawable(0xFFFFFFFF);
            }
            data.backgroundIconPngData = renderToPng(bg, params, scale);
        }

        drawable.setBounds(left, top, left + outW, top + outH);
        Bitmap bmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.save();
        canvas.scale(scale, scale, dim / 2f, dim / 2f);
        drawable.draw(canvas);
        canvas.restore();
        data.iconPngData = convertBitmapToPng(bmp);

        return new Pair<>(data, bmp);
    }

    private static byte[] renderToPng(
            Drawable drawable, IconParameters params, float scale) {
        int dim = params.iconDimension;
        if (drawable.getIntrinsicWidth() == -1) {
            drawable.setBounds(0, 0, dim, dim);
        } else {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
        }
        int w = drawable.getBounds().width();
        int h = drawable.getBounds().height();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.save();
        canvas.scale(scale, scale, w / 2f, h / 2f);
        drawable.draw(canvas);
        canvas.restore();
        return convertBitmapToPng(bmp);
    }
}
