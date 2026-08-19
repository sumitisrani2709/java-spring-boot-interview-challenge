# Cab Booking Service — Nearest Driver Assignment

A deliberately small Spring Boot service used as a **one-hour technical interview exercise**.

It does exactly one interesting thing: when a customer requests a ride, it finds the nearest
available driver and assigns them. Everything else — payments, vehicles, pricing, auth, trip
history — is intentionally out of scope.

---

## Problem statement

> When a customer requests a ride, find the nearest **available** driver to the pickup location
> and assign that driver to the ride.

## Business requirements

1. Only drivers with status `AVAILABLE` may be assigned. `BUSY` and `OFFLINE` drivers are ignored,
   even when they are physically closest.
2. Distance is the great-circle distance between the pickup point and the driver's last known
   position.
3. The **nearest** eligible driver wins.
4. A driver farther away than `cab.dispatch.max-pickup-distance-km` (default **15 km**) is not
   eligible. A driver exactly at that distance still is.
5. Ties are broken deterministically: same distance → **lowest driver id** wins.
6. If no driver is eligible, the API must say so clearly rather than returning an empty success.
7. Once assigned, the driver becomes `BUSY` and cannot be given to another ride.
8. The ride moves `REQUESTED` → `DRIVER_ASSIGNED`, and the driver change happens with it —
   either both or neither.
9. Two ride requests arriving at the same instant must never end up with the same driver.

---

## Tech stack

| | |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.x |
| Build | Gradle (Groovy DSL), JAR packaging |
| Web | Spring Web (MVC) |
| Persistence | Spring Data JPA |
| Database | H2, in-memory |
| Tests | JUnit 5, AssertJ, Mockito, MockMvc |

No Docker, no external services, no network access needed beyond dependency download.

---

## How to run

```bash
./gradlew bootRun
```

The app starts on <http://localhost:8080> with six seeded drivers (see
`src/main/resources/data.sql`) and an H2 console at <http://localhost:8080/h2-console>
(JDBC URL `jdbc:h2:mem:cabbooking`, user `sa`, empty password).

## How to run the tests

```bash
./gradlew test                                  # everything
./gradlew test --tests '*DriverMatching*'       # one class
./gradlew test --tests '*RideAssignment*'
```

An HTML report is written to `build/reports/tests/test/index.html`.

> On a fresh checkout **the suite fails**. That is intentional — the failures describe the work
> to be done.

---

## API

### Create a ride request

```http
POST /api/rides
Content-Type: application/json

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

| Situation | Status |
|---|---|
| Driver found and assigned | `201 Created` |
| No eligible driver | `503 Service Unavailable` |
| Missing / out-of-range coordinates, missing `customerId` | `400 Bad Request` |
| Ride already has a driver | `409 Conflict` |
| Unknown ride or driver id | `404 Not Found` |

### Other endpoints

```http
GET   /api/rides/{rideId}
POST  /api/rides/{rideId}/cancel          # releases the driver back to AVAILABLE
GET   /api/drivers
GET   /api/drivers/{driverId}
PATCH /api/drivers/{driverId}/status      { "status": "AVAILABLE" }
```

### Try it end to end

```bash
curl -s localhost:8080/api/drivers

curl -s -X POST localhost:8080/api/rides \
  -H 'Content-Type: application/json' \
  -d '{"customerId":101,"pickupLatitude":28.6139,"pickupLongitude":77.2090}'
```

---

## Repository layout

```
src/main/java/com/example/cabbooking/
├── CabBookingApplication.java
├── config/      DispatchProperties          — cab.dispatch.* settings (max pickup radius)
├── domain/      Driver, DriverStatus, RideRequest, RideStatus
├── dto/         Location, CreateRideRequest, RideResponse, DriverResponse, ...
├── exception/   domain exceptions + GlobalExceptionHandler (maps them to HTTP)
├── repository/  DriverRepository, RideRequestRepository
├── service/     DistanceCalculator / HaversineDistanceCalculator   (done)
│                DriverMatchingService     — picks the driver        <-- TODO
│                RideAssignmentService     — commits the assignment  <-- TODO
│                RideService, DriverService                          (done)
└── web/         RideController, DriverController
```

---

## Branches

| Branch | What it is |
|---|---|
| `main` | Reference starting point plus interviewer material. |
| `candidate-task` | **What the candidate works on.** Same code, no interviewer notes. |
| `review-pr` | A pull request from a teammate, to be reviewed in the last part of the session. |

---

## Candidate instructions

You have roughly an hour. The project compiles and runs; two methods are missing.

**Your task**

1. `DriverMatchingService.findNearestAvailableDriver(Location)` — return the nearest eligible
   driver, or `Optional.empty()`.
2. `RideAssignmentService.assignDriver(Long rideRequestId)` — assign that driver to the ride and
   take them out of the pool.

Both are marked with a `TODO (candidate)` block that lists the building blocks available to you.
`./gradlew test` tells you when you are done.

**What we are looking for**

- Correct behaviour, including the edge cases in the requirements above.
- Idiomatic Java — collections, streams, `Optional`, exceptions used deliberately.
- Sensible use of Spring: layering, dependency injection, transactions.
- Awareness of what the *database* should be doing versus what your JVM should be doing.
- Your reasoning out loud. Talking through a trade-off scores better than silently guessing.

**What we are not looking for**

- New entities, extra endpoints, caching layers, or a geospatial index.
- Perfect formatting or exhaustive Javadoc.
- Finishing every stretch goal. A correct, well-explained core beats a rushed everything.

**Notes**

- `ConcurrentAssignmentTest` is `@Disabled`. It is a discussion prop; enable it if you want to
  check your work against it.
- You may add repository methods, helper classes, or tests. Say why when you do.

## Interviewer instructions

Hand the candidate the `candidate-task` branch — it has no spoilers in it. Keep
`INTERVIEWER_GUIDE.md` (on `main`) open for yourself; it contains the reference solution, the
list of planted problems in `review-pr`, prompts, and a scoring rubric.

Suggested timeline:

| Time | Activity |
|---|---|
| 0–10 min | Walk through the requirements; candidate reads the code and asks questions. |
| 10–35 min | Candidate implements the two TODO methods. |
| 35–45 min | Run the suite, fix what is red, talk through the edge cases. |
| 45–60 min | Switch to `review-pr` and review it as a real pull request. |

---

## Expected scope

Small. The whole task is two method bodies, possibly one extra repository method, and the
conversation around them. If a candidate is designing a new subsystem, redirect them.
