package com.seleniumboot.migrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MigrationReportTest {

    @Test
    void matchesSnapshot() throws Exception {
        var report = fixtureReport();

        String expected = Files.readString(
                Path.of("src/test/resources/migration-report.md")
        ).replace("\r\n", "\n");

        assertEquals(expected, MigrationReport.render(report));
    }

    @Test
    void rendersReportWithZeroFindings() {
        var report = new Report(0, 0, List.of(), List.of());

        String expected = """
                # Selenium Boot Migration Report

                ## Summary

                - Files found: 0
                - Files parsed: 0
                - Automatic findings: 0
                - Manual findings: 0
                - Unparsable files: 0

                ## Detected technologies

                - No supported build descriptor found

                ## Recognized technologies

                - No supported test technologies detected

                ## Rules applied per file

                - No migration rules detected

                ## Warnings

                - None

                ## Manual actions

                - None

                ## Estimated migration confidence

                100% estimated migration confidence. This is an estimate, not a guarantee.
                """;

        assertEquals(expected, MigrationReport.render(report));
    }

    @Test
    void writesMigrationReport(@TempDir Path directory) throws Exception {
        var report = fixtureReport();

        Path reportPath = MigrationReport.write(directory, report);

        assertEquals(directory.resolve("MIGRATION_REPORT.md"), reportPath);
        assertTrue(Files.exists(reportPath));
        assertEquals(MigrationReport.render(report), Files.readString(reportPath));
    }

    private static Report fixtureReport() {
        return new Report(
                3,
                2,
                List.of("Broken.java"),
                List.of(
                        new Finding(
                                "MIG-002",
                                Finding.Status.AUTO,
                                "Auto.java",
                                4,
                                "WebDriverManager",
                                "Delete; Selenium Manager fetches drivers automatically."
                        ),
                        new Finding(
                                "MIG-003",
                                Finding.Status.MANUAL,
                                "Page.java",
                                8,
                                "WebDriverWait / ExpectedConditions",
                                "Use $(locator) auto-wait or getWait(); review the condition by hand."
                        )
                ),
                List.of("Build system: Maven"),
                List.of("Selenium", "JUnit 5"),
                Map.of("By.id", 2L)
        );
    }
}
