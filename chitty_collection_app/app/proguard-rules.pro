# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\user\AppData\Local\Android\Sdk\tools\proguard\proguard-android.txt
# You can edit that file to add flags that are common to all your projects.

# If you use reflection, you might need to keep the name of the members you reflect on.
# -keep class com.example.MyClass {
#   public <init>();
#   void myMethod(java.lang.String);
#   java.lang.String myField;
# }

# For using GSON with obfuscated classes, you need to add the following rules.
# -keep class com.google.gson.examples.android.model.** { *; }

# If you're using retrofit, you might need this.
# -dontwarn retrofit2.**
# -keep class retrofit2.** { *; }
# -keepattributes Signature
# -keepattributes Exceptions

# For Room
-keep class androidx.room.** { *; }
-keep class com.example.chittycollectionapp.data.model.** { *; }
