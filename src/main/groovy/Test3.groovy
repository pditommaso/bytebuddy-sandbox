import static net.bytebuddy.matcher.ElementMatchers.named

import java.util.concurrent.Callable

import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.SuperCall

/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
class Test3 {

    static class Source {
        public String foo() {
            return 'foo';
        }

        public String bar() {
            return 'bar'
        }
    }

    static class Target {
        public static String wrap(@SuperCall Callable<String> zuper ) {
            println "before"
            def result = zuper.call()
            println "after"
            return result
        }
    }

    static void main(String... args) {

        def gcl = new GroovyClassLoader()

        new ByteBuddy()
                .subclass(Source.class)
                .name("example.Type")
                .method(named("foo")).intercept(MethodDelegation.to(Target.class))
                .make()
                .load(gcl, ClassLoadingStrategy.Default.INJECTION)


        def script =
                """
                assert new example.Type().foo() == 'foo'
                """

        new GroovyShell(gcl).parse(script) .run()
    }
}
