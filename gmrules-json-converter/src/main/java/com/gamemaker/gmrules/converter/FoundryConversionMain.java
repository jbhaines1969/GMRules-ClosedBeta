package com.gamemaker.gmrules.converter;

import com.gamemaker.gmrules.GameSaveIO;
import java.nio.file.Path;

/** Command-line entry point for the provisional Foundry PF2e level-one conversion. */
public final class FoundryConversionMain {
    private FoundryConversionMain() {
    }

    public static void main(String[] args) throws Exception {
        Path source = args.length > 0 ? Path.of(args[0]) : Path.of("games", "pf2e");
        Path output = args.length > 1
            ? Path.of(args[1])
            : Path.of("target", "generated-games", "pathfinder-2e-remaster-level1-provisional.gmrf");
        FoundryConversionReport report = new FoundryPf2eLevelOneConverter().convert(source);
        new GameSaveIO().saveToPath(report.getGame(), output);
        System.out.println("Wrote " + output.toAbsolutePath().normalize());
        System.out.println("Imported " + report.getImportedCounts());
        System.out.println("Excluded OGL records: " + report.getExcludedOgl());
        System.out.println("Excluded unknown-license records: " + report.getExcludedUnknownLicense());
        System.out.println("Diagnostics: " + report.getGame().getCatalogDiagnostics().size());
        report.getGame().getCatalogDiagnostics().forEach(diagnostic -> System.out.println(
            diagnostic.getSeverity() + " " + diagnostic.getCode() + " "
                + diagnostic.getElementType() + " " + diagnostic.getElementName() + ": " + diagnostic.getMessage()
        ));
    }
}
