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

import com.alex_zaitsev.adg.filter.*;
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
        String[] ignoredClasses = new String[] {".*Dagger.*", ".*Injector.*", ".*\\$_ViewBinding$", ".*_Factory$"};
        defaultFilters = new Filters(packageName, Filters.DEFAULT_PROCESS_INNER, ignoredClasses);
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
        FilterProvider sut = new FilterProvider(defaultFilters);
        Filter<String> filter = sut.makePathFilter();

        assertThat(filter, nullValue());
    }

    /**
     * If package name is empty, `makePathFilter` returns null.
     */
    @Test
    public void makePathFilterReturnsNullIfPackageNameIsEmpty() {
        defaultFilters.setPackageName("");
        FilterProvider sut = new FilterProvider(defaultFilters);
        Filter<String> filter = sut.makePathFilter();

        assertThat(filter, nullValue());
    }

    /**
     * If provided filters are ok, `makePathFilter` returns expected Filter.
     */
    @Test
    public void makePathFilterReturnsExpectedFilter() {
        FilterProvider sut = new FilterProvider(defaultFilters);
        Filter<String> filter = sut.makePathFilter();

        assertThat(filter, notNullValue());
        String filterStringRepr = "RegexFilter{.*com/example/package.*}".replaceAll("/", File.separator);
        assertThat(filter.toString(), equalTo(filterStringRepr));
    }

    /**
     * If provided filters are ok, `makePathFilter` returns Filter
     * that filters as expected.
     */
    @Test
    public void makePathFilterReturnsFilterThatFiltersAsExpected() {
        FilterProvider sut = new FilterProvider(defaultFilters);
        Filter<String> filter = sut.makePathFilter();

        assertThat(filter, notNullValue());

        String correctPath1 = "com/example/package".replaceAll("/", File.separator);
        assertThat(filter.filter(correctPath1), is(true));
        String correctPath2 = "some/path/com/example/package/inner".replaceAll("/", File.separator);
        assertThat(filter.filter(correctPath2), is(true));

        String wrongPath1 = "com/example/wrong".replaceAll("/", File.separator);
        assertThat(filter.filter(wrongPath1), is(false));
        String wrongPath2 = "com/wrong/package".replaceAll("/", File.separator);
        assertThat(filter.filter(wrongPath2), is(false));
    }

    /** 
     * If provided filters are ok, `makeClassFilter` returns AndFilter with
     * InverseRegexFilter and RegexFilter.
     */
    @Test
    public void makeClassFilterReturnsExpectedFilter() {
        FilterProvider sut = new FilterProvider(defaultFilters);
        Filter<String> filter = sut.makeClassFilter();

        assertThat(filter, notNullValue());
        String inverseRegexFilter = "InverseRegexFilter{.*Dagger.*|.*Injector.*|.*\\$_ViewBinding$|.*_Factory$}";
        assertThat(filter.toString(), equalTo(inverseRegexFilter));
    }

    /** 
     * If provided filters are ok, `makeClassFilter` returns Filter
     * that filters as expected.
     */
    @Test
    public void makeClassFilterReturnsFilterThatFiltersAsExpected() {
        FilterProvider sut = new FilterProvider(defaultFilters);
        Filter<String> filter = sut.makeClassFilter();

        assertThat(filter, notNullValue());

        assertThat(filter, notNullValue());
        String notPassing1 = "ClassDagger";
        assertThat(filter.filter(notPassing1), is(false));
        String notPassing2 = "SomeInjectorClass";
        assertThat(filter.filter(notPassing2), is(false));
        String notPassing3 = "QrCodeZxingMvpPresenterImpl_Factory";
        assertThat(filter.filter(notPassing3), is(false));
        String notPassing4 = "Some$_ViewBinding";
        assertThat(filter.filter(notPassing4), is(false));

        String passing = "SomeClass";
        assertThat(filter.filter(passing), is(true));
    }
}