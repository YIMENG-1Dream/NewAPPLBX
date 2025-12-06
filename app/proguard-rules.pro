# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep AndroidX classes
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Keep our app classes
-keep class com.destinationovo.weblxbapp.** { *; }

# Keep WebView interface
-keepclassmembers class com.destinationovo.weblxbapp.WebAppInterface {
    public *;
}