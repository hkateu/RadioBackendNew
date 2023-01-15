# Radio Backend
This is a simple functional backend server built with [http4s](http4s.org), [circe](github.com/circe/circe) and [doobie](tpolecat.github.io/doobie). This project developed as a need to port my backend from play framework to something more function. 
This app is a backend server for a radio streaming application. The database implementation is built with MySql.

# Routes
| Request | Route | Description |
|:--- |:--- |:---|
| GET | Root/ "getradios" / ""  | Fetches all radio stations.|
| GET | Root/ "shows" | Fetches all shows. | 

# Installation
## Prequisites
The following should be installed on your system.
1. Scala
1. Sbt
1. Mysql server

## Step1
Clone the repo using the code below
```
clone https://github.com/hkateu/RadioBackendNew.git
```
This will pull all the files and folders onto you local machine

## Step2
Create a mysql database called radio and import the data using the following code.

```sql
CREATE DATABASE radio;
```

## Step3
Edit Controller.scala and provide the appropriate username and password.

## Step4
Import data from mysql dump found in 'SQL/Bradio.sql'

``` sql
source Bradio.sql;
```

## Step5
Navigate into this new folder run the following code.

``` scala
sbt run
```
The code above when run for the first time will download all the artifacts needed for the project to run on your local machine.

# Testing
You can test the project by first compiling it with sbt and running a query to the server which should start on port 9000.

```scala
sbt run
```
The code above compiles and starts the server.

```
http -vv "http://localhost:9000/getradios/"
```
The code above sends a get request to port 9000. The following results should be seen.

```
GET /getradios/ HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Host: localhost:9000
User-Agent: HTTPie/3.2.1



HTTP/1.1 200 OK
Connection: keep-alive
Content-Length: 361
Content-Type: application/json
Date: Sun, 15 Jan 2023 12:34:13 GMT

[
    {
        "frequency": "91.2",
        "likes": 2,
        "location": "Kampala",
        "radioId": 1,
        "station": "Capital FM",
        "stnid": "capital",
        "url": "mixA.mp3"
    },
    {
        "frequency": "92.3",
        "likes": 4,
        "location": "Mbarara",
        "radioId": 2,
        "station": "Crooze FM",
        "stnid": "crooze",
        "url": "mixC.mp3"
    },
    {
        "frequency": "88.3",
        "likes": 0,
        "location": "Kampala",
        "radioId": 3,
        "station": "Sanyu FM",
        "stnid": "sanyu",
        "url": "mixB.mp3"
    }
]


Elapsed time: 3.220399698s
```

