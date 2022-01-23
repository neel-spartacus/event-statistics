**Introduction**:

**System requirements:**

1)OpenJDK for Java 1.8
2)Git
3)Maven 3.6.1 or higher
6)Junit 4.13
7)Swagger 1.5.2
8)Project Lombok: https://projectlombok.org


**Building the project:**

To build the JAR and run some tests:
 ** mvn clean install**

To run the application:
  **java -jar target/helloFresh-1.0-SNAPSHOT.jar**

Swagger UI:
  http://localhost:8080/swagger-ui/

About assignment:

In the requirements part,its mentioned that: 𝑦: An integer in 1,073,741,823..2,147,483,647.So,i assumed its a value
between 1073741823 and 2147483647 including both. In the example payload, the values in the 3rd row is,

1607341271814,0.0586780608,111212767 , where the value of y is 111212767 which is less than 1073741823.
So, running this command : java -jar producer.jar -m=test -p=8080  which send the example payload and expects a response
fails.
Please rectify this value or change in the ValidationUtils.java  to run the above command:
Change line number 39 : Comment out if(valueY< MIN_VALUE_X || valueY> MAX_VALUE_X)
Change line number 40 : Uncomment out if(valueY> MAX_VALUE_X)


Implementation :

The implementation is based on asynchronous api call to provide non-blocking api's for both the POST and GET api's.
Java's Native ExecutorService,CompletableFuture and Spring DefferedResult has been used
to make it asynchronous and parallel thread capable at the same time.


Improvements:

Since its mentioned in the description,that there would be past events.So, assuming the past events can be even more than
60 seconds or more old,a better approach could have been done to reject all those events which were already more than 60 seconds
old from the time the POST api is called.