# Report – Answers to the Theoretical Questions

## 1) What is the program output and why?

```text
Atomic Counter: 2000000
Normal Counter: a value smaller than 2 000 000 (varies each run)
```

* Two threads (`t1` and `t2`) each increment both counters one million times.
* The `atomicCounter` variable is an `AtomicInteger`; every increment is **atomic**, so after 2 × 1 000 000 increments it prints **2 000 000**.
* The `normalCounter` is a plain `int`. The operation `normalCounter++` is really three steps—read, add 1, write. Threads can interleave these steps (a race condition), so some increments are lost and the final value is usually **less than 2 000 000**.

---

## 2) What is the purpose of `AtomicInteger` in this code?

`AtomicInteger` provides integer operations such as `incrementAndGet()`, `getAndAdd()`, and `compareAndSet()` that are **atomic and lock‑free**. It lets multiple threads safely update a shared variable without the heavier cost of an external lock (`synchronized` or `Lock`).

---

## 3) What thread‑safety guarantees does `incrementAndGet()` give?

* It performs the read‑modify‑write as a **single, uninterruptible atomic instruction** (or a short sequence with a hardware lock).
* It guarantees no two threads will see duplicate or missing increments—each call adds exactly one.
* It includes a lightweight memory barrier: when thread A increments and thread B later reads, the change is visible according to the Java Memory Model.

---

## 4) When is using a *lock* better than an atomic variable?

| Scenario | Why a lock is preferable |
| --- | --- |
| Composite operations on several variables (e.g., increment two counters and check a condition) | Atomics cover **one** variable; coordinating several objects needs a lock. |
| Long critical sections or code that must run **completely sequentially** | A lock can wrap any block of code—multiple statements, many variables. |
| Logic that needs conditions (e.g., check‑then‑act) or waiting | Lock classes (`ReentrantLock`, `Condition`) support `await()` / `signal()`; atomics don’t. |
| Avoiding starvation (fair locks) or complex deadlock handling | Locks can be fair/unfair and offer `tryLock` with timeout. |
| Low contention and code clarity matters more than a few nanoseconds of speed | Locks are often clearer and less error‑prone than subtle atomic code. |

---

## 5) What other data types exist in `java.util.concurrent.atomic`?

| Type                                                                                                                              | Brief description |
|-----------------------------------------------------------------------------------------------------------------------------------| --- |
| `AtomicBoolean`                                                                                                                   | Atomic boolean |
| `AtomicInteger` , `AtomicLong`                                                                                                    | 32‑/64‑bit atomic integers |
| `AtomicReference&lt;V&gt;`                                                                                                        | Atomic reference to any object |
| `AtomicIntegerArray` , `AtomicLongArray`                                                                                          | Arrays of atomic integers |
| `AtomicReferenceArray&lt;E&gt;`                                                                                                   | Array of atomic references |
| `AtomicStampedReference&lt;V&gt;`                                                                                                 | Reference + stamp to fix the ABA problem |
| `AtomicMarkableReference&lt;V&gt;`                                                                                                | Reference + boolean mark |
| Field updaters: `AtomicIntegerFieldUpdater&lt;T&gt;`, `AtomicLongFieldUpdater&lt;T&gt;`, `AtomicReferenceFieldUpdater&lt;T,V&gt;` | Atomically update `volatile` fields inside an object without boxing |
| `LongAdder`, `DoubleAdder`                                                                                                        | High‑throughput counters that give each thread a cell instead of one central variable |
| `LongAccumulator`, `DoubleAccumulator`                                                                                            | Custom accumulators with a user‑defined binary operator |

---

### Final note
If you need help with other parts of the assignment (e.g., Monte‑Carlo coding or the banking system), let me know and we can tackle them step by step.
