package io.fydeos.arc.installpolicy;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * How an ArcPolicy {@code packageName} selects packages, and which of several matching entries
 * governs a package. Used by both FydroidPolicyManager and ArcPolicyService, so the install-time
 * gate and the after-install enforcement always pick the same entry.
 *
 * <p>A {@code packageName} is one of:
 * <ul>
 *   <li>a literal package name ({@code com.example.app}): matches exactly that package;
 *   <li>{@code *}: matches every package;
 *   <li>a prefix wildcard ending in one {@code *} ({@code com.example.*}): plain prefix match;
 *   <li>anything else: a Java regex, matched with {@link java.util.regex.Matcher#find()}.
 * </ul>
 * Everything that is not a literal is a "pattern".
 *
 * <p>Precedence, most specific first: a literal beats every pattern; between patterns the longer
 * one wins; equal lengths fall back to string order, so the choice is deterministic.
 */
public final class PackagePatterns {
    // A literal package name: dot-separated word groups, e.g. com.example.app
    private static final Pattern LITERAL = Pattern.compile("^(\\w+\\.)+\\w+$");
    // A simple prefix wildcard, e.g. com.example.*
    private static final Pattern PREFIX = Pattern.compile("^[^*]+\\*$");

    private PackagePatterns() {
    }

    /** True if {@code packageName} names one package rather than a pattern. */
    public static boolean isLiteral(String packageName) {
        return packageName != null && LITERAL.matcher(packageName).matches();
    }

    /** True if the entry {@code packageName} (a pattern when {@code isPattern}) selects {@code target}. */
    public static boolean matches(String packageName, boolean isPattern, String target) {
        if (packageName == null || target == null) {
            return false;
        }
        if (!isPattern) {
            return packageName.equals(target);
        }
        if (packageName.equals("*")) {
            return true;
        }
        if (PREFIX.matcher(packageName).find()) {
            return target.startsWith(packageName.substring(0, packageName.length() - 1));
        }
        try {
            return Pattern.compile(packageName).matcher(target).find();
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * Orders two entries by precedence: negative if {@code a} governs over {@code b}. See the class
     * comment for the rule.
     */
    public static int compare(String a, boolean aIsPattern, String b, boolean bIsPattern) {
        if (aIsPattern != bIsPattern) {
            return aIsPattern ? 1 : -1;
        }
        if (a.length() != b.length()) {
            return b.length() - a.length();
        }
        return a.compareTo(b);
    }
}
