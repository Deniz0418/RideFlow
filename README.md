# RideFlow — Java Console Ride-Hailing Simulator

**Java 21 • Apache NetBeans compatible • Git-friendly • No Maven • No external dependencies**

## What is included?
- A **working starter skeleton**, not a completed project: one ride can be requested, matched, simulated to completion and paid (simulated cash).
- Core classes and stable service interfaces for all four team members.
- A basic second matching strategy, queue, cancellation, console and starter analytics.
- Ant build for repeatable compilation in NetBeans or another IDE.
- Dependency-free smoke tests. These are **not** a comprehensive test suite.
- UML diagrams and source files under `docs/`.

**Important:** This is a classroom simulation. It does **not** offer GPS, real driver movement, live payments, real-world routing, simultaneous threads or persistence. See `docs/FEATURE_BACKLOG.md` for what remains to build.

## Getting started: Apache NetBeans
1. Install **JDK 21** (or newer with Java 21 support) and Apache NetBeans.
2. Extract this ZIP. Select **File > Open Project**, choose the **RideFlow** folder (the folder containing `build.xml` and `nbproject`).
3. This is a **NetBeans Java Free-Form Ant** project (not Maven). If your NetBeans edition does not recognize it, choose **File > New Project > Java with Ant > Java Project with Existing Sources**, select the `src` and `test` folders, set main class `rideflow.app.Main` and Java 21.
4. Run with **Run Project** or right-click `build.xml` to run the `run` Ant target.
5. Use `P001`, `P002` or `P003` for demo passengers. Available categories: ECONOMY, PREMIUM and XL.

From a terminal with **JDK 21 and Ant**:

```bash
ant clean compile
ant test
ant run
ant jar
java -jar dist/RideFlow.jar
```

## First hands-on demo
Request Economy for passenger `P001` from `(0,0)` to `(4,0)` for 2 passengers. The nearest driver `D001` should be assigned. Advance the virtual clock by 6 minutes, then inspect completed rides and the report.

## Repo contents

```text
RideFlow/
├─ build.xml                  # Shared Ant build, not Maven
├─ nbproject/project.xml      # NetBeans project metadata
├─ src/rideflow/
│  ├─ model/                  # Shared model: users, locations, vehicles
│  ├─ fleet/                  # Member 1
│  ├─ simulation/             # Member 1 (coordinate with Member 3)
│  ├─ booking/                # Member 2
│  ├─ app/                    # Member 2 + shared composition
│  ├─ dispatch/               # Member 3
│  ├─ finance/                # Member 4
│  └─ analytics/              # Member 4
├─ test/rideflow/tests/       # No dependency on JUnit yet
└─ docs/                      # UML, team contracts, backlog and Git guide
```

## Rules for teammates
1. **Agree on service method signatures** in `docs/INTERFACES.md` before changing shared classes.
2. Work on your own Git branch and submit pull requests; do not commit directly to `main`.
3. Do **not** rename packages or change public methods without telling other members.
4. Run `ant test` before opening a pull request.
5. Avoid merging IDE-specific settings and generated `build/`, `dist/` directories.
6. Tests should demonstrate invalid capacity, double assignment, queue/cancellation, fare and payment correctness.

Start with **`docs/TEAM_HANDOFF.md`**, **`docs/UML_DOMAIN.svg`** and **`docs/UML_SERVICES.svg`**.
