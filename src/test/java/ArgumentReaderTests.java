import static org.junit.Assert.assertNull;

import org.junit.Test;
import com.alex_zaitsev.adg.io.ArgumentReader;

public class ArgumentReaderTests {

    @Test
    public void wrongArgumentsReturnsNullArguments() {
        String[] args = new String[] {"-i /path/"};
        ArgumentReader sut = new ArgumentReader(args);

        assertNull(sut.read());
    }
}