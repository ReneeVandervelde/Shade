Change Log
==========

2.0.0
-----

Version 2 of Shade is a multi-platform rewrite of the library.
Because of this, JVM-specific dependencies have been removed in favor of
some multi-platform types.
Breaking API changes are kept to a minimum where possible. Previously
deprecated references are now compile errors.

### Changed:
 - ThreeTen date and time objects have been replaced with [kotlinx-datetime]
 - `Instant` types converted to `LocalDateTime` types, due to lack of timezone support
 - `LightState.brightness` is now nullable.
 - Scene `data` fields are now strictly typed as an `AppData` object, rather
   than a key/value map.
 - Serialization module has been merged into structures module.


### Removed:
 - OkHttp has been removed, and subsequently references to the client have been
   removed from the API, including the `http` module.
 - `UpdateState.lastInstall` has been removed in favor of the nullable
   `UpdateState.lastKnownInstall`
 - `Schedule.time` is now an error in favor of `localTime`
 - `HueProperties`, not used by the public API, has been removed
 - `ShadeCompositeException` no longer has `printStackTrace` methods.

[kotlinx-datetime]: https://github.com/Kotlin/kotlinx-datetime

1.2.0
-----

 - Update ColorMath dependency to 2.0.0
 - Update to Kotlin 1.4.20
 - Update OkHttp to 4.9.0
 - Update ThreeTenBP to 1.5.0

1.1.3
-----

### Fixed:

 - Handle null `lastInstall` fields on hue lights' update state.

1.1.2
-----

### Fixed:

 - Migrate Discover functionality from old nupnp to new endpoint (discover.meethue.com)

1.1.1
-----

### Fixed:

 - Unhandled JsonDataException when a room is empty.

### Other Changes:
 - Update ThreeTenBP to 1.4.4
 - Update OkHttp to 4.8.0
 - Update Moshi to 1.9.3
 - Update Coroutines to 1.3.8

1.1.0
-----

### Other Changes:
 - API Requests for modifying light state are rate limited by 100ms.
 - API Requests for modifying group state are rate limited by 1s.

1.0.0
-----

Initial Release SDK and CLI with support for:
 - Light Control
 - Group Control
 - Scenes
 - Schedules
