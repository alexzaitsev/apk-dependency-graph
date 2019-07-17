import static org.hamcrest.MatcherAssert.*; 
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.hamcrest.core.StringContains;

import com.alex_zaitsev.adg.io.ArgumentReader;
import com.alex_zaitsev.adg.io.Arguments;

public class ArgumentReaderTests {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private File apkFile;
    private File filterFile;

    @Before
    public void setUp() throws IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        apkFile = folder.newFile("test.apk");
        filterFile = folder.newFile("filter.json");
    }

    @After
    public void teardown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * When wrong or incomplete arguments are passed
     * to ArgumentReader, read() must return Null and print error message.
     */
    @Test
    public void wrongArgumentsReturnsNullAndPrintsMessage() {
        String[] inputArgs = new String[] {"-i", "/path/"};
        ArgumentReader sut = new ArgumentReader(inputArgs);

        Arguments args = sut.read();

        assertThat(args, nullValue());
        assertThat(errContent.toString(), containsString("must be provided!"));
        assertThat(errContent.toString(), containsString("Usage:"));
    }

    /**
     * When non-existing Apk file path is passed
     * to ArgumentReader, read() must return Null and print error message.
     */
    @Test
    public void nonExistingApkPathReturnsNullAndPrintsMessage() {
        String[] inputArgs = new String[] {"-i", "/path/", "-o", "/path/", "-a", "/wrong/"};
        ArgumentReader sut = new ArgumentReader(inputArgs);

        Arguments args = sut.read();

        assertThat(args, nullValue());
        String message = File.separator + "wrong is not found!";
        assertThat(errContent.toString(), containsString(message));
    }

    /**
     * When wrong filter is passed to ArgumentReader, 
     * read() must return Arguments and print error message.
     */
    @Test
    public void wrongFilterFileReturnsNullAndPrintsMessage() {
        String[] inputArgs = new String[] {"-i", "/path/", "-o", "/path/", 
                                           "-a", apkFile.getAbsolutePath(), 
                                           "-f", "/wrong/"};
        ArgumentReader sut = new ArgumentReader(inputArgs);

        Arguments args = sut.read();

        assertThat(args, nullValue());
        String message = File.separator + "wrong is not found!";
        assertThat(errContent.toString(), containsString(message));
    }

    /**
     * When correct arguments without filter are passed
     * to ArgumentReader, read() must return correct Arguments.
     */
    @Test
    public void correctArgumentsWithoutFiltersReturnsArguments() {
        String projectPath = "/pathI/";
        String resultPath = "/pathO/";
        String apkPath = apkFile.getAbsolutePath();
        String[] inputArgs = new String[] {"-i", projectPath, "-o", resultPath, 
                                           "-a", apkPath};
        ArgumentReader sut = new ArgumentReader(inputArgs);

        Arguments args = sut.read();

        assertThat(args, notNullValue());
        assertThat(args.getFiltersPath(), nullValue());
        assertThat(args.getProjectPath(), equalTo(projectPath));
        assertThat(args.getResultPath(), equalTo(resultPath));
        assertThat(args.getApkFilePath(), equalTo(apkPath));
    }

    /**
     * When correct arguments are passed to ArgumentReader, 
     * read() must return correct Arguments.
     */
    @Test
    public void correctArgumentsReturnsArguments() {
        String projectPath = "/pathI/";
        String resultPath = "/pathO/";
        String apkPath = apkFile.getAbsolutePath();
        String filtersPath = filterFile.getAbsolutePath();
        String[] inputArgs = new String[] {"-i", projectPath, "-o", resultPath, 
                                           "-a", apkPath, "-f", filtersPath};
        ArgumentReader sut = new ArgumentReader(inputArgs);

        Arguments args = sut.read();

        assertThat(args, notNullValue());
        assertThat(args.getProjectPath(), equalTo(projectPath));
        assertThat(args.getResultPath(), equalTo(resultPath));
        assertThat(args.getApkFilePath(), equalTo(apkPath));
        assertThat(args.getFiltersPath(), equalTo(filtersPath));
    }
}