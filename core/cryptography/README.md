# :core:cryptography

This module provides the cryptographic backbone for Sanctum's **privacy-first, offline-first**
architecture. It handles:

- AES secret key generation and storage using Android Keystore.
- Password/PIN-based key derivation (PBKDF2).
- Biometric-gated key usage support.
- Stateless runtime encryption/decryption via injected keys.

## Components

### SecretKeyManager

- Generates or loads AES keys stored in Android Keystore (for guest mode).
- Derives exportable AES keys from user-defined PIN (PBKDF2).
- Supports biometric-gated Keystore key access.
- Stateless interaction with Keystore and KDF systems.

### CryptographyManager

- Runtime encryption/decryption with externally provided `SecretKey`.
- Session-level logic, does not persist keys.
- Works seamlessly with both guest and authenticated flows.

### EncryptedPrefs

- Secures on-device persistence (e.g. salt, encrypted AES key).
- Used in offline-first recovery scenarios.
- Optional mirror with backend if sync is enabled.

## Key Flows

### Guest Mode

- User can create journal entries without logging in.
- Journal entries are encrypted via generated AES key and stored locally.
- Uninstalling the app deletes the key, making the data irrecoverable.

### Authenticated Mode

- After logging in, user is required to enter PIN.
- On PIN setup, a derived key is created to wrap the encryption key to protect all data.
- Encrypted AES key and salt are stored securely via EncryptedPrefs.

### Biometric Integration (Optional)

- Keystore key can be configured with biometric requirement.
- **SecretKeyManager** supports such creation flows.
- UI prompts handled externally, logic supported internally.

## Design Principles

- **Zero-Knowledge by Default**  
  Backend holds encrypted data it cannot decrypt.

- **Modular Cryptography**  
  Clean separation between key generation, storage, and usage.

- **Offline-First, Sync-Aware**  
  Encryption and journaling work offline.

- **Stateless by Design**  
  Keys are never persisted in memory. They are derived or injected as needed.