# SEED
This repository contains the artifacts for Software Execution Event Data (SEED) model.

1) XES converter to facilitate kiker monitoring logs to XES format.
2) Implementation for compliance rules to validate the SEED model compatibility.

# HOW TO RUN XES converter 

Pre-requisits
 Java version 22
 
System properties - Update below system properties

server.port=8889
server.servlet.contextPath=/XESConverter

app.logFolder=C:/Development/SEED/Input/Input_JpetStore
app.XESFileLocation=C:/Development/SEED/Output/

These properties defines the standard port of the application, context part, folder for input kieker logs, and location for XES output file.
Sample input and output files have provided.

