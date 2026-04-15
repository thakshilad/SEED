package com.uniMelb.XESConverter.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.uniMelb.XESConverter.entities.Event;


@Component
public class AppComponent {

    public static Map<String, List<Event>> dataMap = new HashMap<>(0); // event data - trace ID and event entity List

    // temp storage of log entries
    List<String> filteredLineList = new ArrayList<String>(0);

    @Value("${app.logFolder}")
    private String logFolder;

    @Value("${app.XESFileLocation}")
    private String XESFileLocation;

    
    // add parameters to xes file output and xes file output required.

    public void initiateIdentification() {
        System.out.println("=========== System Parameters Starts =============");
        System.out.println("app.logFolder : "+ logFolder );
        System.out.println("app.XESFileLocation : "+ XESFileLocation );
        System.out.println("=========== System Parameters Ends =============");

        readAndConvert(logFolder);
    }


    // read the software log folder
    public void readAndConvert(String logFolder) {
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
			convertLogsToXesFormat(filteredLineList);
		} catch (Exception e) {
			System.out.println("Exception in read log file : "+e.getMessage() );
		}

     }

     // convert filtered kieker logs to xes format
    public void convertLogsToXesFormat(List<String> dataList) {

        File file = new File(XESFileLocation+"output.xes");

        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        try{
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            // Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            String xesText ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+"\n"+
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
            System.out.println("Data list size "+ dataList.size());
            // for(int i=0;i<dataList.size();i++){
                for(int i=dataList.size()-1; i>=0; i--){
                // System.out.println("event loop : "+i);
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
                // List<Event> miningTempList = new ArrayList<>(0);
                if(dataMap.get(event.getTraceId()) != null) {
                    tempList = dataMap.get(event.getTraceId());
                    // miningTempList = miningDatMap.get(event.getTraceId());
                    // System.out.println("%%%%%%%%%% Element available in temp list");
                }
                
                tempList.add(event);
                Collections.sort(tempList, (o1, o2) -> o1.getCallingOrder() - o2.getCallingOrder());
                dataMap.put(event.getTraceId(), tempList);

            }
            System.out.println("Starting XES Conversion");
            // uncomment below to get the xes files, takes time to produce the output, hence commenting
            int i = 1;
            for (Entry entry : dataMap.entrySet()) {
                // System.out.println(entry.getKey() + "/" + entry.getValue());
                    xesText = xesText + "<trace>"+"\n"+
                            "<string key=\"concept:name\" value=\"Trace"+i+"\"/>"+
                            "<string key=\"traceID\" value=\""+entry.getKey()+"\"/>"+"\n";
                        i++;
                        // System.out.println("### : "+i);
                        ArrayList<Event> entryList = (ArrayList<Event>) entry.getValue();
                        for (Event e : entryList){
                            xesText = xesText+"<event>"+"\n"+
                            "<string key=\"concept:name\" value=\""+e.methodName()+"\"/>"+"\n"+
                            "<string key=\"lifecycle:transition\" value=\"complete\"/>"+"\n"+
                            "<date key=\"time:timestamp\" value=\""+e.getFormattedDate()+"\"/>"+"\n"+
                                    "<string key=\"caseId\" value=\""+e.getSequenceId()+"\"/>"+"\n"+
                                    "<int key=\"duration\" value=\""+e.getMethodDuration()+"\"/>"+"\n"+
                                    "<int key=\"callingOrder\" value=\""+e.getCallingOrder()+"\"/>"+"\n"+
                                    "<int key=\"depth\" value=\""+e.getDeptOfCallingStack()+"\"/>"+"\n"+"</event>"; 
                            
                        }
                        xesText = xesText+"</trace>"+"\n";
            }
            xesText = xesText+"</log>"+"\n";
            writer.println(xesText);
            writer.close();
            System.out.println("Writing data to XES file completed...");  

        } catch (Exception e) {
            System.out.println("Exception in convert logs to XES format : "+ e.getLocalizedMessage());
            e.printStackTrace();
        }
	}

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
