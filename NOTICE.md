1. You are only allowed to submit **10 times every 24 hours** (on a rolling basis). Only valid submissions will count.
2. You should submit archived program in **zip** format. Max allowed size is **64KB**. The main executable should be named **CARP_solver.py** (case sensitive) and put in **root directory**. Multiple files and subdirectories are allowed.
3. Your program will be executed with command: `python3 CARP_solver.py $data -t $time`
4. Do not use non-standard library except for **numpy**.
5. Your program is run in Docker container with no network interface.
6. You are allowed to use up to **800% CPU** (8 cores) and **2GB RAM**. Pay attention that **threading** in Python does not give you real parallelism.
7. Be sure to exit before time running out. You should leave 1 second for starting container.
8. Exit code must be 0, otherwise it will be considered invalid.
9. Maximum allowed processes in container is **64**.
10. If you need to write temp file, write to **/tmp**. The whole RootFS is **read-only**.
11. Stdout except for the **last 256KB** will be discarded. Be sure not to print too many logs after printing result.