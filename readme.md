#Catalog API Test

##Overview
A test implementation of a RESTful API service with courses scraped from the online catalog.

##Description
Catalog API test is a web app API allowing users to access information on various computer science courses at Oregon State University.  There are two main components to this API, the scraper and the actual API.

###Web Scraper
Course information will be scraped from the online catalog and stored for use (in an Oracle database).  The data will include course information such as the cid, CRN, course name, description, instructor, CRN, day, time, and location.

###RESTful API
The API will grab the data from the database and provide an outlet for users to request and interact with the data.  The API as a whole as well as the methods of interaction will be highlighted in the next section.

##Features
Users will be able to access each course’s information through a RESTful interface.  With this data, they will be able to build any course related service.

With requests, users will be able to access each courses information as listed above.  They will be able to GET to retrieve data from a resource, PUT to update a resource, POST to create a resource, and DELETE to remove a resource.  If invalid requests are submitted, ie. attempting to retrieve a non-existing resource or submitting a PUT request with invalid data, an error message will be outputted to the user - examples are as shown in the mockup section.

**Please note that if scraper is run again after any submitted PUT/POST/DELETE requests, those changes will be overwritten by the scraped data.

Courses will be accessible by search through the course’s CRN.  For example CRN #50220 may reference one of the first computer science courses, ie. CS 101. And CRN #11933 may reference another course that follows, ie. CS 160.

### Setup Instructions

First off, you'll need to enter in your credentials into the ```configuration.yaml``` file.

Next you will need to download the ```ojdbc6_g.jar``` file from [Oracle](http://www.oracle.com/technetwork/apps-tech/jdbc-112010-090769.html) and move it into your bin directory.

Once that has been done, Open terminal, navigate to your cloned local directory, and enter the following line

```
gradle build
```

This builds the project into a single deployable jar file.

**Side note** - If you want to see a full list of commands you can enter ```gradle tasks```, which lists all possible commands associated with the project, including ```gradle idea``` which may be helpful if using IntelliJ...

Next you'll want to run the jar file along with your credentials in your config file.  To do so, enter the following line:

```

java -jar build/libs/catalog-api-test-all.jar server configuration.yaml

```

An alternative option to this, if you all the required resources installed properly, you should be able to simply run ```gradle run``` to build and run everything.  You can also create and use a bash script to do this for you.

##Mockup

*Please note that the following responses are pretty-printed for easier viewing, your actual responses may not be as elegant.  Also, the current readme responses (header content-lengths and body JSON) may not be as current in this readme as they are currently as much testing has been in development.

###Connecting

The following HTTP requests will be done over netcat for the purposes of example:

```
$ nc localhost 8008 << HERE
…
…
HERE
```

###GET
Request data from resource.

####If the course actually exists:

#####Get All
```
$ nc localhost 8008 << HERE
>
> GET /api/v0/course/all HTTP/1.0
> 
> HERE

HTTP/1.1 200 OK
Date: Mon, 20 Jul 2015 17:30:41 GMT
Content-Type: application/json
Content-Length: 112

[
        {
        "cid": 5,
        "crn": 11111,
        "courseName": "CS 121",
        "instructor": "Mr. TEST",
        "day":"MWF",
        "time":"12-1",
        "location":"KEC"
        },
        {
        "cid": 6,
        "crn": 11112,
        "courseName": "CS 122",
        "instructor": "Mr. Test2",
        "day":"MWF",
        "time":"12-1",
        "location":"KEC"
        }
]
```

#####Get by CRN:
```
$ nc localhost 8008 << HERE
>
> GET /api/v0/course/11111 HTTP/1.0
> 
> HERE

HTTP/1.1 200 OK
Date: Mon, 20 Jul 2015 17:30:41 GMT
Content-Type: application/json
Content-Length: 112

[
        {
        "cid": 5,
        "crn": 11111,
        "courseName": "CS 121",
        "instructor": "Mr. TEST",
        "day":"MWF",
        "time":"12-1",
        "location":"KEC"
        }
]
```

#####Get by Name
```
$ nc localhost 8008 << HERE
>
> GET /api/v0/course/name/CS%20121 HTTP/1.0
>
> HERE

HTTP/1.1 200 OK
Date: Mon, 20 Jul 2015 17:30:41 GMT
Content-Type: application/json
Content-Length: 112

[
        {
        "cid": 5,
        "crn": 11111,
        "courseName": "CS 121",
        "instructor": "Mr. TEST",
        "day":"MWF",
        "time":"12-1",
        "location":"KEC"
        }               
]  
```

#####If the data is invalid:

```
$ nc localhost 8008 << HERE
>
> GET /api/v0/course/name/NotARealName HTTP/1.0
>
> HERE

HTTP/1.1 404 Not Found
Date: Wed, 22 Jul 2015 16:41:31 GMT
Content-Type: text/html; charset=ISO-8859-1
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 301

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Error 404 Not Found</title>
</head>
<body><h2>HTTP ERROR 404</h2>
<p>Problem accessing /course/name/NotARealName. Reason:
<pre>    Not Found</pre></p><hr><i><small>Powered by Jetty://</small></i><hr/>

</body>
</html>
```

###PUT
Creates or modifies an existing resource.

#####If data is valid:
```
$ nc localhost 8008 << HERE
>
> PUT /api/v0/course/11111 HTTP/1.0
> Content-Length: 158
> Content-Type: application/json
> 
>   {
>     "cid": 5,
>     "crn": 11111,
>     "courseName": "CS NEW",
>     "instructor": "Mr. TEST",
>     "day": "MWF",
>     "time": "12-1",
>     "location": "KEC"
>   }
> 
> HERE

HTTP/1.1 200 OK
Date: Wed, 22 Jul 2015 17:18:06 GMT
Content-Length: 0
```

#####If data is invalid:
```
$ nc localhost 8008 << HERE
>
> PUT /api/v0/course/11111 HTTP/1.0
> Content-Length: 158
> Content-Type: application/json
>  {
>    "cid": 5,
>    "crn": AAAAA,
>    "courseName": "CS NEW",
>    "instructor": "Mr. TEST",
>    "day": "MWF",
>    "time": "12-1",
>    "location": "KEC"
>  }
>
> HERE

HTTP/1.1 400 Bad Request
Date: Wed, 22 Jul 2015 17:19:55 GMT
Content-Length: 0

```

###POST
Create course.

#####If data is valid:

```
$ nc localhost 8008 << HERE
>
> POST /api/v0/course HTTP/1.0
> Content-Length: 158
> Content-Type: application/json
>
>         {
>         "cid": 9,
>         "crn": 97225,
>         "courseName": "CS 678",
>         "instructor": "Mr. TEST",
>         "day":"MWF",
>         "time":"12-1",
>         "location":"KEC"
>         }
>
> HERE

HTTP/1.1 201 Created
Date: Wed, 22 Jul 2015 17:00:55 GMT
Location: http://127.0.0.1:8008/course/29
Content-Length: 0
```

#####If data is invalid:

```
$ nc localhost 8008 << HERE
>
> POST /api/v0/course/ HTTP/1.0
> Content-Length: 158
> Content-Type: application/json
>
>         {
>         "cid": 66,
>         "crn": aaaaa,
>         "courseName": "CS 111",
>         "instructor": "Mr. 123",
>         "day":"MWF",
>         "time":"12-1",
>         "location":"KEC"
>         }
> ]
>
> HERE

HTTP/1.1 400 Bad Request
Date: Wed, 22 Jul 2015 17:04:34 GMT
Content-Type: application/json
Content-Length: 47
```

###DELETE
Remove course.

#####If data is valid:

```
$ nc localhost 8008 << HERE
>
> DELETE /api/v0/course/11111 HTTP/1.0
>
> HERE

HTTP/1.1 200 OK
Date: Mon, 20 Jul 2015 17:30:41 GMT
Content-Type: application/json
Content-Length: 112
```

#####If data is invalid

```
$ nc localhost 8008 << HERE
>
> DELETE /api/v0/course/NotARealCourse HTTP/1.0
>
> HERE

HTTP/1.1 404 Not Found
Date: Wed, 22 Jul 2015 16:41:31 GMT
Content-Type: text/html; charset=ISO-8859-1
Cache-Control: must-revalidate,no-cache,no-store
Content-Length: 301

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Error 404 Not Found</title>
</head>
<body><h2>HTTP ERROR 404</h2>
<p>Problem accessing /course/name/NotARealCourse. Reason:
<pre>    Not Found</pre></p><hr><i><small>Powered by Jetty://</small></i><hr/>

</body>
</html>
```
