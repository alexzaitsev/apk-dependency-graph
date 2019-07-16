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

import com.alex_zaitsev.adg.filter.Filter;
import com.alex_zaitsev.adg.io.Arguments;
import com.alex_zaitsev.adg.io.Filters;
import com.alex_zaitsev.adg.FilterProvider;

public class FilterProviderTests {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
     * If package name is null, `makePathFilter` returns null.
     */
    @Test
    public void makePathFilterReturnsNullIfPackageNameIsNull() {
        defaultFilters.setPackageName(null);
        Filter<String> sut = FilterProvider.makePathFilter(defaultFilters);

        assertThat(sut, nullValue());
    }

    /**
     * If package name is empty, `makePathFilter` returns null.
     */
    @Test
    public void makePathFilterReturnsNullIfPackageNameIsEmpty() {
        defaultFilters.setPackageName("");
        Filter<String> sut = FilterProvider.makePathFilter(defaultFilters);

        assertThat(sut, nullValue());
    }

    /**
     * If provided filters are ok, `makePathFilter` returns expected Filter.
     */
    @Test
    public void makePathFilterReturnsExpectedFilter() {
        Filter<String> sut = FilterProvider.makePathFilter(defaultFilters);

        assertThat(sut, notNullValue());
        String filterStringRepr = "RegexFilter{.*com/example/package.*}"
            .replaceAll("/", File.separator);
        assertThat(sut.toString(), equalTo(filterStringRepr));
    }

    /**
     * If provided filters are ok, `makePathFilter` returns Filter
     * that filters as expected.
     */
    @Test
    public void makePathFilterReturnsFilterThatFiltersAsExpected() {
        Filter<String> sut = FilterProvider.makePathFilter(defaultFilters);

        assertThat(sut, notNullValue());
        String correctPath1 = "com/example/package".replaceAll("/", File.separator);
        assertThat(sut.filter(correctPath1), is(true));
        String correctPath2 = "some/path/com/example/package/inner"
            .replaceAll("/", File.separator);
        assertThat(sut.filter(correctPath2), is(true));
        String wrongPath1 = "com/example/wrong".replaceAll("/", File.separator);
        assertThat(sut.filter(wrongPath1), is(false));
        String wrongPath2 = "com/wrong/package".replaceAll("/", File.separator);
        assertThat(sut.filter(wrongPath2), is(false));
    }

    @Test
    public void makeClassFilter() {
        
    }
}