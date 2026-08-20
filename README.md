# Cab Booking Service - Coding Challenge

## Problem Statement

Customers request rides from wherever they are. Drivers are spread across the city — some idle,
some mid-trip, some off shift — and the service knows each driver's last known position.

When a ride is requested, the system must pick the **nearest driver who is actually free**,
assign them to that ride, and take them out of the pool so no second customer gets the same cab.
If no suitable driver is in range, the customer must be told so explicitly.

**Your task:** implement [`DriverMatchingService.findNearestAvailableDriver`](src/main/java/com/example/cabbooking/service/DriverMatchingService.java#L27) and
[`RideAssignmentService.assignDriver`](src/main/java/com/example/cabbooking/service/RideAssignmentService.java#L30).

## Assignment Logic

1. **Filter** — only drivers with status `AVAILABLE` are candidates. `BUSY` and `OFFLINE` are
   ignored however close they are.
2. **Cap** — drop anyone beyond [`cab.dispatch.max-pickup-distance-km`](src/main/resources/application.properties#L21) (default 15 km,
   bound in [`DispatchProperties`](src/main/java/com/example/cabbooking/config/DispatchProperties.java#L8)). A driver
   *exactly* at the limit is still eligible.
3. **Pick** — nearest wins. On a tie, lowest driver id wins, so results are deterministic.
4. **Commit** — in one transaction: ride goes `REQUESTED` → `DRIVER_ASSIGNED` with the driver id
   set, and the driver goes `AVAILABLE` → `BUSY`. Both or neither.
5. **Report** — no eligible driver means `503`, not an empty success. The ride stays `REQUESTED`
   and can be retried.

Two ride requests arriving at the same instant must never end up with the same driver.

## API Reference

**Endpoint:** `POST /api/rides`

```json
{
  "customerId": 101,
  "pickupLatitude": 28.6139,
  "pickupLongitude": 77.2090
}
```

`201 Created`

```json
{
  "rideId": 1,
  "driverId": 1,
  "status": "DRIVER_ASSIGNED"
}
```

| Method | Path | Notes |
|---|---|---|
| `POST` | `/api/rides` | Creates the ride and assigns a driver. `503` if none eligible, `400` if the payload is invalid. |
| `GET` | `/api/rides/{rideId}` | `404` if unknown. |
| `POST` | `/api/rides/{rideId}/cancel` | Releases the driver back to `AVAILABLE`. |
| `GET` | `/api/drivers` | All drivers. |
| `GET` | `/api/drivers/{driverId}` | `404` if unknown. |
| `PATCH` | `/api/drivers/{driverId}/status` | Body `{ "status": "AVAILABLE" }`. |

All failures return the same body, produced by `GlobalExceptionHandler`:

```json
{ "status": 503, "error": "Service Unavailable", "message": "No available driver near ride 1" }
```

## Data Model

**Driver**

| Column | Type | Description |
|---|---|---|
| `id` | bigint | Primary key |
| `name` | string | Driver name |
| `latitude` / `longitude` | double | Last known position |
| `status` | enum | `AVAILABLE` / `BUSY` / `OFFLINE`. Indexed as `idx_driver_status`. |

**Driver Status Lifecycle**

```
AVAILABLE  ──assigned to a ride──▶  BUSY  ──ride ends / cancelled──▶  AVAILABLE
    │                                                                      ▲
    └──────────────────── goes off shift ──▶ OFFLINE ──comes online────────┘
```

Only `AVAILABLE` is eligible for assignment. `OFFLINE` must never be assigned.

**RideRequest**

| Column | Type | Description |
|---|---|---|
| `id` | bigint | Primary key |
| `customer_id` | bigint | Who requested the ride |
| `pickup_latitude` / `pickup_longitude` | double | Where they are waiting |
| `status` | enum | `REQUESTED` / `DRIVER_ASSIGNED` / `CANCELLED` / `COMPLETED` |
| `assigned_driver_id` | bigint | Set by `assignTo(...)`, together with the status |

**Ride Status Lifecycle**

```
REQUESTED ──▶ DRIVER_ASSIGNED ──▶ COMPLETED
    │                │
    └────────────────┴──▶ CANCELLED
```

A ride is created in `REQUESTED` and leaves it only by gaining a driver or being cancelled.

Coordinates are validated in the `Location` value object: latitude ∈ `[-90, 90]`, longitude ∈
`[-180, 180]`, no `NaN` or infinity.

## Seed Data

Six drivers clustered around a single city centre. Distances are measured from the pickup point
used in the examples above (`28.6139, 77.2090`).

| id | Name | Status | Distance | Eligible? |
|---|---|---|---|---|
| 1 | Emma | `AVAILABLE` | 0.12 km | ✅ **correct answer** |
| 2 | Liam | `AVAILABLE` | 1.27 km | ✅ |
| 3 | Noah | `BUSY` | 0.04 km | ❌ closest of all, but busy |
| 4 | Olivia | `OFFLINE` | 0.02 km | ❌ closest of all, but off shift |
| 5 | Sophia | `AVAILABLE` | 18.72 km | ❌ beyond the 15 km radius |
| 6 | Lucas | `AVAILABLE` | 19.80 km | ❌ beyond the 15 km radius |

One booking at that pickup point exercises both the status filter and the radius cap.

## Getting Started

The app is a single Spring Boot service with an in-memory H2 database — no external
services to stand up either way. Run it straight from Gradle, or in Docker.

Stack: Spring Boot 4.0.x, Spring Web MVC, Spring Data JPA, H2 in-memory, JUnit 5 + AssertJ +
Mockito + MockMvc.

### Option A — Without Docker (Gradle)

**Prerequisites:** JDK 17 (the Gradle wrapper is bundled, so no separate Gradle install).

```bash
./gradlew bootRun     # http://localhost:8080, H2 console at /h2-console
./gradlew test
```

On Windows use `gradlew.bat` instead of `./gradlew`.

### Option B — With Docker

**Prerequisites:** Docker 20.10+ (with the Compose plugin). No local JDK needed — the
`Dockerfile` is a two-stage build: stage one compiles the JAR on `eclipse-temurin:17-jdk`,
stage two runs it on a JRE image as a non-root `spring` user.

```bash
docker compose up --build          # build + run, http://localhost:8080
docker compose down                # stop and remove the container
```

Configuration is passed as environment variables (Spring's relaxed binding maps them onto the
properties). `docker-compose.yml` already sets:

| Variable | Default | Maps to |
|---|---|---|
| `CAB_DISPATCH_MAX_PICKUP_DISTANCE_KM` | `15.0` | [`cab.dispatch.max-pickup-distance-km`](src/main/resources/application.properties#L21) |
| `SPRING_PROFILES_ACTIVE` | `default` | active Spring profile |
| `JAVA_OPTS` | `-XX:MaxRAMPercentage=75.0` | JVM flags used by the entrypoint |


### Smoke test

```bash
curl -s http://localhost:8080/api/drivers
```

### Run test cases
```bash
./gradlew test --tests '*DriverMatching*'
./gradlew test --tests '*RideAssignment*'
```

## Your Task

You need to write two methods. Both are empty in the code and marked with a
`TODO (candidate)` comment that lists the tools you can use:

| Method | Responsibility |
|---|---|
| [`DriverMatchingService.findNearestAvailableDriver(Location)`](src/main/java/com/example/cabbooking/service/DriverMatchingService.java#L27) | Pure read. Return the nearest eligible driver, or `Optional.empty()`. |
| [`RideAssignmentService.assignDriver(Long rideRequestId)`](src/main/java/com/example/cabbooking/service/RideAssignmentService.java#L30) | Owns the writes and the transaction. |

**You can** add a new method to a repository if you need one.

**You should not** add new database tables, new API endpoints, a cache, or a map/geo search
library. Stick to the two methods above.

## Evaluation Criteria

| Area | What we look for |
|---|---|
| **Correctness** | Every rule above, including the tie-break and the inclusive radius boundary. |
| **Code quality** | Idiomatic Java — streams, `Optional`, meaningful exceptions.  |
| **Performance** | Filtering in the query, not in Java. No N+1. What happens at 100k drivers? |
| **Database** | Right use of indexes, transactions, and locking. |
| **Concurrency** | Does the solution survive two simultaneous requests — and a second instance of the service? |
| **Discussion** | Trade-offs said out loud beat trade-offs guessed silently. |

## Key Discussion Topics

- How would you match against 100k drivers and 5k bookings a minute?
- Two requests, one driver — transaction, pessimistic lock, optimistic lock, or conditional
  update? What does each cost?
- What happens to a `BUSY` driver if the service crashes mid-assignment?
