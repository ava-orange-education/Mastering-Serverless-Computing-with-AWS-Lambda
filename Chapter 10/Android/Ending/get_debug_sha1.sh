#!/bin/bash

# Default path for the debug keystore
KEYSTORE_PATH="$HOME/.android/debug.keystore"
ALIAS_NAME="androiddebugkey"
STOREPASS="android"
KEYPASS="android"

# Run keytool to get the SHA-1 fingerprint
keytool -list -v -keystore "$KEYSTORE_PATH" -alias "$ALIAS_NAME" -storepass "$STOREPASS" -keypass "$KEYPASS" | grep -E "SHA1:"

# If you need the SHA-256 fingerprint as well, uncomment the line below
# keytool -list -v -keystore "$KEYSTORE_PATH" -alias "$ALIAS_NAME"
