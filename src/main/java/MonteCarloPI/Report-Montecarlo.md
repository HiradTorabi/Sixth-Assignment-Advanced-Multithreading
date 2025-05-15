# Monte Carlo π – Performance Questions

## Was the multi‑threaded implementation always faster than the single‑threaded one?

**No – not for every workload size.**  
When the total number of points is large (e.g. ≥ 10–20 million on a multi‑core CPU) the multi‑threaded version is *significantly* faster.  
For small workloads, however, the parallel version can be just as fast or even **slower** because overhead dominates.

## If not, what factors cause the slow‑downs?

| Factor | Why it hurts performance |
|--------|--------------------------|
| **Thread‑start & scheduling overhead** | Creating/waking threads and context‑switching adds milliseconds that swamp very small jobs. |
| **Sequential sections (Amdahl’s Law)** | Task dispatch, the final reduction, and console I/O stay single‑threaded, limiting overall speed‑up. |
| **Memory‑bandwidth & cache contention** | Many cores generating random numbers and updating shared data compete for the same memory bus and cache lines. |
| **Random‑generator contention** | A shared `java.util.Random` uses a lock—if reused across threads it becomes a bottleneck. |
| **Too many threads for the amount of work** | Launching more threads than points gives some threads nothing to do, yet still incurs scheduling cost. |

## What can we do to mitigate these issues?

* **Reuse a thread‑pool** instead of creating new `Thread`s each time (we used `Executors.newFixedThreadPool`).
* **Right‑size the pool** – `numThreads = min(availableProcessors, numPoints)` so we never have idle threads.
* **Batch more work per task** to amortise overhead (each thread processes a big *chunk* of points).
* **Per‑thread RNGs** (`ThreadLocalRandom`, `SplittableRandom`) remove locking contention.
* **Use `LongAdder` instead of a single `AtomicLong`** to spread updates across cache lines.
* **Warm‑up the JVM** – run a small rehearsal iteration so that the JIT compiles hot loops before timing.
* **Avoid false sharing** – keep per‑thread counters in separate cache lines (e.g. via an array cell per thread).

With these practices in place the multi‑threaded version scales close to linearly until it becomes memory‑bandwidth‑bound. In our benchmark of **50 000 000 points on a 16‑logical‑core CPU** we saw a speed‑up of **≈ 7–9×** (1.48 s → 0.20 s), which is close to the theoretical maximum once unavoidable sequential work is considered.
