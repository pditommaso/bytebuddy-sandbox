/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */


import static net.bytebuddy.matcher.ElementMatchers.named
import static net.bytebuddy.matcher.ElementMatchers.takesArguments

import java.util.concurrent.Callable

import groovy.transform.CompileStatic
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.SuperCall
import net.bytebuddy.pool.TypePool


interface Marker { }

class Target7 {
    public static void write(@SuperCall Callable origin, Writer writer, Object object) {
        if( object instanceof Marker ) {
            writer.write(object.toString())
        }
        else {
            origin.call()
        }
    }
}


@CompileStatic
class Test7 {

    static void main(String... args) {
        new Test7().run()
    }

    void run () {

        TypePool typePool = TypePool.Default.ofClassPath();

        new ByteBuddy()
                .rebase(typePool.describe("org.codehaus.groovy.runtime.InvokerHelper").resolve(), ClassFileLocator.ForClassLoader.ofClassPath())
                .method(named("write").and(takesArguments(Writer,Object)))
                .intercept(MethodDelegation.to(Target7.class))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)


        def script =
                '''
                def plain = [1,2,3]
                def list = new MyList()
                list << 1 << 2 << 3
                def str = "$list"
                assert str.toString() == '1-2-3'
                assert str == '1-2-3'
                assert "${plain}" == '[1, 2, 3]'
                '''

        new GroovyShell(new GroovyClassLoader()).parse(script) .run()

    }
}
