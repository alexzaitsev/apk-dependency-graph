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
     * to ArgumentReader, read() must return Null.
     */
    @Test
    public void wrongArgumentsReturnsNull() {
        String[] args = new String[] {"-i", "/path/"};
        ArgumentReader sut = new ArgumentReader(args);

        assertThat(sut.read(), nullValue());
    }

    /**
     * When wrong or incomplete arguments are passed
     * to ArgumentReader, read() must print error message.
     */
    @Test
    public void wrongArgumentsPrintsMessage() {
        String[] args = new String[] {"-i", "/path/"};
        new ArgumentReader(args).read();

        assertThat(errContent.toString(), containsString("must be provided!"));
        assertThat(errContent.toString(), containsString("Usage:"));
    }

    /**
     * When non-existing Apk file path is passed
     * to ArgumentReader, read() must return Null.
     */
    @Test
    public void nonExistingApkPathReturnsNull() {
        String[] args = new String[] {"-i", "/path/", "-o", "/path/", "-a", "/wrong/"};
        ArgumentReader sut = new ArgumentReader(args);

        assertThat(sut.read(), nullValue());
    }

    /**
     * When non-existing Apk file path is passed
     * to ArgumentReader, read() must print error message.
     */
    @Test
    public void nonExistingApkPathPrintsMessage() {
        String[] args = new String[] {"-i", "/path/", "-o", "/path/", "-a", "/wrong/"};
        new ArgumentReader(args).read();

        String message = "/wrong is not found!";
        assertThat(errContent.toString(), containsString(message));
    }

    /**
     * When wrong filter is passed
     * to ArgumentReader, read() must return Arguments.
     */
    @Test
    public void wrongFilterFileReturnsNull() {
        String[] inputArgs = new String[] {"-i", "/path/", "-o", "/path/", 
                                           "-a", apkFile.getAbsolutePath(), 
                                           "-f", "/wrong/"};
        Arguments args = new ArgumentReader(inputArgs).read();

        assertThat(args, nullValue());
    }

    /**
     * When wrong filter is passed
     * to ArgumentReader, read() must print error message.
     */
    @Test
    public void wrongFilterFilePrintsMessage() {
        String[] inputArgs = new String[] {"-i", "/path/", "-o", "/path/", 
                                           "-a", apkFile.getAbsolutePath(), 
                                           "-f", "/wrong/"};
        Arguments args = new ArgumentReader(inputArgs).read();

        String message = "/wrong is not found!";
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
        Arguments args = new ArgumentReader(inputArgs).read();

        assertThat(args, notNullValue());
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
        Arguments args = new ArgumentReader(inputArgs).read();

        assertThat(args, notNullValue());
        assertThat(args.getProjectPath(), equalTo(projectPath));
        assertThat(args.getResultPath(), equalTo(resultPath));
        assertThat(args.getApkFilePath(), equalTo(apkPath));
        assertThat(args.getFiltersPath(), equalTo(filtersPath));
    }
}