# SEED
This repository contains the artifacts for Software Execution Event Data (SEED) model.

1) XES converter to facilitate kiker monitoring logs to XES format.
2) Implementation for compliance rules to validate the SEED model compatibility.

# HOW TO RUN XES converter 

Pre-requisits
 Java version 22
 
System properties - Update below system properties

server.port=8890
server.servlet.contextPath=/XESConverter

#Input_E_Commerce_Logs
app.logFolder=C:/Development/SEED/Input/Input_E_Commerce_Logs
app.XESFileLocation=C:/Development/SEED/Output/

# input formats 1 - kieker, 2 - SEED logs
app.inputFormat=2   

# output format 1 - XES, 2 - text format
app.outputFormat=2

# SW log related attributes
# Sample software log : traceId:TRACE-0001, sessionId:SESSION-001, userId:USER-01, methodName:placeOrder, timestamp:2024-06-01T10:00:00.000Z, transactionType:START, methodId:M001, instance:0x4C2581, returnType:void, inputParameters:OrderRequest, isConstructor:false, nested:true, depth:0, callingOrder:1, sourceLocation:OrderController.java:24, packageName:com.example.order, threadId:thread-1
# log.delimter - delimter between two properties
log.delimiter = ,
# log.propertyDelimiter - delimiter between property and value
log.propertyDelimiter = :

# log mapping starts
# name of below properties can be differnt in software logs. Hence mapping has defined to identify which software log name maps to property
traceId=traceId
sessionId=sessionId
userId=userId
methodName=methodName
timestamp=timestamp
transactionType=transactionType
methodId=methodId
instance=instance
returnType=returnType
inputParameters=inputParameters
isConstructor=isConstructor
nested=nested
depth=depth
callingOrder=callingOrder
sourceLocation=sourceLocation
packageName=packageName
threadId=threadId
callerMethod=callerMethod
callerInstance=callerInstance
duration=duration
# log mapping ends

