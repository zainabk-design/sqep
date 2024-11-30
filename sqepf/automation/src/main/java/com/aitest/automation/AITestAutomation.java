package com.aitest.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.util.*;
import java.awt.Color;
import io.github.bonigarcia.wdm.WebDriverManager;
public class AITestAutomation {
    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(AITestAutomation.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    public AITestAutomation(ChromeOptions options) {
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver(options);
    }

    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Smart element detection using multiple strategies
     */
    public WebElement smartFindElement(String identifier) {
        List<By> strategies = Arrays.asList(
            By.id(identifier),
            By.name(identifier),
            By.className(identifier),
            By.cssSelector(identifier),
            By.xpath("//*[contains(text(),'" + identifier + "')]")
        );

        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);

        for (By strategy : strategies) {
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(strategy));
                logger.info("Element found using strategy: " + strategy.toString());
                return element;
            } catch (Exception e) {
                logger.debug("Strategy failed: " + strategy.toString());
            }
        }

        logger.error("Element not found with identifier: " + identifier);
        return null;
    }

    /**
     * Visual regression testing with AI-based comparison
     */
    public boolean visualRegressionTest(String baselineImagePath, String actualImagePath, double threshold) {
        try {
            BufferedImage baseline = ImageIO.read(new File(baselineImagePath));
            BufferedImage actual = ImageIO.read(new File(actualImagePath));

            if (baseline.getWidth() != actual.getWidth() || baseline.getHeight() != actual.getHeight()) {
                logger.error("Image dimensions do not match");
                return false;
            }

            int totalPixels = baseline.getWidth() * baseline.getHeight();
            int differentPixels = 0;

            for (int x = 0; x < baseline.getWidth(); x++) {
                for (int y = 0; y < baseline.getHeight(); y++) {
                    Color baselineColor = new Color(baseline.getRGB(x, y));
                    Color actualColor = new Color(actual.getRGB(x, y));

                    if (!colorsMatch(baselineColor, actualColor)) {
                        differentPixels++;
                    }
                }
            }

            double similarity = 1.0 - ((double) differentPixels / totalPixels);
            logger.info("Image similarity: " + similarity);
            return similarity >= threshold;

        } catch (Exception e) {
            logger.error("Visual regression test failed", e);
            return false;
        }
    }

    private boolean colorsMatch(Color c1, Color c2) {
        int tolerance = 10;
        return Math.abs(c1.getRed() - c2.getRed()) <= tolerance &&
               Math.abs(c1.getGreen() - c2.getGreen()) <= tolerance &&
               Math.abs(c1.getBlue() - c2.getBlue()) <= tolerance;
    }

    /**
     * Self-healing test mechanism
     */
    public boolean selfHealingTest(TestFunction testFunction) {
        int maxRetries = 3;
        int retryCount = 0;
        List<Exception> exceptions = new ArrayList<>();

        while (retryCount < maxRetries) {
            try {
                return testFunction.execute();
            } catch (Exception e) {
                exceptions.add(e);
                logger.warn("Test failed, attempt " + (retryCount + 1) + " of " + maxRetries, e);
                retryCount++;

                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(2000); // Wait before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        logger.error("Test failed after " + maxRetries + " attempts");
        exceptions.forEach(e -> logger.error("Exception occurred:", e));
        return false;
    }

    /**
     * Performance monitoring and anomaly detection
     */
    public void monitorPerformance(String elementId) {
        List<Long> loadTimes = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            long startTime = System.currentTimeMillis();
            smartFindElement(elementId);
            long endTime = System.currentTimeMillis();
            loadTimes.add(endTime - startTime);
        }

        detectAnomalies(loadTimes);
    }

    private void detectAnomalies(List<Long> measurements) {
        double mean = measurements.stream()
                .mapToDouble(Long::doubleValue)
                .average()
                .orElse(0.0);

        double stdDev = Math.sqrt(measurements.stream()
                .mapToDouble(value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0));

        measurements.forEach(value -> {
            if (Math.abs(value - mean) > 2 * stdDev) {
                logger.warn("Anomaly detected: " + value + " ms (Mean: " + mean + " ms, StdDev: " + stdDev + " ms)");
            }
        });
    }

    /**
     * Generate test report with AI insights
     */
    public void generateReport(List<TestResult> results) {
        StringBuilder report = new StringBuilder();
        report.append("AI Test Automation Report\n");
        report.append("========================\n\n");

        // Summary statistics
        long passed = results.stream().filter(TestResult::isPassed).count();
        long failed = results.size() - passed;
        
        report.append(String.format("Total Tests: %d\n", results.size()));
        report.append(String.format("Passed: %d\n", passed));
        report.append(String.format("Failed: %d\n\n", failed));

        // Detailed results with insights
        report.append("Detailed Results:\n");
        results.forEach(result -> {
            report.append(String.format("Test: %s\n", result.getName()));
            report.append(String.format("Status: %s\n", result.isPassed() ? "PASSED" : "FAILED"));
            report.append(String.format("Duration: %d ms\n", result.getDuration()));
            if (!result.isPassed()) {
                report.append(String.format("Error: %s\n", result.getErrorMessage()));
            }
            report.append("\n");
        });

        try {
            FileUtils.writeStringToFile(new File("test-report.txt"), report.toString(), "UTF-8");
            logger.info("Test report generated successfully");
        } catch (Exception e) {
            logger.error("Failed to generate test report", e);
        }
    }

    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }
    
}