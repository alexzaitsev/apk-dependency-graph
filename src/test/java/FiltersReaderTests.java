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
     * When file with empty 'package-name' parameter
     * is passed to FiltersReader, read() must return null
     * and print error message.
     */
    @Test
    public void filtersFileWithEmptyPackageNameReturnsNullAndPrintsMessage() throws IOException {
        String filtersEmptyPackageName = "{" +
            "\"package-name\": \"\"," +
            "\"show-inner-classes\": false," +
            "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
        "}";
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(filtersEmptyPackageName);    
        fw.close();

        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());
        Filters filters = sut.read();

        assertThat(filters, nullValue());
        String message = "'package-name' option cannot be empty. Check " + filterFile.getAbsolutePath();
        assertThat(errContent.toString(), containsString(message));
    }

    /**
     * When filter file without 'package-name' parameter
     * is passed to FiltersReader, read() must return null
     * and print error message.
     */
    @Test
    public void filtersFileWithoutPackageNameReturnsFiltersAndPrintsMessage() throws IOException {
        String filtersWithoutPackageName = "{" +
            "\"show-inner-classes\": false," +
            "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
        "}";
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(filtersWithoutPackageName);    
        fw.close();

        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());
        Filters filters = sut.read();

        assertThat(filters, nullValue());
        String message = "'package-name' option cannot be empty. Check " + filterFile.getAbsolutePath();
        assertThat(errContent.toString(), containsString(message));
    }

    /**
     * When filter file with full parameters
     * is passed to FiltersReader, read() must return correct Filters.
     */
    @Test
    public void correctFiltersFileReturnsFilters() throws IOException {
        String filtersFull = "{" +
            "\"package-name\": \"com.example.package\"," +
            "\"show-inner-classes\": false," +
            "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
        "}";
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(filtersFull);    
        fw.close();

        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());
        Filters filters = sut.read();

        assertThat(filters, notNullValue());
        assertThat(filters.getPackageName(), equalTo("com.example.package"));
        assertThat(filters.isProcessingInner(), is(false));
        assertThat(filters.getIgnoredClasses(),
            equalTo(new String[] {".*Dagger.*", ".*Injector.*", ".*\\$_ViewBinding$", ".*_Factory$"}));
    }

    /**
     * When filter file without `show-inner-classes` option is provided,
     * read() must return correct Filters.
     */
    @Test
    public void filtersFileWithoutInnerClassesReturnsFilters() throws IOException {
        String filtersWithoutShowInner = "{" +
            "\"package-name\": \"com.example.package\"," +
            "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
        "}";
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(filtersWithoutShowInner);
        fw.close();

        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());
        Filters filters = sut.read();

        assertThat(filters, notNullValue());
        assertThat(filters.getPackageName(), equalTo("com.example.package"));
        assertThat(filters.isProcessingInner(), is(false));
        assertThat(filters.getIgnoredClasses(), 
            equalTo(new String[] {".*Dagger.*", ".*Injector.*", ".*\\$_ViewBinding$", ".*_Factory$"}));
    }

    /**
     * When `show-inner-classes` option is enabled,
     * read() must return correct Filters and print info message.
     */
    @Test
    public void correctFiltersFileWithEnabledInnerClassesReturnsFiltersAndPrintsMessage() throws IOException {
        String filtersShowInnerTrue = "{" +
            "\"package-name\": \"com.example.package\"," +
            "\"show-inner-classes\": true," +
            "\"ignored-classes\": [\".*Dagger.*\", \".*Injector.*\", \".*\\$_ViewBinding$\", \".*_Factory$\"]" +
        "}";
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(filtersShowInnerTrue);
        fw.close();

        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());
        Filters filters = sut.read();

        assertThat(filters, notNullValue());
        assertThat(filters.getPackageName(), equalTo("com.example.package"));
        assertThat(filters.isProcessingInner(), is(true));
        assertThat(filters.getIgnoredClasses(), 
            equalTo(new String[] {".*Dagger.*", ".*Injector.*", ".*\\$_ViewBinding$", ".*_Factory$"}));
        String message = "Warning! Processing including inner classes.";
        assertThat(outContent.toString(), containsString(message));
    }

    /**
     * When filter file without `ignored-classes` option is provided,
     * read() must return correct Filters and print into message.
     */
    @Test
    public void filtersFileWithoutIgnoredClassesReturnsFiltersAndPrintsMessage() throws IOException {
        String filtersWithoutIgnoredClasses = "{" +
            "\"package-name\": \"com.example.package\"," +
            "\"show-inner-classes\": false" +
        "}";
        FileWriter fw = new FileWriter(filterFile);    
        fw.write(filtersWithoutIgnoredClasses);
        fw.close();

        FiltersReader sut = new FiltersReader(filterFile.getAbsolutePath());
        Filters filters = sut.read();

        assertThat(filters, notNullValue());
        assertThat(filters.getPackageName(), equalTo("com.example.package"));
        assertThat(filters.isProcessingInner(), is(false));
        assertThat(filters.getIgnoredClasses(), nullValue());
        String message = "Warning! Processing without class filtering.";
        assertThat(outContent.toString(), containsString(message));
    }
}