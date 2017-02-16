# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Jorell\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}


-keepclassmembers class * {
    public void openFileChooser(android.webkit.ValueCallback, java.lang.String);
    public void openFileChooser(android.webkit.ValueCallback);
    public void openFileChooser(android.webkit.ValueCallback, java.lang.String, java.lang.String);
    public boolean onShowFileChooser(android.webkit.WebView, android.webkit.ValueCallback, android.webkit.WebChromeClient.FileChooserParams);
}

-keep class nl.matshofman.saxrssreader.** {
    *;
}



-keeppackagenames org.jsoup.nodes

-dontwarn com.squareup.okhttp.**

-dontwarn okio.**

-keepclassmembers class * extends android.webkit.WebChromeClient {
   public void openFileChooser(...);
}

-keepattributes Signature
-keep class com.facebook.** {
   *;
}


-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

-keep class android.support.v7.widget.SearchView { *; }

-keep class * extends android.webkit.WebChromeClient { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# jsoup library
-keeppackagenames org.jsoup.nodes

# remove logs (disabled for now)
# works only with proguard-android-optimize.txt
#-assumenosideeffects class android.util.Log {
#    *;
#}

# acra library
# keep this class so that logging will show 'ACRA' and not a obfuscated name like 'a'.
# Note: if you are removing log messages elsewhere in this file then this isn't necessary
-keep class org.acra.ACRA {
    *;
}

# keep this around for some enums that ACRA needs
-keep class org.acra.ReportingInteractionMode {
    *;
}

-keepnames class org.acra.sender.HttpSender$** {
    *;
}

-keepnames enum org.acra.ReportField {
    *;
}

# keep this otherwise it is removed by ProGuard
-keep public class org.acra.ErrorReporter {
    public void addCustomData(java.lang.String,java.lang.String);
    public void putCustomData(java.lang.String,java.lang.String);
    public void removeCustomData(java.lang.String);
}

# keep this otherwise it is removed by ProGuard
-keep public class org.acra.ErrorReporter {
    public void handleSilentException(java.lang.Throwable);
}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}