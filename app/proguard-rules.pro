-keepattributes Signature
-keepattributes *Annotation*

# Retrofit
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.plexquest.app.data.models.** { *; }
-keep class com.plexquest.app.data.api.** { *; }
