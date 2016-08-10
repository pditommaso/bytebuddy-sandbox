import static net.bytebuddy.matcher.ElementMatchers.named

import groovy.transform.CompileStatic
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.SuperCall
import net.bytebuddy.pool.TypePool

class Target6 {
    public static String foo(@SuperCall origin, String str) {
        return origin.call().reverse()
    }
}


@CompileStatic
class Test6 {

    static void main(String... args) {
        new Test6().run()
    }

    void run () {

        TypePool typePool = TypePool.Default.ofClassPath();

        new ByteBuddy()
                .rebase(typePool.describe("Helper").resolve(), ClassFileLocator.ForClassLoader.ofClassPath())
                .method(named("foo")).intercept(MethodDelegation.to(Target6.class))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)


        def script =
                '''
                assert Helper.foo('Hello World!') == '!dlroW olleH'
                assert Helper.bar() == 'Say bar'
                assert new Other().doThisAndThat() == '!dlroW olleH'
                '''

        new GroovyShell(new GroovyClassLoader()).parse(script) .run()

    }
}
