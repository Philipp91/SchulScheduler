package schulscheduler.ui.windows;

import javax.inject.Scope;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Ein Dependency Injection Scope, der pro Fenster (WindowComponent) existiert.
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
@Scope
@Documented
public @interface WindowScoped {
}
