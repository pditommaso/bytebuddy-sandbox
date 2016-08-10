import static net.bytebuddy.matcher.ElementMatchers.named

import groovy.transform.CompileStatic
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.pool.TypePool
import org.codehaus.groovy.tools.RootLoader

@CompileStatic
class Target {
    public static String foo(String str) {
        return str.reverse()
    }
}


@CompileStatic
class Test5 {

    static void main(String... args) {

        def gcl = new RootLoader([] as URL[], Thread.currentThread().getContextClassLoader())
        TypePool typePool = TypePool.Default.ofClassPath();

        new ByteBuddy()
                .rebase(typePool.describe("Helper").resolve(), ClassFileLocator.ForClassLoader.ofClassPath())
                .method(named("foo")).intercept(MethodDelegation.to(Target.class))
                .make()
                .load(gcl, ClassLoadingStrategy.Default.CHILD_FIRST)


        def script =
                """
                assert Helper.foo('Hello World!') == '!dlroW olleH'
                assert new Other().doThisAndThat() == '!dlroW olleH'
                """

        new GroovyShell(gcl).parse(script) .run()
    }
}
