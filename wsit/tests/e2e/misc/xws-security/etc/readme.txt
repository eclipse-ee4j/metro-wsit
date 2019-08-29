If certificates expire or you need to generate new ones, use sth like:

client-keystore.jks [alice]:
keytool -keystore client-keystore.jks -genkey -alias alice -keyalg RSA -keysize 1024 -validity 3650 -storepass changeit
CN=Alice, OU=Interop Test Cert, O=Eclipse Foundation, L=Prague, ST=Czech Republic, C=CZ
keytool -export -keystore client-keystore.jks -alias alice -file Alice.cer -storepass changeit

server-keystore.jks [bob]
keytool -keystore server-keystore.jks -genkey -alias bob -keyalg RSA -keysize 1024 -validity 3650 -storepass changeit
CN=Bob, OU=Interop Test Cert, O=Eclipse Foundation, L=Prague, ST=Czech Republic, C=CZ
keytool -export -keystore server-keystore.jks -alias bob -file Bob.cer -storepass changeit

sts-keystore.jks [wssip's oasis interop test ca id]
keytool -keystore sts-keystore.jks -genkey -alias "wssip's oasis interop test ca id" -keyalg RSA -keysize 1024 -validity 3650 -storepass changeit
CN=WssIP, OU=Interop Test Cert, O=Eclipse Foundation, L=Prague, ST=Czech Republic, C=CZ
keytool -export -keystore sts-keystore.jks -alias "wssip's oasis interop test ca id" -file Wssip.cer -storepass changeit


client-truststore.jks:
keytool -import -file Bob.cer -alias bob -keystore client-truststore.jks
keytool -import -file Wssip.cer -alias wssip -keystore client-truststore.jks

server-truststore.jks:
keytool -import -file Alice.cer -alias alice -keystore server-truststore.jks
keytool -import -file Wssip.cer -alias wssip -keystore server-truststore.jks

sts-truststore.jks:
keytool -import -file Alice.cer -alias alice -keystore sts-truststore.jks
keytool -import -file Bob.cer -alias bob -keystore sts-truststore.jks
keytool -import -file Wssip.cer -alias wssip -keystore sts-truststore.jks
