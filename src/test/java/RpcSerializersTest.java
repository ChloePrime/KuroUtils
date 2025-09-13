import cn.chloeprime.commons_impl.rpc.serialization.RpcSerializationUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RpcSerializersTest {
    @Test
    public void testParentClassExtractor() {
        var simpleTestData = List.of(
                new ByteArrayInputStream(new byte[0]),
                new DataInputStream(new ByteArrayInputStream(new byte[0])),
                new FilterInputStream(new ByteArrayInputStream(new byte[0])) {}
        );
        var interfaceTestData = List.of(
                new Impl1(),
                new Impl2()
        );
        var strangeInterfaceTestData = List.of(
                new Impl2(),
                new Impl3(),
                new Impl4(),
                new Interface() {
                    @Override
                    public String toString() {
                        return "wahaha";
                    }
                }
        );
        var pureTempClassTestData = List.of(
                new Interface() {
                    @Override
                    public String toString() {
                        return "Coke Cola Le Le";
                    }
                }
        );
        assertEquals(InputStream.class, RpcSerializationUtils.findCommonParentClasses(simpleTestData));
        assertEquals(Interface.class, RpcSerializationUtils.findCommonParentClasses(interfaceTestData));
        assertEquals(Interface.class, RpcSerializationUtils.findCommonParentClasses(strangeInterfaceTestData));
        assertEquals(Interface.class, RpcSerializationUtils.findCommonParentClasses(pureTempClassTestData));
    }

    private interface Interface {
    }

    private static class Impl1 implements Interface {
    }

    private static class Impl2 implements Interface {
    }

    private static class Impl3 extends Impl2 implements Serializable {
    }

    private static class Impl4 implements Interface, Serializable {
    }
}
