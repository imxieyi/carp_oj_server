API Reference
===
## Data Model

### User

#### Fields

|Field|Type|Comment|
|---|---|---|
|id|string|mongo generated|
|username|string|unique, indexed|
|password|string|bcrypt encrypted|
|type|int||

**User types:**

|Type|Code|
|---|---|
|ROOT|0|
|ADMIN|100|
|WORKER|200|
|USER|300|

### Dataset

|Field|Type|Comment|
|---|---|---|
|id|string|mongo generated|
|name|string|unique, indexed|
|time|int|time limit|
|memory|int|memory limit|
|cpu|int|cpu limit|
|data|string|input data|

### CARPCase

|Field|Type|Comment|
|---|---|---|
|id|string|mongo generated|
|user|User|reference, indexed|
|dataset|Dataset|reference, indexed|
|archive|binary|original zip|
|status|int||
|submitTime|date||
|judgeTime|date||
|judgeWorker|User||
|timedout|bool||
|stdout|string||
|outOverflow|bool|stdout exceeds limit|
|stderr|string||
|errOverflow|bool|stderr exceeds limit|
|time|double|running time including docker overhead|
|exitcode|int||
|path|string|carp result|
|valid|bool|is result valid|
|cost|int|path cost|

**Status codes:**

|Status|Code|Comment|
|---|---|---|
|WAITING|0||
|QUEUED|1|sent to worker|
|RUNNING|2||
|FINISHED|3||

## REST API

### Exception Handling

- All exceptions will result in non-200 status code with body of json format containing further details.
A typical response looks like:
```json
{
  "timestamp": "2018-10-29T04:46:26.091+0000",
  "status": 403,
  "error": "Forbidden",
  "message": "Not logged in!",
  "path": "/api/judge/all"
}
```
- All responses with 200 status **DO NOT** include information like the example above.

### Admin

#### System

##### Add test data

`GET /api/system/addtestdata`

*Allowed user types:* ROOT

*Response:* `done` if finished successfully.

#### User

##### Add user

`POST /api/admin/user/add`

*Request body:*

```json
{
  "username": "user",
  "password": "pass",
  "type": 300
}
```

*Allowed user types:*

- ROOT: ADMIN, WORKER, USER
- ADMIN: USER

*Response:*

```json
{
  "id": "Object id of the added user"
}
```

### Dataset

##### Add dataset

`POST /api/dataset/add`

*Request body:*

```json
{
  "name" : "small-1",
  "time" : 10,
  "memory" : 256,
  "cpu" : 1,
  "data" : "NAME : small-1\nVERTICES : 12..."
}
```

*Allowed user types:* ROOT, ADMIN

*Response:*

```json
{
  "id": "Object id of the added dataset"
}
```

##### Query all datasets

`GET /api/dataset/all`

*Allowed user types:* ROOT, ADMIN, USER

*Response:*

```json
{
  "datasets": [
    {
      "id": "5bd5fb0a044caa567486134d",
      "name": "gdb10",
      "time": 10,
      "memory": 256,
      "cpu": 1
    }
  ]
}
```

### Judge

##### Query all for logged in user

`GET /api/judge/all`

*Allowed user types:* ROOT, ADMIN, USER

*Response:*

```json
{
    "carpCases": [
        {
            "id": "5bd602c4044caa5464b356e0",
            "status": 3,
            "submitTime": "2018-10-28T18:41:08.775+0000",
            "judgeTime": "2018-10-28T18:41:10.171+0000",
            "timedout": false,
            "outOverflow": false,
            "errOverflow": false,
            "time": 9.969951629638672,
            "exitcode": 0,
            "valid": true,
            "cost": 0,
            "userId": "5bd5fb02044caa5674861349",
            "datasetId": "5bd5fb0a044caa567486134d",
            "workerName": "judge"
        }
    ]
}
```

**Ordered by submitTime desc.**

##### Query by case id

`GET /api/judge/query?cid={CASE_ID}`

*Allowed user types:* ROOT, ADMIN, USER

*Response:*

```json
{
    "id": "5bd5fbce044caa567486134e",
    "status": 3,
    "submitTime": "2018-10-28T18:11:26.103+0000",
    "judgeTime": "2018-10-28T18:11:36.646+0000",
    "timedout": false,
    "outOverflow": false,
    "errOverflow": false,
    "time": 10.630940437316895,
    "exitcode": 0,
    "valid": false,
    "cost": 0,
    "datasetId": "5bd5fb0a044caa567486134d",
    "userId": "5bd5fb02044caa5674861349",
    "workerName": "judge"
}
```

##### Query leaderboard for dataset

`GET /api/judge/top?did={DATASET_ID}`

*Allowed user types:* ROOT, ADMIN, USER

*Response:*

```json
{
    "carpCases": [
        {
            "userName": "root",
            "datasetId": "5bd602c4044caa5464b356e0",
            "submitTime": "2018-10-28T18:41:08.775+0000",
            "time": 9.969951629638672,
            "cost": 0
        }
    ]
}
```

**Each user appears only once, order by cost asc, time asc, submitTime asc.**

##### Get remaining submit time

`GET /api/judge/remain`

*Allowed user types:* ROOT, ADMIN, USER

*Response:*

```json
{
    "remain": 6,
    "total": 10
}
```

##### Submit data for judging

`POST /api/judge/submit`

*Request body:*

```json
{
  "dataset": "5bd5fb0a044caa567486134d",
  "data": "Base64 encoded zip"
}
```

*Allowed user types:* ROOT, ADMIN, USER

*Response:*

```json
{
    "cid": "5bd6a0170a44aa105c5927e9",
    "remain": 6
}
```

### Login & Logout

##### Login

`GET /api/login?username={USERNAME}&password={PASSWORD}`

*Response:*

```json
{
    "uid": "5bd5fb0a044caa567486134c",
    "type": 300
}
```

**Logout before login again.**

##### Logout

`GET /api/logout`

*Response:*

```json
{
    "uid": "5bd5fb0a044caa567486134c"
}
```

### User

##### Get info for logged in user

`GET /api/user/info`

*Allowed user types:* ROOT, ADMIN, WORKER, USER

*Response:*

```json
{
    "uid": "5bd5fb0a044caa567486134c",
    "username": "user",
    "type": 300
}
```

##### Change password for logged in user

`GET /api/user/change/password?old={OLD_PASSWORD}&new={NEW_PASSWORD}`

*Allowed user types:* ROOT, ADMIN, WORKER, USER

*Response:*

```json
{
}
```

**Status of 200 means success. Otherwise exception is thrown.**