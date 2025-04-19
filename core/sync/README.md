## Overview

This module provides synchronization logic between local storage and Firestore, optimized for
offline-first behavior. It uses WorkManager for safe background execution and throttled sync
triggers to minimize redundant writes.

## Responsibilities

- Manage pending sync entries using `entries_sync` table.
- Use `SyncManager` to enqueue a one-time sync via WorkManager.
- Use `SyncWorker` to send updates to Firestore.
- Track responses and remove only entries confirmed to be successfully synced.

## Key Concepts

- **Debounced Syncing:** An observer watches pending entries. After a delay, a one-time sync
  is triggered.
- **Safe Upserts:** Before deleting from the sync table, it compares response timestamp with
  current entry’s `updatedAt`.
- **Batching:** Uses Firestore `batchWrite` endpoint instead of sending one entry per request.
- **Resilience:** The worker only deletes synced entries if `status.code == 200` and
  `response.updatedAt >= local.updatedAt`.

## Worker Implementation

- Worker class: `SyncWorker`
- Dispatcher: Uses `DispatchersProvider.io`
- Trigger: Enqueued via `SyncManager.sync()`
- Dependencies are injected via Hilt.

## DAOs Involved

- `EntrySyncDao`: Tracks pending entries.
- `EntryDao`: Provides access to all local entries.

## Firestore Interaction

- **Upsert:** Uses `documents:batchWrite` endpoint.
- **Query:** Uses `documents:runQuery` endpoint with pagination.

## Data Factories

Factories abstract the request payload creation:

- `JournalEntriesBatchWriteRequestFactory`: Builds batch write requests from local entities.
- `JournalEntriesStructuredQueryRequestFactory`: Builds structured query with pagination support.