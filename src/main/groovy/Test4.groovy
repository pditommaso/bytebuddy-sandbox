import static net.bytebuddy.matcher.ElementMatchers.named

import java.util.concurrent.Callable

import groovy.transform.CompileStatic
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.SuperCall

/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */

@CompileStatic
class Helper {

    static public String foo(String str) {
        return str;
    }

    static public String bar() {
        return 'Say bar'
    }
}

@CompileStatic
class Interceptor {
    public static String intercept(@SuperCall Callable<String> obj, String str) {
        return "${str.reverse()} - ${obj.call()}"
    }
}


@CompileStatic
class Other {
    String doThisAndThat() {
        Helper.foo('Hello World!')
    }
}


@CompileStatic
class Test4 {

    static void main(String... args) {

        def gcl = new GroovyClassLoader()

        new ByteBuddy()
                .rebase(Helper.class)
                .method(named("foo")).intercept(MethodDelegation.to(Interceptor.class))
                .make()
                .load(gcl, ClassLoadingStrategy.Default.INJECTION)


        def script =
                """
                assert Helper.foo('Hello World!') == '!dlroW olleH - Hello World!'
                assert new Other().doThisAndThat() == '!dlroW olleH - Hello World!'
                """

        new GroovyShell(gcl).parse(script) .run()
    }
}
