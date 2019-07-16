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
        // RegexFilter{.*com/example/package.*}
        String filterStringRepr = "RegexFilter{.*com" + File.separator + "example" + File.separator + "package.*}";
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

        // com/example/package
        String correctPath1 = "com" + File.separator + "example" + File.separator + "package";
        assertThat(sut.filter(correctPath1), is(true));
        // some/path/com/example/package/inner
        String correctPath2 = "some" + File.separator + "path" + File.separator + 
            "com" + File.separator + "example" + File.separator + "package" + File.separator + "inner";
        assertThat(sut.filter(correctPath2), is(true));

        // com/example/wrong
        String wrongPath1 = "com" + File.separator + "example" + File.separator + "wrong";
        assertThat(sut.filter(wrongPath1), is(false));
        // com/wrong/package
        String wrongPath2 = "com" + File.separator + "wrong" + File.separator + "package";
        assertThat(sut.filter(wrongPath2), is(false));
    }

    /** 
     * If package name is null, `makeClassFilter` returns AndFilter with
     * InverseRegexFilter.
     */
    @Test
    public void makeClassFilterReturnsFilterWithoutRegexIfPackageNameIsNull() {
        defaultFilters.setPackageName(null);
        Filter<String> sut = FilterProvider.makeClassFilter(defaultFilters);

        assertThat(sut, notNullValue());
        String filterStringRepr = "AndFilter[InverseRegexFilter{.*Dagger.*|.*Injector.*|.*\\$_ViewBinding$|.*_Factory$}]";
        assertThat(sut.toString(), equalTo(filterStringRepr));
    }

    /** 
     * If package name is null, `makeClassFilter` returns Filter
     * that filters as expected.
     */
    @Test
    public void makeClassFilterReturnsFilterWithoutRegexThatFiltersAsExpectedIfPackageNameIsNull() {
        defaultFilters.setPackageName(null);
        Filter<String> sut = FilterProvider.makeClassFilter(defaultFilters);

        assertThat(sut, notNullValue());
        String notPassing1 = "ClassDagger";
        assertThat(sut.filter(notPassing1), is(false));
        String notPassing2 = "QrCodeZxingMvpPresenterImpl_Factory";
        assertThat(sut.filter(notPassing2), is(false));
        String notPassing3 = "Some$_ViewBinding";
        assertThat(sut.filter(notPassing3), is(false));

        String passing = "MyManager";
        assertThat(sut.filter(passing), is(true));
    }

    /** 
     * If provided filters are ok, `makeClassFilter` returns AndFilter with
     * InverseRegexFilter and RegexFilter.
     */
    @Test
    public void makeClassFilterReturnsExpectedFilter() {
        Filter<String> sut = FilterProvider.makeClassFilter(defaultFilters);

        assertThat(sut, notNullValue());
        String inverseRegexFilter = "InverseRegexFilter{.*Dagger.*|.*Injector.*|.*\\$_ViewBinding$|.*_Factory$}";
        // RegexFilter{^com/example/package}
        String regexFilter = "RegexFilter{^com" + File.separator + "example" + File.separator + "package}";
        String filterStringRepr = "AndFilter[" + inverseRegexFilter + ", " + regexFilter + "]";
        assertThat(sut.toString(), equalTo(filterStringRepr));
    }

    /** 
     * If provided filters are ok, `makeClassFilter` returns Filter
     * that filters as expected.
     */
    @Test
    public void makeClassFilterReturnsFilterThatFiltersAsExpected() {
        /*
        Filter<String> sut = FilterProvider.makeClassFilter(defaultFilters);

        assertThat(sut, notNullValue());
        String notPassing1 = "ClassDagger";
        assertThat(sut.filter(notPassing1), is(false));
        String notPassing2 = "QrCodeZxingMvpPresenterImpl_Factory";
        assertThat(sut.filter(notPassing2), is(false));
        String notPassing3 = "Some$_ViewBinding";
        assertThat(sut.filter(notPassing3), is(false));

        String passing = "MyManager";
        assertThat(sut.filter(passing), is(true));
        */
    }
}