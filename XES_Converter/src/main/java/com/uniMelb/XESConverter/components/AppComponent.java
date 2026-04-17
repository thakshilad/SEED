package com.uniMelb.XESConverter.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uniMelb.XESConverter.entities.Event;


@Component
public class AppComponent {

    public static Map<String, List<Event>> dataMap = new HashMap<>(0); // event data - trace ID and event entity List
    public static Map<String, String> propertyStore = new HashMap<>(0); // data store for log properties names SEED property conversion


    // temp storage of log entries
    List<String> filteredLineList = new ArrayList<String>(0);

    @Value("${app.logFolder}")
    private String logFolder;

    @Value("${app.XESFileLocation}")
    private String XESFileLocation;

    // 1 - kieker, 2 - SEED logs
    @Value("${app.inputFormat}") 
    private int inputFormat;

    //1 - XES, 2 - text format
    @Value("${app.outputFormat}") 
    private int outputFormat;

    @Value("${log.delimiter}")
    private String logDelimiter;

    @Value("${log.propertyDelimiter}")
    private String propertyDelimiter;

    @Value("${traceId}")
    private String traceId;

    @Value("${sessionId}")
    private String sessionId;

    @Value("${userId}")
    private String userId;

    @Value("${methodName}")
    private String methodName;   

    @Value("${timestamp}")
    private String timestamp;   

    @Value("${transactionType}")
    private String transactionType;

    @Value("${methodId}")
    private String methodId;

    @Value("${instance}")
    private String instance;

    @Value("${returnType}")
    private String returnType;

    @Value("${inputParameters}")
    private String inputParameters;

    @Value("${isConstructor}")
    private String isConstructor;    

    @Value("${nested}") 
    private String nested;  

    @Value("${depth}")
    private String depth;    

    @Value("${callingOrder}")
    private String callingOrder;    

    @Value("${sourceLocation}")
    private String sourceLocation;    

    @Value("${packageName}")
    private String packageName;    

    @Value("${threadId}")
    private String threadId;  

    @Value("${callerMethod}")
    private String callerMethod; 

    @Value("${callerInstance}")
    private String callerInstance; 

    @Value("${duration}")
    private String duration; 
    
   
// Initial data loading and printing
    public void initiateIdentification() {
        System.out.println("=========== System Parameters Starts =============");
        System.out.println("app.logFolder : "+ logFolder );
        System.out.println("app.XESFileLocation : "+ XESFileLocation );
        System.out.println("app.inputFormat : "+ inputFormat );
        System.out.println("app.outputFormat : "+ outputFormat );
        System.out.println("logDelimiter : "+ logDelimiter); 
        System.out.println("propertyDelimiter: "+ propertyDelimiter);
        System.out.println("traceId: "+ traceId);
        System.out.println("sessionId: "+ sessionId);
        System.out.println("userId: "+ userId);
        System.out.println("methodName: "+ methodName);   
        System.out.println("timestamp: "+ timestamp);   
        System.out.println("transactionType: "+ transactionType);
        System.out.println("methodId: "+ methodId);
        System.out.println("instance: "+ instance);
        System.out.println("returnType: "+ returnType);
        System.out.println("inputParameters: "+ inputParameters);
        System.out.println("isConstructor: "+ isConstructor);    
        System.out.println("nested: "+ nested);  
        System.out.println("depth: "+ depth);    
        System.out.println("callingOrder: "+ callingOrder);    
        System.out.println("sourceLocation: "+ sourceLocation);    
        System.out.println("packageName: "+ packageName);   
        System.out.println("threadId: "+ threadId);   
        System.out.println("callerInstance: "+ callerInstance);   
        System.out.println("callerMethod: "+ callerMethod);  
        System.out.println("duration: "+ duration); 
        System.out.println("=========== System Parameters Ends =============");

        propertyStore.put("traceId", traceId);
        propertyStore.put("sessionId", sessionId);
        propertyStore.put("userId", userId);
        propertyStore.put("methodName", methodName);  
        propertyStore.put("timestamp", timestamp);   
        propertyStore.put("transactionType", transactionType);
        propertyStore.put("methodId", methodId);
        propertyStore.put("instance", instance);
        propertyStore.put("returnType", returnType);
        propertyStore.put("inputParameters", inputParameters);
        propertyStore.put("isConstructor", isConstructor);    
        propertyStore.put("nested", nested);  
        propertyStore.put("depth", depth);    
        propertyStore.put("callingOrder", callingOrder);    
        propertyStore.put("sourceLocation", sourceLocation);    
        propertyStore.put("packageName", packageName);    
        propertyStore.put("threadId", threadId);  
        propertyStore.put("callerInstance", callerInstance);    
        propertyStore.put("callerMethod", callerMethod);  
        propertyStore.put("duration", duration); 

        readAndConvert(logFolder);
    }


    // read the kiker or sw logs
    public void readAndConvert(String logFolder) {

        if (inputFormat == 1) { // Kieker input file
        System.out.println("Reading Kiker log file");
            try {
                final File folder = new File(logFolder);
                for (final File fileEntry : folder.listFiles()) {
                    BufferedReader br = new BufferedReader(new FileReader(fileEntry)); 
                        for(String line; (line = br.readLine()) != null; ) {
                            // process the line. tokernize and extract properties
                            if(line.startsWith("$1;") && !line.contains("kieker.monitoring.probe") && !line.contains("Mapper") ){
                                filteredLineList.add(line);
                            }
                        }
                }
                convertKiekerLogs(filteredLineList);
            } catch (Exception e) {
                System.out.println("Exception in reading kieker log file : "+e.getMessage() );
            }
        } else { // SW log files
            try {
                System.out.println("Reading software log file");
                final File folder = new File(logFolder);
                for (final File fileEntry : folder.listFiles()) {
                    BufferedReader br = new BufferedReader(new FileReader(fileEntry)); 
                        for(String line; (line = br.readLine()) != null; ) {
                            filteredLineList.add(line);
                        }
                }
                convertSWLogs(filteredLineList);
            } catch (Exception e) {
                System.out.println("Exception in reading software log file : "+e.getMessage() );
            }

        }
     }

     // convert filtered kieker logs to xes format
    public void convertKiekerLogs(List<String> dataList) {
        try{
            System.out.println("Data list size "+ dataList.size());
                for(int i=dataList.size()-1; i>=0; i--){
                String eventString = dataList.get(i);
                String[] tokenizedArray = eventString.split(";");
                String timeStamp = tokenizedArray[1];  // in nano seconds
                String sessionId = tokenizedArray[3];
                String traceId = tokenizedArray[4];

                long inTime = Long.parseLong(tokenizedArray[5]);
                long outTime = Long.parseLong(tokenizedArray[6]);
                String kikerNodeId = tokenizedArray[7];
                int callingOrder = Integer.parseInt(tokenizedArray[8]);
                int depthOfCallingStack = Integer.parseInt(tokenizedArray[9]);
                String methodSignature = tokenizedArray[2];
                
                Event event = new Event();
                event.setSequenceId(Long.parseLong(timeStamp));
                event.setSessionId(sessionId);
                event.setTraceId(traceId);
                event.setMethodSignature(methodSignature);
                event.setInTime(inTime);
                // event.setFormattedDate(""+inTime);
                event.setFormattedDate(convertDate(inTime));
                event.setOutTime(outTime);
                event.setCallingOrder(callingOrder);
                event.setDeptOfCallingStack(depthOfCallingStack);
                List<Event> tempList = new ArrayList<>(0);
                if(dataMap.get(event.getTraceId()) != null) {
                    tempList = dataMap.get(event.getTraceId());
                }
                
                tempList.add(event);
                Collections.sort(tempList, (o1, o2) -> o1.getCallingOrder() - o2.getCallingOrder());
                dataMap.put(event.getTraceId(), tempList);

            }
                generateOutputFile();
        } catch (Exception e) {
            System.out.println("Exception in convert logs to XES format : "+ e.getLocalizedMessage());
            e.printStackTrace();
        }
	}

    // convert SW logs to event format map
    public void convertSWLogs(List<String> dataList) {
        try{
            System.out.println("Data list size "+ dataList.size());

                for(int i=dataList.size()-1; i>=0; i--){
                    String logStringEntry = dataList.get(i);
                    Event event = new Event();
                    String[] tokenizedArray = logStringEntry.split(logDelimiter);
                    for (int j=0; j<tokenizedArray.length; j++) {
                        String[] propertyArray = tokenizedArray[j].split(propertyDelimiter);
                        String keyString = propertyArray[0].trim();
                        String valueString = propertyArray[1].trim();

                        if (keyString.equals(propertyStore.get("traceId"))) {
                            event.setTraceId(valueString);
                        } else if(keyString.equals(propertyStore.get("sessionId"))){
                            event.setSessionId(valueString);
                        } else if(keyString.equals(propertyStore.get("userId"))) {
                            event.setUserId(valueString);
                        } else if (keyString.equals(propertyStore.get("methodName"))){
                            event.setMethodSignature(valueString);
                            event.setMethodName(valueString);
                        } else if(keyString.equals(propertyStore.get("timestamp"))) {
                            event.setTimeStamp(valueString);
                        } else if(keyString.equals(propertyStore.get("transactionType"))) {
                            event.setTransactionType(valueString);
                        } else if(keyString.equals(propertyStore.get("methodId"))) {
                            event.setMethodId(valueString);
                        } else if(keyString.equals(propertyStore.get("instance"))) {
                            event.setInstance(valueString);
                        } else if(keyString.equals(propertyStore.get("returnType"))) {
                            event.setReturnType(valueString);
                        } else if(keyString.equals(propertyStore.get("inputParameters"))) {
                            event.setInputParameters(valueString);
                        } else if(keyString.equals(propertyStore.get("isConstructor"))) {
                            event.setIsConstructor(valueString.equalsIgnoreCase("true")?true:false);
                        } else if(keyString.equals(propertyStore.get("nested"))) {
                            event.setIsNested(valueString.equalsIgnoreCase("true")?true:false);
                        } else if(keyString.equals(propertyStore.get("depth"))) {
                            event.setDeptOfCallingStack(Integer.parseInt(valueString));
                        } else if(keyString.equals(propertyStore.get("callingOrder"))) {
                            event.setCallingOrder(Integer.parseInt(valueString));
                        } else if(keyString.equals(propertyStore.get("sourceLocation"))) {
                            event.setSourceLocation(valueString);
                        } else if(keyString.equals(propertyStore.get("packageName"))) {
                            event.setPackageName(valueString);
                        } else if(keyString.equals(propertyStore.get("threadId"))) {
                            event.setThreadId(valueString);
                        } else if(keyString.equals(propertyStore.get("callerInstance"))) {
                            event.setCallerInstance(valueString);
                        } else if(keyString.equals(propertyStore.get("callerMethod"))) {
                            event.setCallerMethod(valueString);
                        } else if(keyString.equals(propertyStore.get("userId"))) {
                            event.setUserId(valueString);
                        } else {
                            System.out.println("invalid property : "+ keyString +" : "+ valueString);
                        }

                        List<Event> tempList = new ArrayList<>(0);
                        if(dataMap.get(event.getTraceId()) != null) {
                            tempList = dataMap.get(event.getTraceId());
                        }
                        
                        tempList.add(event);
                        Collections.sort(tempList, (o1, o2) -> o1.getCallingOrder() - o2.getCallingOrder());
                        dataMap.put(event.getTraceId(), tempList);
                    }
                }
            generateOutputFile();
        } catch (Exception e) {
            System.out.println("Error in sw log parsing : "+ e);

        }
    }

// generating xes or txt output files
    public void generateOutputFile() {

            File file = null;

            if (outputFormat == 1) {// XES
                file = new File(XESFileLocation+"output.xes");
            } else {
                file = new File(XESFileLocation+"output.txt");
            }
                // File file = new File(XESFileLocation+"output.txt");

        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try{
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            String xesHead ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+"\n"+
            "<log xes.version=\"2.0\" xes.features=\"nested-attributes\" openxes.version=\"1.0RC7\" xmlns=\"http://www.xes-standard.org/\">"+"\n"+
                    "<extension name=\"Lifecycle\" prefix=\"lifecycle\" uri=\"http://www.xes-standard.org/lifecycle.xesext\"/>"+"\n"+
                    "<extension name=\"Organizational\" prefix=\"org\" uri=\"http://www.xes-standard.org/org.xesext\"/>"+"\n"+
                    "<extension name=\"Time\" prefix=\"time\" uri=\"http://www.xes-standard.org/time.xesext\"/>"+"\n"+
                    "<extension name=\"Concept\" prefix=\"concept\" uri=\"http://www.xes-standard.org/concept.xesext\"/>"+"\n"+
                    "<extension name=\"Semantic\" prefix=\"semantic\" uri=\"http://www.xes-standard.org/semantic.xesext\"/>"+"\n"+
                    "<global scope=\"trace\">"+"\n"+
                        "<string key=\"concept:name\" value=\"UNKNOWN\"/>"+"\n"+
                            "<string key=\"traceID\" value=\"UNKNOWN\"/>"+"\n"+
                    "</global>"+"\n"+
                    "<global scope=\"event\">"+"\n"+
                        "<string key=\"concept:name\" value=\"UNKNOWN\"/>"+"\n"+
                        "<string key=\"lifecycle:transition\" value=\"complete\"/>"+"\n"+
                        "<date key=\"time:timestamp\" value=\"2008-12-09T08:20:01.527+01:00\"/>"+"\n"+
                        // "<date key=\"time:timestamp\" value=\""+currentTimestamp+"\"/>"+"\n"+
                            "<string key=\"caseId\" value=\"UNKNOWN\"/>"+"\n"+
                            "<int key=\"duration\" value=\"1\"/>"+"\n"+
                            "<int key=\"callingOrder\" value=\"1\"/>"+"\n"+
                            "<int key=\"depth\" value=\"1\"/>"+"\n"+
                    "</global>"+"\n";

            System.out.println("Starting output file Conversion");
            int i = 0;
            String xesText = "";
            for (Entry entry : dataMap.entrySet()) {
                        i++;
                        // System.out.println("Trace head at the begining : "+traceHead);
                        int j=0;
                        ArrayList<Event> entryList = (ArrayList<Event>) entry.getValue();
                        for (Event e : entryList){
                            if (j == 0) {
                            xesText =  xesText + "<trace>"+"\n"+
                            "<string key=\"concept:name\" value=\"Trace"+i+"\"/>"+
                            "<string key=\"traceID\" value=\""+entry.getKey()+"\"/>"+"\n";
                            if (e.getUserId() != null) {
                                xesText = xesText + "<string key=\"user:id\" value=\""+e.getUserId()+"\"/>"+"\n";
                            }
                            if (e.getSessionId() != null) {
                                xesText = xesText + "<string key=\"sessionId:id\" value=\""+e.getSessionId()+"\"/>"+"\n";
                            }
                            }
                            j++;

                            xesText = xesText+"<event>"+"\n"+
                            "<string key=\"concept:name\" value=\""+e.methodName()+"\"/>"+"\n"+
                            "<string key=\"lifecycle:transition\" value=\""+e.getTransactionType()+"\"/>"+"\n"+
                            "<date key=\"time:timestamp\" value=\""+e.getFormattedDate()+"\"/>"+"\n"+
                                    "<string key=\"caseId\" value=\""+e.getSequenceId()+"\"/>"+"\n"+
                                    "<int key=\"method:duration\" value=\""+e.getMethodDuration()+"\"/>"+"\n"+
                                    "<int key=\"method:callingOrder\" value=\""+e.getCallingOrder()+"\"/>"+"\n"+
                                    "<int key=\"method:depth\" value=\""+e.getDeptOfCallingStack()+"\"/>"+"\n";

                            if (e.getMethodId() != null) {
                                xesText = xesText + "<string key=\"method:id\" value=\""+e.getMethodId()+"\"/>"+"\n";
                            }
                            
                            if (e.getInstance() != null) {
                                xesText = xesText + "<string key=\"method:instance\" value=\""+e.getInstance()+"\"/>"+"\n";
                            }
                            if (e.getReturnType() != null) {
                                xesText = xesText + "<string key=\"method:returnType\" value=\""+e.getReturnType()+"\"/>"+"\n";
                            }
                            if (e.getInputParameters() != null) {
                                xesText = xesText + "<string key=\"method:inputParameters\" value=\""+e.getInputParameters()+"\"/>"+"\n";
                            }
                            if (e.isIsConstructor()) {
                                xesText = xesText + "<string key=\"method:isConstructor\" value=\""+e.isIsConstructor()+"\"/>"+"\n";
                            }
                            if (e.isIsNested()) {
                                xesText = xesText + "<string key=\"method:nested\" value=\""+e.isIsNested()+"\"/>"+"\n";
                            }
                            if (e.getDeptOfCallingStack() >0) {
                                xesText = xesText + "<string key=\"method:depth\" value=\""+e.getDeptOfCallingStack()+"\"/>"+"\n";
                            }
                            if (e.getCallingOrder() > 0) {
                                xesText = xesText + "<string key=\"method:callingOrder\" value=\""+e.getCallingOrder()+"\"/>"+"\n";
                            }
                            if (e.getSourceLocation() != null) {
                                xesText = xesText + "<string key=\"method:sourceLocation\" value=\""+e.getSourceLocation()+"\"/>"+"\n";
                            }
                            if (e.getPackageName() != null) {
                                xesText = xesText + "<string key=\"method:packageName\" value=\""+e.getPackageName()+"\"/>"+"\n";
                            }
                            if(e.getThreadId() != null) {
                                xesText = xesText + "<string key=\"method:threadId\" value=\""+e.getThreadId()+"\"/>"+"\n";
                            }
                            if(e.getCallerInstance() != null) {
                                xesText = xesText + "<string key=\"method:callerInstance\" value=\""+e.getCallerInstance()+"\"/>"+"\n";
                            }
                            if(e.getCallerMethod() != null) {
                                xesText = xesText + "<string key=\"method:callerMethod\" value=\""+e.getCallerMethod()+"\"/>"+"\n";
                            }
                         xesText = xesText +"</event>"; 
                            
                        }
                        xesText =  xesText+"</trace>"+"\n";
                        // System.out.println("############### TRACE : "+ xesText);
            }
            xesText =  xesHead + xesText+"</log>"+"\n";
            writer.println(xesText);
            writer.close();
            System.out.println("Writing data to output file completed...");  
        } catch (Exception e) {
            System.out.println("Error in output file writing" + e);
        }
    }

// data format converter
    private static String convertDate(long dateInNano) {
		try {
		// String target = "01/01/1970 12:00:00:000000";  // Your given date string
		String target = "1970-01-01 12:00:00";
		long nanoTime = Math.abs(dateInNano%1000000);
		long millis = TimeUnit.MILLISECONDS.convert(dateInNano, TimeUnit.NANOSECONDS); 
		
		// DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //2008-12-09T08:20:01.527+01:00

		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = formatter.parse(target);
		
		long newTimeInmillis = date.getTime() + millis;
		
		Date date2 = new Date(newTimeInmillis);
		String formattedDate =  formatter.format(date2);
		return (formattedDate.split(" ")[0] +"T"+formattedDate.split(" ")[1]+"."+nanoTime);
		// return formatter.format(date2)+"."+nanoTime;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return ""+dateInNano;
	} 

}
