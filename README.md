# Smart Parking System

Backend service for managing parking lots, parking sessions, and fee calculation. The project is built with Java 21, Spring Boot 3.5, Gradle, and an in-memory H2 database.

## Overview

The system supports:

- parking lot administration
- level and slot management
- vehicle check-in and check-out
- active parking session tracking
- fee calculation by vehicle type through the Strategy pattern

## Design Summary

### Architecture

The application is split into clear layers:

- `api` for controllers, request/response DTOs, and HTTP error handling
- `application` for business services and application-level exceptions
- `domain` for entities, enums, pricing strategies, and compatibility rules
- `infrastructure` for Spring Data JPA repositories

### Core Domain Model

- `ParkingLot` contains one or more `ParkingLevel`
- `ParkingLevel` contains multiple `ParkingSlot`
- `ParkingSlot` has a type and status: `AVAILABLE`, `OCCUPIED`, `UNAVAILABLE`
- `ParkingSession` represents a vehicle stay from check-in to check-out

### Main Design Decisions

- Fee calculation uses `FeeCalculationStrategy` so new vehicle-specific pricing rules can be added without changing checkout logic.
- Slot compatibility is isolated in `VehicleSlotCompatibilityPolicy`.
- Parking session records store a snapshot of lot/level/slot data, so historical sessions remain valid even if the parking structure changes later.
- Check-in uses a first-fit allocation strategy: the first compatible available slot ordered by level and slot code is assigned.
- Billing is calculated per started hour with a minimum of one hour.

## Assumptions

- Check-in requires `parkingLotId` because the system may contain multiple parking lots.
- Check-out is done by `sessionId`.
- `HANDICAPPED` slots are modeled, but they are not used for automatic assignment because the assignment did not include eligibility rules.
- Slot compatibility rules are:
  - `MOTORCYCLE` -> `MOTORCYCLE`, `COMPACT`, `LARGE`
  - `CAR` -> `COMPACT`, `LARGE`
  - `TRUCK` -> `LARGE`

## Tech Stack

- Java 21
- Spring Boot 3.5.9
- Gradle Wrapper
- Spring Web
- Spring Data JPA
- H2 in-memory database
- JUnit 5 and MockMvc

## How to Run

### Start the application

On Windows:

```bash
gradlew.bat bootRun
```

On macOS/Linux:

```bash
./gradlew bootRun
```

The API starts on `http://localhost:8080`.

### Swagger UI / OpenAPI

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Run tests

On Windows:

```bash
gradlew.bat test
```

On macOS/Linux:

```bash
./gradlew test
```

### H2 Console

- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:parkingdb`
- Username: `sa`
- Password: empty

## API Overview

### Parking lot administration

- `GET /api/v1/parking-lots`
- `GET /api/v1/parking-lots/{parkingLotId}`
- `POST /api/v1/parking-lots`
- `DELETE /api/v1/parking-lots/{parkingLotId}`
- `POST /api/v1/parking-lots/{parkingLotId}/levels`
- `DELETE /api/v1/parking-lots/{parkingLotId}/levels/{levelId}`
- `POST /api/v1/parking-lots/{parkingLotId}/levels/{levelId}/slots`
- `DELETE /api/v1/parking-lots/{parkingLotId}/levels/{levelId}/slots/{slotId}`
- `PATCH /api/v1/parking-lots/{parkingLotId}/levels/{levelId}/slots/{slotId}/status`

### Parking sessions

- `POST /api/v1/parking-sessions/check-in`
- `POST /api/v1/parking-sessions/{sessionId}/check-out`
- `GET /api/v1/parking-sessions/active`

## Example API Calls

### 1. Create a parking lot

```bash
curl -X POST http://localhost:8080/api/v1/parking-lots \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Central Plaza\"}"
```

### 2. Add a level

```bash
curl -X POST http://localhost:8080/api/v1/parking-lots/{parkingLotId}/levels \
  -H "Content-Type: application/json" \
  -d "{\"levelNumber\":1}"
```

### 3. Add slots

```bash
curl -X POST http://localhost:8080/api/v1/parking-lots/{parkingLotId}/levels/{levelId}/slots \
  -H "Content-Type: application/json" \
  -d "{\"slotCode\":\"C-01\",\"slotType\":\"COMPACT\"}"
```

```bash
curl -X POST http://localhost:8080/api/v1/parking-lots/{parkingLotId}/levels/{levelId}/slots \
  -H "Content-Type: application/json" \
  -d "{\"slotCode\":\"L-01\",\"slotType\":\"LARGE\"}"
```

### 4. Check in a vehicle

```bash
curl -X POST http://localhost:8080/api/v1/parking-sessions/check-in \
  -H "Content-Type: application/json" \
  -d "{\"parkingLotId\":{parkingLotId},\"licensePlate\":\"AA-1234-BB\",\"vehicleType\":\"CAR\"}"
```

### 5. View active sessions

```bash
curl http://localhost:8080/api/v1/parking-sessions/active
```

### 6. Check out a vehicle

```bash
curl -X POST http://localhost:8080/api/v1/parking-sessions/{sessionId}/check-out
```

## Testing

The test suite covers:

- compatibility rules between vehicle types and slot types
- rounded hourly fee calculation
- service-level parking flow and conflict handling
- end-to-end API flow with MockMvc

## Known Limitations / TODO

- No authentication or authorization layer
- No pagination or filtering for administrative read endpoints
- No special eligibility workflow for `HANDICAPPED` slots
- Allocation strategy is first-fit only; no optimization for proximity or occupancy balancing
- Historical sessions are kept in-memory only because H2 is used for this assignment
