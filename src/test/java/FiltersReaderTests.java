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

import com.alex_zaitsev.adg.io.FiltersReader;
import com.alex_zaitsev.adg.io.Filters;

public class FiltersReaderTests {

    private static final String PACKAGE_NAME = "com.example.package";
    private static final boolean SHOW_INNER_CLASSES_DEFAULT = false;
    private static final boolean SHOW_INNER_CLASSES_TRUE = true;
    private static final String[] IGNORED_CLASSES_DEFAULT = new String[] {
        ".*Dagger.*", ".*Injector.*", ".*\\$_ViewBinding$", ".*_Factory$"
    };
    private static final String FILTERS_DEFAULT = "{" +
        "\"package-name\": \"\"," +
        "\"show-inner-classes\": false," +
        "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
    "}";
    private static final String FILTERS_WITHOUT_PACKAGE_NAME = "{" +
        "\"show-inner-classes\": false," +
        "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
    "}";
    private static final String FILTERS_SHOW_INNER_TRUE = "{" +
        "\"package-name\": \"com.example.package\"," +
        "\"show-inner-classes\": true," +
        "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
    "}";
    private static final String FILTERS_FULL = "{" +
        "\"package-name\": \"com.example.package\"," +
        "\"show-inner-classes\": false," +
        "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
    "}";
    private static final String FILTERS_MALFORMED = "{\"malformed\"}";

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private File filterFile;

    @Before
    public void setUp() throws IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        filterFile = folder.newFile("default.json");
    }

    @After
    public void teardown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * When wrong file is passed to FiltersReader,
     * read() must return null and print error message.
     */
    @Test
    public void wrongFileReturnsNullAndPrintsMessage() {
        String wrongFilePath = "/wrong/";
        FiltersReader sut = new FiltersReader(wrongFilePath);

        Filters filters = sut.read();

        assertThat(filters, nullValue());
        String message = "An error happened during " + wrongFilePath + " processing!";
        assertThat(errContent.toString(), containsString(message));
    }

    /**
     * When malformed filter file is passed to FiltersReader, 
     * read() must return null and print error message.
     */
    @Test
    public void malformedFiltersFileReturnsNullAndPrintsMessage() throws IOException {
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(FILTERS_MALFORMED);    
        fw.close();
        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());

        Filters filters = sut.read();

        assertThat(filters, nullValue());
        String message = "An error happened during " + filterFile.getAbsolutePath() + " processing!";
        assertThat(errContent.toString(), containsString(message));
    }

    /**
     * When correct file with empty 'package-name' parameter
     * is passed to FiltersReader, read() must return correct Filters
     * and print info message.
     */
    @Test
    public void correctFiltersFileWithEmptyPackageNameReturnsFiltersAndPrintsMessage() throws IOException {
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(FILTERS_DEFAULT);    
        fw.close();
        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());

        Filters filters = sut.read();

        assertThat(filters, notNullValue());
        assertThat(filters.getPackageName(), nullValue());
        assertThat(filters.isProcessingInner(), equalTo(SHOW_INNER_CLASSES_DEFAULT));
        assertThat(filters.getIgnoredClasses(), equalTo(IGNORED_CLASSES_DEFAULT));
        String message = "Warning! Processing without package filter.";
        assertThat(outContent.toString(), containsString(message));
    }

    /**
     * When filter file without 'package-name' parameter
     * is passed to FiltersReader, read() must return correct Filters
     * and print info message.
     */
    @Test
    public void correctFiltersFileWithoutPackageNameReturnsFiltersAndPrintsMessage() throws IOException {
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(FILTERS_WITHOUT_PACKAGE_NAME);    
        fw.close();
        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());

        Filters filters = sut.read();

        assertThat(filters, notNullValue());
        assertThat(filters.getPackageName(), nullValue());
        assertThat(filters.isProcessingInner(), equalTo(SHOW_INNER_CLASSES_DEFAULT));
        assertThat(filters.getIgnoredClasses(), equalTo(IGNORED_CLASSES_DEFAULT));
        String message = "Warning! Processing without package filter.";
        assertThat(outContent.toString(), containsString(message));
    }

    /**
     * When filter file with full parameters
     * is passed to FiltersReader, read() must return correct Filters.
     */
    @Test
    public void correctFiltersFileReturnsFilters() throws IOException {
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(FILTERS_FULL);    
        fw.close();
        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());

        Filters filters = sut.read();

        assertThat(filters, notNullValue());
        assertThat(filters.getPackageName(), equalTo(PACKAGE_NAME));
        assertThat(filters.isProcessingInner(), equalTo(SHOW_INNER_CLASSES_DEFAULT));
        assertThat(filters.getIgnoredClasses(), equalTo(IGNORED_CLASSES_DEFAULT));
    }

    /**
     * When `show-inner-classes` option is enabled,
     * read() must return correct Filters and info message is printed.
     */
    @Test
    public void correctFiltersFileWithEnabledInnerClassesPrintsMessage() throws IOException {
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(FILTERS_SHOW_INNER_TRUE);    
        fw.close();
        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());

        Filters filters = sut.read();

        assertThat(filters, notNullValue());
        assertThat(filters.getPackageName(), equalTo(PACKAGE_NAME));
        assertThat(filters.isProcessingInner(), equalTo(SHOW_INNER_CLASSES_TRUE));
        assertThat(filters.getIgnoredClasses(), equalTo(IGNORED_CLASSES_DEFAULT));
        String message = "Warning! Processing including inner classes.";
        assertThat(outContent.toString(), containsString(message));
    }
}