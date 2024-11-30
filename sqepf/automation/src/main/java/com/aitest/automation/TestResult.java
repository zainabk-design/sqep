package com.aitest.automation;

public class TestResult {
    private String name;
    private boolean passed;
    private long duration;
    private String errorMessage;

    public TestResult(String name, boolean passed, long duration, String errorMessage) {
        this.name = name;
        this.passed = passed;
        this.duration = duration;
        this.errorMessage = errorMessage;
    }

    public String getName() { return name; }
    public boolean isPassed() { return passed; }
    public long getDuration() { return duration; }
    public String getErrorMessage() { return errorMessage; }
}
