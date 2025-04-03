# `:core:network`

Sanctum's centralized networking core. This module provides:

- Robust HTTP networking built on Retrofit and OkHttp.
- Structured API response handling.
- Fail-fast network interception.
- Network connectivity monitoring.
- Comprehensive dependency injection setup.

## Overview

This module encapsulates all networking functionality required by Sanctum, designed for
offline-first and reliable synchronization. It promotes:

- Explicit error handling.
- Efficient connectivity management.
- Clear separation between networking layers and features.

## Configuration

The base URL is defined in `secrets.properties`, facilitating easy switching between development
and production:

```
BACKEND_URL="https://sanctum-journal-dev-default-rtdb.asia-southeast1.firebasedatabase.app/"
```

The URL for production is swapped automatically by CI scripts during release builds.

## Permissions

Network state permission is declared to enable accurate connectivity status:

```
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Components

### ApiResponse Handling

Custom adapters for Retrofit responses, encapsulating network calls into explicit states:

- `ApiResponseCall`: Wraps Retrofit calls, converting responses and exceptions into structured API
  responses (`Success`, `ApiError`, `NetworkError`, `NoInternetError`, and `UnknownError`).
- `ApiResponseCallAdapter` & `ApiResponseCallAdapterFactory`: Integrate with Retrofit's adapter
  factory system, seamlessly handling response transformations.

### ConnectivityManagerNetworkMonitor

A utility for network connectivity monitoring, leveraging Android's `ConnectivityManager`:

- Provides real-time connectivity updates using `StateFlow<Boolean>`.
- Efficiently registers/unregisters network callbacks.
- Automatic retry mechanism in case of monitoring errors.

### NoInternetInterceptor

Interceptor enforcing fail-fast network behavior:

- Immediately throws `NoInternetException` if offline.
- Integrated seamlessly into OkHttp's interceptor chain via DI.

## Dependency Injection

Using Hilt and multibinding strategies, interceptors are flexibly managed and easily extendable:

### Interceptors DI

Interceptors are categorized clearly:

- **Application Interceptors:** Modify application-level requests (e.g., logging, offline checks).
- **Network Interceptors:** Modify network-level responses and requests.

### Retrofit & OkHttp DI

Provided with efficient configurations:

- Lazy initialization (`Lazy<Call.Factory>`) to prevent OkHttpClient initialization at main thread.
- Json serialization setup (`kotlinx.serialization`).

## Test Doubles (Fakes)

To support better testing without brittle mocks:

- `FakeNetworkMonitor`: Provides controllable connectivity state.
- `FakeInterceptorChain`: Simulates OkHttp interceptor chains for isolated interceptor testing.

Example test:

```
@Test
fun `intercept when offline should throw NoInternetException`() {
    FakeNetworkMonitor.setIsOnline(false)

    assertFailsWith<NoInternetException> {
        interceptor.intercept(FakeInterceptorChain)
    }
}
```

## Dependencies

Core dependencies:

- Retrofit, OkHttp, kotlinx.serialization, Coroutines.
- Timber logging.
- Coil for image loading (with SVG support).

## Future Enhancements

- Additional interceptors (e.g., Authentication).
- Network retry strategies.
- Enhanced logging and diagnostics.