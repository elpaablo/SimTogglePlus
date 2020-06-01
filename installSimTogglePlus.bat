::WIN BATCH SCRIPT

:: CHANGE THESE
set app_package=com.elpaablo.simtoggleplus
set dir_app_name=SimTogglePlus
set MAIN_ACTIVITY=MainActivity

set ADB="adb"
::ADB_SH="%ADB% shell" # this script assumes using `adb root`. for `adb su` 
see `Caveats`

set path_sysapp=/system/product/priv-app
set apk_host=C:\Users\elpablo\AndroidStudioProjects\SimTogglePlus\app\build\outputs\apk\debug\app-debug.apk
set apk_name=%dir_app_name%.apk
set apk_target_dir=%path_sysapp%/%dir_app_name%
set apk_target_sys=%apk_target_dir%/%apk_name%

:: Delete previous APK
del %apk_host%

:: Compile the APK: you can adapt this for production build, flavors, etc.
call gradlew assembleDebug

set ADB_SH=%ADB% shell su -c

:: Install APK: using adb su
%ADB_SH% mount -o rw,remount /
%ADB_SH% chmod 777 /system/lib/
%ADB_SH% mkdir -p /data/local/tmp
%ADB_SH% mkdir -p %apk_target_dir%
%ADB% push %apk_host% /data/local/tmp/%apk_name% 
%ADB_SH% mv /data/local/tmp/%apk_name% %apk_target_sys%
::%ADB_SH% rmdir /data/local/tmp

:: Give permissions
%ADB_SH% chmod 755 %apk_target_dir%
%ADB_SH% chmod 644 %apk_target_sys%

::Unmount system
%ADB_SH% mount -o remount,ro /

:: Stop the app
%ADB% shell am force-stop %app_package%

:: Reinstall the app
%ADB% shell pm install -r %path_sysapp%/%dir_app_name%/%apk_name%

:: Re execute the app
:: %ADB% shell am start -n \"%app_package%/%app_package%.%MAIN_ACTIVITY%\" -a android.intent.action.MAIN -c android.intent.category.DEFAULT

