package com.aitest.automation;

@FunctionalInterface
public interface TestFunction {
    boolean execute() throws Exception;
}