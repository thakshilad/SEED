package com.uniMelb.XESConverter.entities;

/**
 * This class contains the event information extracted from 
 * kieker log. Trace or use case is a collection of events.
 */
public class Event {

    private long sequenceId;
    private String methodSignature;
    private String sessionId;
    private String traceId;
    private long inTime;
    private long outTime;
    private int callingOrder;
    private int deptOfCallingStack;
    private String formattedDate ="";
    private int integerId;

    private String methodName;
    private String methodId;
    private String timeStamp="";
    private String transactionType;
    private String instance;
    private String returnType;
    private String inputParameters;
    private boolean isConstructor;
    private boolean isNested;
    private String sourceLocation;
    private String packageName;
    private String threadId;
    private String callerInstance;
    private String callerMethod;
    private String userId;
    private long duration;
    
    public Event() {
    }



    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }
    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    public void setInTime(long inTime) {
        this.inTime = inTime;
    }
    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }
    public void setCallingOrder(int callingOrder) {
        this.callingOrder = callingOrder;
    }
    public void setDeptOfCallingStack(int deptOfCallingStack) {
        this.deptOfCallingStack = deptOfCallingStack;
    }
    public long getSequenceId() {
        if (sequenceId > 0) {
            return sequenceId;
        } else {
            return System.currentTimeMillis();
        }
       
    }
    public String getMethodSignature() {
        return methodSignature;
    }
    public String getSessionId() {
        return sessionId;
    }
    public String getTraceId() {
        return traceId;
    }
    public long getInTime() {
        return inTime;
    }
    public long getOutTime() {
        return outTime;
    }
    public int getCallingOrder() {
        return callingOrder;
    }
    public int getDeptOfCallingStack() {
        return deptOfCallingStack;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getFormattedDate() {
        if (formattedDate != null || !formattedDate.equals("")) {
            return formattedDate;
        } else {
            return timeStamp;
        }
        
    }

    public double getMethodDuration() {
        
        if(this.inTime > 0 & this.outTime > 0) {
            return Double.parseDouble("" + (this.outTime - this.inTime));
        }
        return duration;
    }

    public String methodName() {
        if (this.methodSignature != null) {
            int methodIndex = methodSignature.split(" ").length-1;
            return this.methodSignature.split(" ")[methodIndex];
        } else {
            return methodName;
        }
        
    }
    public String shortMethodName() {
        if (this.methodSignature != null) {
            return this.methodSignature;
        }
        if (this.methodSignature != null) {
            String parameters = "";
            String tempMethodSignature = "";
            if (this.methodSignature.contains("(")) {
                tempMethodSignature = this.methodSignature.split("(?=\\()")[0];
                parameters = this.methodSignature.split("(?=\\()")[1];
            } else {
                tempMethodSignature = methodSignature;
            }

            int methodIndex = tempMethodSignature.split(" ").length-1;
            String fullMethodName = tempMethodSignature.split(" ")[methodIndex];
            String className = fullMethodName.split("\\(")[0];
            String[] MethodNameArray = className.split("\\.");
            if (MethodNameArray != null & MethodNameArray.length >=2) {
            String shortName = MethodNameArray[MethodNameArray.length - 2]+"."+MethodNameArray[MethodNameArray.length - 1];
            if (shortName.contains("string") || shortName.contains("lang")){ //) {
                System.out.println("String Entry + ######################### "+ tempMethodSignature);
            }
            // System.out.println("Method Signagure : " + methodSignature + " Short Name : +" + shortName+ " Parameters : "+ parameters );
            return shortName; //+parameters;
            
        }
            
        }
        return "";
    }

    @Override
    public String toString() {
        return "Event [sequenceId=" + sequenceId + ", methodSignature=" + methodSignature + ", sessionId=" + sessionId
                + ", traceId=" + traceId + ", inTime=" + inTime + ", outTime=" + outTime + ", callingOrder="
                + callingOrder + ", deptOfCallingStack=" + deptOfCallingStack + ", formattedDate=" + formattedDate
                + ", integerId=" + integerId + "]";
    }


    public int getIntegerId() {
        return integerId;
    }


    public void setIntegerId(int integerId) {
        this.integerId = integerId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTransactionType() {
        if (transactionType != null && !("").equals(transactionType)) {
            return transactionType;
        } else {
            return "complete";
        }
        
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(String inputParameters) {
        this.inputParameters = inputParameters;
    }

    public boolean isIsConstructor() {
        return isConstructor;
    }

    public void setIsConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    public boolean isIsNested() {
        return isNested;
    }

    public void setIsNested(boolean isNested) {
        this.isNested = isNested;
    }


    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getCallerInstance() {
        return callerInstance;
    }

    public void setCallerInstance(String callerInstance) {
        this.callerInstance = callerInstance;
    }

    public String getCallerMethod() {
        return callerMethod;
    }

    public void setCallerMethod(String callerMethod) {
        this.callerMethod = callerMethod;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }
    
}
