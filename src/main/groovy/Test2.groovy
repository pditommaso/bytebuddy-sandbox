import static net.bytebuddy.matcher.ElementMatchers.named

import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.MethodDelegation
/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class Test2 {

    static class Source {
        public String hello(String name) {
            return null;
        }
    }

    static class Target {
        public static String hello(String name) {
            return "Hello " + name + "!";
        }
    }

    static void main(String... args) {

        def gcl = new GroovyClassLoader()

        new ByteBuddy()
                .subclass(Source.class)
                .name("example.Type")
                .method(named("hello")).intercept(MethodDelegation.to(Target.class))
                .make()
                .load(gcl, ClassLoadingStrategy.Default.INJECTION)


        def script =
                """
                assert new example.Type().hello('Paolo') == 'Hello Paolo!'
                """

        new GroovyShell(gcl).parse(script) .run()
    }
}
