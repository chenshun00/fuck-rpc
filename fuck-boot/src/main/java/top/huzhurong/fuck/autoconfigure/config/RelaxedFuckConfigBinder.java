package top.huzhurong.fuck.autoconfigure.config;

import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.handler.IgnoreErrorsBindHandler;
import org.springframework.boot.context.properties.bind.handler.NoUnboundElementsBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.UnboundElementsSourceFilter;

import java.io.Serializable;

import static org.springframework.boot.context.properties.source.ConfigurationPropertySources.from;


/**
 * @author chenshun00@gmail.com
 * @since 2019/5/3
 */
public class RelaxedFuckConfigBinder extends AbstractFuckConfigBinder {

    public <C extends Serializable> void bind(String prefix, C config) {

        // Converts ConfigurationPropertySources
        Iterable<ConfigurationPropertySource> propertySources = from(getPropertySources());

        // Wrap Bindable from Config instance
        Bindable<C> bindable = Bindable.ofInstance(config);

        Binder binder = new Binder(propertySources);

        // Get BindHandler
        BindHandler bindHandler = getBindHandler();

        // Bind
        binder.bind(prefix, bindable, bindHandler);
    }


    private BindHandler getBindHandler() {
        BindHandler handler = BindHandler.DEFAULT;
        if (isIgnoreInvalidFields()) {
            handler = new IgnoreErrorsBindHandler(handler);
        }
        if (!isIgnoreUnknownFields()) {
            UnboundElementsSourceFilter filter = new UnboundElementsSourceFilter();
            handler = new NoUnboundElementsBindHandler(handler, filter);
        }
        return handler;
    }

    public boolean isIgnoreInvalidFields() {
        return ignoreInvalidFields;
    }

    public boolean isIgnoreUnknownFields() {
        return ignoreUnknownFields;
    }


    private boolean ignoreUnknownFields = true;

    private boolean ignoreInvalidFields = false;
}
