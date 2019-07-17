import static org.hamcrest.MatcherAssert.*; 
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.Is;

import com.alex_zaitsev.adg.io.Arguments;
import com.alex_zaitsev.adg.io.Filters;
import com.alex_zaitsev.adg.SmaliAnalyzer;

public class SmaliAnalyzerTests {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private Arguments defaultArguments;
    private Filters defaultFilters;

    @Before
    public void setUp() throws IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        String apkPath = folder.newFile("application.apk").getAbsolutePath();
        String projectPath = folder.newFolder("project").getAbsolutePath();
        String outputPath = folder.newFolder("result").getAbsolutePath();
        String filtersPath = folder.newFile("filters.json").getAbsolutePath();
        defaultArguments = new Arguments(apkPath, projectPath, outputPath, filtersPath);

        String packageName = "com.example.package";
        String[] ignoredClasses = new String[] {
            ".*Dagger.*", ".*Injector.*", ".*\\$_ViewBinding$", ".*_Factory$"
        };
        defaultFilters = new Filters(packageName, Filters.DEFAULT_PROCESS_INNER, 
            ignoredClasses);
    }

    @After
    public void teardown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * When `run` method is called, intro info message is printed.
     */
    @Test
    public void runPrintsIntroMessage() {
        SmaliAnalyzer sut = new SmaliAnalyzer(defaultArguments, defaultFilters, null, null);

        sut.run();

        String message = "Analyzing dependencies...";
        assertThat(outContent.toString(), containsString(message));
    }

    /**
     * If project folder does not exist, error message is printed
     * and `run` returns false.
     */
    @Test
    public void runPrintsMessageAndReturnsFalseIfProjectFolderIsAbsent() throws IOException {
        Arguments argsWihNonExistingPath = defaultArguments;
        File nonExisting = new File("non-existing");
        argsWihNonExistingPath.setProjectPath(nonExisting.getAbsolutePath());
        SmaliAnalyzer sut = new SmaliAnalyzer(argsWihNonExistingPath, defaultFilters, null, null);

        boolean result = sut.run();

        String message = nonExisting + " does not exist!";
        assertThat(errContent.toString(), containsString(message));
        assertThat(result, is(false));
    }

    /**
     * If apk uses instant run, error message is printed
     * and `run` returns false.
     */
    @Test
    public void runPrintsMessageAndReturnsFalseIfApkUsesInstantRun() throws IOException {
        File project = new File(defaultArguments.getProjectPath());
        File unknown = new File(project, "unknown");
        unknown.mkdir();
        File unknownZip = new File(unknown, "instant-run.zip");
        unknownZip.createNewFile();
        SmaliAnalyzer sut = new SmaliAnalyzer(defaultArguments, defaultFilters, null, null);

        boolean result = sut.run();

        String message = "Enabled Instant Run feature detected.";
        assertThat(errContent.toString(), containsString(message));
        assertThat(result, is(false));
    }

    /**
     * If good params were provided to `SmaliAnalyzer`,
     * `run` returns true.
     */
    @Test
    public void runWithDefaultParamsReturnsTrue() {
        SmaliAnalyzer sut = new SmaliAnalyzer(defaultArguments, defaultFilters, null, null);

        boolean result = sut.run();

        assertThat(result, is(true));
    }
}