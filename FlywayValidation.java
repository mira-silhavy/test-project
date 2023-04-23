import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings({"NullabilityAnnotations", "UseOfSystemOutOrSystemErr"})
class FlywayValidation {

    private static final String MIGRATE_PATH = "main-modules/app/src/main/java/db";

    private List<String> findExistingMigrates(String searchedVersion) throws IOException {
        List<String> allMigrates = new ArrayList<>();
        Path root = Paths.get(MIGRATE_PATH);

        if (!Files.exists(root)) {
            return allMigrates;
        }

        try (Stream<Path> walk = Files.walk(root)) {
            walk.filter(p -> p.toFile().isFile())
                    .filter(p -> p.toString().contains(searchedVersion))
                    .forEach(p -> allMigrates.add(p.toFile().getName()));
        }

        System.out.println("Found " + allMigrates.size() + " migrates for version " + searchedVersion);

        return allMigrates;
    }

    private List<String> processNewMigrates(String diffFileWithNewMigrates) throws IOException {
        List<String> newMigrates;

        try (Stream<String> lines = Files.lines(Path.of(diffFileWithNewMigrates))) {
            newMigrates = lines.filter(line -> !line.trim().isEmpty())
                    .toList();
        }

        System.out.println("Listing all new migrates:");
        newMigrates.forEach(System.out::println);
        System.out.println();

        return newMigrates;
    }

    private List<String> validateAddedMigrates(List<String> addedMigrates, String expectedVersion) throws Exception {
        System.out.println("Validating new migrates");

        List<String> addedMigratesFilenames = new ArrayList<>();

        for (String migrate : addedMigrates) {
            if (migrate.startsWith(MIGRATE_PATH)) {
                migrate = migrate.substring(MIGRATE_PATH.length()).replaceAll("^[\\\\/]+", "");
            }

            Path migratePath = Paths.get(migrate);
            String migrateDirectoryVersion = migratePath.getName(0).toString();
            String migrateFilename = migratePath.getFileName().toString();

            System.out.println("Validating migrate: " + migrateFilename);

            int splitIndex = migrateFilename.indexOf("__");
            if (splitIndex == -1) {
                String message = "Added migrate is missing \"__\" in name: " + migrateFilename;
                throw new Exception(message);
            }
            String migrateFilenameVersion = migrateFilename.substring(0, splitIndex);
            int migrateFilenameVersionMinorIndex = migrateFilenameVersion.lastIndexOf("_");
            String migrateFilenameVersionMinor = migrateFilenameVersion.substring(migrateFilenameVersionMinorIndex + 1);
            String migrateFilenameVersionMajor = migrateFilenameVersion.substring(0, migrateFilenameVersionMinorIndex);

            System.out.println("Migrate major version: " + migrateFilenameVersionMajor);
            System.out.println("Migrate minor version: " + migrateFilenameVersionMinor);
            System.out.println("Migrate directory version: " + migrateDirectoryVersion);

            if (!migrateDirectoryVersion.equalsIgnoreCase(expectedVersion)) {
                String message = "New migrate is in invalid version directory: " + migrateDirectoryVersion + ", expected: " + expectedVersion;
                throw new Exception(message);
            }

            if (!migrateFilenameVersionMajor.equalsIgnoreCase(expectedVersion)) {
                String message = "New migrate has invalid filename version: " + migrateFilenameVersionMajor + ", expected: " + expectedVersion;
                throw new Exception(message);
            }

            addedMigratesFilenames.add(migrateFilename);
        }

        System.out.println("All new migrates have valid directory and major version\n");

        return addedMigratesFilenames;
    }

    private void validateExistingMigrates(List<String> addedMigratesFilenames, String expectedVersion) throws Exception {
        System.out.println("Validating all current version migrates");

        List<String> existingMigrates = findExistingMigrates(expectedVersion);

        List<String> minorVersions = new ArrayList<>();
        List<String> minorVersionsExcludingAdded = new ArrayList<>();
        List<String> minorVersionsAdded = new ArrayList<>();

        for (String migrateFilename : existingMigrates) {
            System.out.println("Validating migrate: " + migrateFilename);
            int splitIndex = migrateFilename.indexOf("__");
            if (splitIndex == -1) {
                String message = "Existing migrate is missing \"__\" in name: " + migrateFilename;
                throw new Exception(message);
            }
            String migrateFilenameVersion = migrateFilename.substring(0, splitIndex);
            int migrateFilenameVersionMinorIndex = migrateFilenameVersion.lastIndexOf("_");
            String migrateFilenameVersionMinor = migrateFilenameVersion.substring(migrateFilenameVersionMinorIndex + 1);
            String migrateFilenameVersionMajor = migrateFilenameVersion.substring(0, migrateFilenameVersionMinorIndex);

            if (!migrateFilenameVersionMajor.equalsIgnoreCase(expectedVersion)) {
                String message = "Existing migrate has invalid version: " + migrateFilenameVersionMajor + " expected: " + expectedVersion;
                throw new Exception(message);
            }

            minorVersions.add(migrateFilenameVersionMinor);
            if (addedMigratesFilenames.contains(migrateFilename)) {
                minorVersionsAdded.add(migrateFilenameVersionMinor);
            } else {
                minorVersionsExcludingAdded.add(migrateFilenameVersionMinor);
            }
        }

        Set<String> duplicates = findDuplicatesInMinorVersions(minorVersions);

        System.out.println("\nChecking for duplicate minor versions");

        if (!duplicates.isEmpty()) {
            StringBuilder message = new StringBuilder("Duplicate minor versions found:\n");
            for (String duplicate : duplicates) {
                message.append(duplicate).append("\n");
            }
            throw new Exception(message.toString());
        } else {
            System.out.println("No duplicate minor versions found\n");
        }

        System.out.println("Checking for out-of-order minor versions");

        for (String addedMinor : minorVersionsAdded) {
            int addNumMinor = Integer.parseInt(addedMinor);
            for (String existingMinor : minorVersionsExcludingAdded) {
                int existingNumMinor = Integer.parseInt(existingMinor);
                if (addNumMinor < existingNumMinor) {
                    String message = "New migrate has invalid minor version: " + addedMinor + ", expected greater than: " + existingMinor;
                    throw new Exception(message);
                }
            }
        }

        System.out.println("No out-of-order minor versions found");
    }

    private Set<String> findDuplicatesInMinorVersions(List<String> minorVersions) {
        Set<String> duplicates = new HashSet<>();
        for (String minorVersion : minorVersions) {
            if (Collections.frequency(minorVersions, minorVersion) > 1) {
                duplicates.add(minorVersion);
            }
        }
        return duplicates;
    }

    private void validate(String inputMigrateFile, String replacedInputVersion) throws Exception {
        List<String> addedMigrates = processNewMigrates(inputMigrateFile);
        List<String> addedMigratesFilenames = validateAddedMigrates(addedMigrates, replacedInputVersion);
        validateExistingMigrates(addedMigratesFilenames, replacedInputVersion);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String input_version = args[0];
        String input_migrate_file = args[1];
        String replaced_input_version = "v" + input_version.replaceAll("\\D+", "_");

        System.out.println("Provided application version " + replaced_input_version + "\n");

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        String result = "";
        boolean success = true;
        try {
            new FlywayValidation().validate(input_migrate_file, replaced_input_version);
            result = "Flyway validation successful";
        } catch (Exception e) {
            result = e.getMessage();
            success = false;
        } finally {
            System.out.println();
            System.out.println("Flyway validation results:");
            System.out.println(result);
            if (!isWindows) {
                System.out.println("\nSetting Github Action property $GITHUB_STEP_SUMMARY");
                setGithubActionProperty(result);
            }
            if (!success) {
                System.exit(1);
            }
        }
    }

    private static void setGithubActionProperty(String result) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("sh", "-c", "echo \"Flyway validation results:\" >> $GITHUB_STEP_SUMMARY").start();
        Process process2 = new ProcessBuilder("sh", "-c", "echo \"" + result + "\" >> $GITHUB_STEP_SUMMARY").start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.out.println("Exited with error code " + exitCode);
            Scanner scanner = new Scanner(new SequenceInputStream(process.getErrorStream(), process2.getErrorStream()));
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }

}