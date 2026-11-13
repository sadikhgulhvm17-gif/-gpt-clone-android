# GPT Clone لأندرويد — مشروع بناء محلي من Termux/Debian

## تنبيه مهم
هذه حزمة مصدر تجريبية لواجهة محادثة Android، وليست APK جاهزاً. لم يتم اختبار بناءها على جهاز ARM64 بعد. كما أن ربط واجهة LiteRT-LM بالمحادثة المحلية ليس مكتملاً في هذه النسخة؛ اختيار ملف النموذج حالياً يحفظ URI فقط ولا يشغّل النموذج. أعددناها لتجهيز مشروع Android والتحقق من سلسلة البناء أولاً قبل إضافة محرك الاستدلال.

## المتطلبات
- Debian داخل `proot-distro` على ARM64.
- JDK 17 أو 21.
- Gradle 8.13.
- Android SDK Platform 36.
- Android Build Tools متوافقة وAAPT2 تعمل على ARM64.

## إعداد SDK
```bash
export ANDROID_HOME=/usr/lib/android-sdk
export ANDROID_SDK_ROOT=/usr/lib/android-sdk
export PATH=/opt/gradle-8.13/bin:$ANDROID_HOME/platform-tools:$PATH
```

من مجلد المشروع أنشئ `local.properties`:
```bash
printf 'sdk.dir=/usr/lib/android-sdk\n' > local.properties
```

## البناء
```bash
gradle --no-daemon --max-workers=1 assembleDebug
```
المفترض أن يكون الناتج عند نجاح البناء:
`app/build/outputs/apk/debug/app-debug.apk`

## نموذج Qwen3
ملف النموذج المقترح للتجربة:
https://huggingface.co/litert-community/Qwen3-0.6B/resolve/main/Qwen3-0.6B_dynamic_wi4b32_afp32.litertlm

لا تضع ملف النموذج داخل APK أو مستودع Git؛ نزّله مباشرة إلى الهاتف. الحجم المذكور في بطاقة النموذج نحو 328 MiB. لا يعتبر هذا الملف متوافقاً تلقائياً مع كل إصدار من LiteRT-LM؛ يجب التحقق من دعم النموذج وAPI قبل تفعيل الاستدلال.
