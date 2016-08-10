import static net.bytebuddy.matcher.ElementMatchers.named;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class Hello {

    public static void main(String... args) throws Exception {
        new Hello().run();
    }

    public void run() throws Exception {

        String toString = new ByteBuddy()
                .subclass(Object.class)
                .name("example.Type")
                .method(named("toString")).intercept(FixedValue.value("Hello World!"))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance()
                .toString();

        System.out.println(toString);

    }
}
