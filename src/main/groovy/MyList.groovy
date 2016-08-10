import groovy.transform.CompileStatic

/**
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */

@CompileStatic
class MyList extends ArrayList implements Marker {

    String toString() {
        this.join('-')
    }

}
