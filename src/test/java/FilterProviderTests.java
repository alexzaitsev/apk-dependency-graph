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
import com.alex_zaitsev.adg.FilterProvider;

public class FilterProviderTests {

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
     * 
     */
    @Test
    public void makePathFilterReturnsNullIfPackageNameIsNull() {
        
    }

    @Test
    public void makePathFilterReturnsNullIfPackageNameIsEmpty() {
        
    }

    @Test
    public void makePathFilterReturnsFilterIfFiltersAreOk() {
        
    }

    @Test
    public void makePathFilterReturnsCorrectFilter() {
        
    }

    @Test
    public void makeClassFilter() {
        
    }
}