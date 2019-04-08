# Server for CARP-OJ

This is the repository for CRAP-OJ. It served for internal use for Artificial Intelligence (CS303) class in SUSTech during Fall 2018.

- Judge worker: [carp_judge_worker](https://github.com/imxieyi/carp_judge_worker)

## Requirements
- JDK 1.8+
- MongoDB

## Build
```bash
gradle build
```

## Setup Database
```bash
gradle addData
```

## Run
```bash
gradle bootRun
```

## Server Design Flaws
- Mongo document references should be avoided.
- Uploads should be stored separately.

## API Reference
Refer to [API.md](API.md). (Not updated)
