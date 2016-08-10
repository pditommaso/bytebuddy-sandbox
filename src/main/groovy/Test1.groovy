import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue

import static net.bytebuddy.matcher.ElementMatchers.named

/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class Test1 {

    static void main(String... args) {

        def gcl = new GroovyClassLoader()

        new ByteBuddy()
                .subclass(Object.class)
                .name("example.Type")
                .method(named("toString")).intercept(FixedValue.value("Hello World!"))
                .make()
                .load(gcl, ClassLoadingStrategy.Default.INJECTION)


        def script =
                """
                assert new example.Type().toString() == 'Hello World!'
                """

        new GroovyShell(gcl).parse(script) .run()
    }
}
