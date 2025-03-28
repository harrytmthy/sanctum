# :core:cryptography

Sanctum's cryptographic core. This module powers **offline-first**, **zero-knowledge**, and
**privacy-preserving** journal encryption with minimal runtime state.

## Overview

This module handles:

- Session-based AES encryption
- PIN-based key derivation (PBKDF2)
- Secure salt and metadata storage
- Guest and logged-in key isolation
- Stateless runtime encryption/decryption

## Key Concepts

### Guest Mode

- A **randomized AES key** is generated locally.
- This key encrypts/decrypts all journal data while offline.
- The key is stored in **EncryptedSharedPreferences**.
- Once uninstalled, the key is gone, and recovery is impossible.

### Logged-in Mode

- **On PIN setup:**
  - A **salt** is generated.
  - A **derived key** is created from the PIN + salt.
  - This key encrypts the session AES key, resulting in an **Encrypted Session Key**.
  - Both salt and Encrypted Session Key are stored locally and uplaoded to the backend.
- **On login:** PIN + stored salt is used to re-derive key, then decrypt Encrypted Session Key,
  and finally load **Session Key** into memory.

### In-Memory Session Key

- **Session Key** is loaded only after successful PIN entry.
- It is used for runtime encryption/decryption.
- Not persisted to disk.

## Components

### `KeyHandler`

- Generates randomized AES keys for both guest and logged-in modes.
- Derives keys from PIN + salt using PBKDF2.
- Stateless utility (no side effects).

### `Cryptography`

- Performs encryption/decryption given a `SecretKey`.
- Stateless, reusable across modules.

### `CryptographyRepository`

- Source of truth for all crypto logic:
  - `generateAndSaveSalt()`
  - `createAndSaveEncryptedSessionKey()`
  - `loadSessionKey()`
  - `encrypt()`, `decrypt()`, and `clearStorage()`
- Bridges crypto operations with EncryptedPrefs.
- Handles transition between guest and logged-in sessions.

### `EncryptedPrefs`

- Secure persistence layer (EncryptedSharedPreferences).
- Before PIN setup, stores Guest Key. After PIN setup, stores both Salt and Encrypted Session Key

## Design Principles

- **Zero-Knowledge by Default**  
  Backend can never decrypt user data. Only user holds the PIN.

- **Session-Based Architecture**  
  No sensitive key lives in memory longer than needed.

- **Offline-First, Sync-Optional**  
  Works fully offline. Sync handled externally.

- **Single Entry Point**  
  All key lifecycle logic flows through `CryptographyRepository`.

## Test Coverage

Thoroughly tested with:
- Guest key generation
- PIN-based session handling
- Encryption/decryption (including edge cases)
- Error propagation via `GeneralSecurityException`