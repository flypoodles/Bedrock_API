# cs441hw3

---
### name
>Lei Chen
### email
>lchen230@uic.edu

### netid
>lchen230
---
## Prerequiste
-Owns a AWS account

## Description
In this assignment, I have to use grpc to call a lambda function on AWS. The lambda function will generate a request to AWS Bedrock.
To do that I set up a proxy GRPC server. Basically I have a GRPC frontend that accepts HTTP request.
The GRPC frontend will sent the request to the backend GRPC server. 
The GRPC server will invoke a call to the lambda function. After lambda function finished executing, 
it will return the response generated from AWS Bedrock back to the GRPC frontend.

## Video
https://youtu.be/Sx-LdaNILI0

## Instruction
1.  Log onto Aws and create a lambda function.
2.  Give the lambda function permission to access AWS Bedrock and AWS Lambda.
3.  Copy the lambda functions's ARN to application.conf inside the project under lambdaAddress
4.  Now convert the project into an uber jar using sbt assembly.
5.  Upload this jar file onto AWS s3.
6.  Upload the jar file to AWS lambda using the jar file's S3 url.
7.  In AWS Lambda runtime setting, change the handler location to "myLambdaHandler::handleRequest".
8.  Change the Lambda function's timeout to 30 seconds.
9.  Create an EC2 instance with Ubuntu as the OS image. Allow SSH, HTTP, and HTTPs traffic. Also enable port 7000 and 8080.
10.  Give it an IAM profile that has Bedrock and Lambda access
11. copy the uber jar file onto the ec2 using scp
````
     scp -i /LOCATION_OF_YOU_PRIVATE_KEY /LOCATION_OF_THE_JAR_FILE  ubuntu@ec2-54-209-251-238.compute-1.amazonaws.com:~
````
12. ssh into your ec2 instance:
````
    ssh -i /LOCATION_OF_YOU_PRIVATE_KEY   ubuntu@ec2-54-209-251-238.compute-1.amazonaws.com
````
13. Inside the ec2 terminal, run to install JVM
````
     sudo apt-get update
     sudo apt install openjdk-11-jre-headless
````

14. Run the following command to start the GRPC server
````
    java -jar cs441hw3.jar server
````
15. Connect to EC2 terminal again through another terminal. Then run the HTTP server using the following command.
````
    java -jar cs441hw3.jar client
````
16. Go to the following link in your browser, you should able to see hello world.
````
    http://YOUR_EC2_ADDRESS:7000/prompt
 ````
17.  You can type in a prompt inside the url like the following and give back a response:
````
    http://YOUR_EC2_ADDRESS:7000/prompt?sentence=I am gru
````

![img.png](img.png)