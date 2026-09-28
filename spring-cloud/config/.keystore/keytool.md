### [example] create keystore
```
 keytool -genkeypair -alias ${alias} -keyalg RSA \
  -dname "CN=${CommonName},OU=${OrganizationalUnit},O=${Oranization},L=${Locality},S=${State},C=${Country}" \
  -keypass ${keypass} -keystore ${fileName}.jks -storepass ${storepass}
```

### [example] create cert file
```
keytool -export -alias camusConfigEncKey -keystore camusConfigEncKey.jks -rfc -file ${trustServer}.cer
```

### [example] create public key file 
```
keytool -import -alias camusConfigEncKey -file ${trustServer}.cer -rfc -keystore ${publicKey}.jks 
```