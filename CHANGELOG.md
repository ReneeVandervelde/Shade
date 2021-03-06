Change Log
==========

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
