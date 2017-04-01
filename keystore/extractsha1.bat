path = %java_home%\bin

echo "SHA1 of xiaomaoqiu.keystore"
keytool -list -keystore xiaomaoqiu.keystore -storepass xiaomaoqiu_android

echo "SHA1 of debug.keysotre"
keytool -list -keystore %appdata%\..\..\.android\debug.keystore -storepass android

pause
