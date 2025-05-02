# Hilt Worker requires class name to remain readable
-keepnames class com.harrytmthy.sanctum.** extends androidx.work.CoroutineWorker

# kotlinx.serialization (used in EntryPayload, Document, etc.)
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Required for @Serializable and @Polymorphic to work at runtime
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault