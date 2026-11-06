# Keep scanning engine classes
-keep class com.sayanox.scanner.domain.** { *; }
-keep class com.sayanox.scanner.data.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
