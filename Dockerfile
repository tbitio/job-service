FROM java:8
ADD target/job-service-1.0.0-exec.jar /usr/local/projects/job-service-1.0.0-exec.jar
ENTRYPOINT ["java","-Xmx200m","-jar","/usr/local/projects/job-service-1.0.0-exec.jar"]
