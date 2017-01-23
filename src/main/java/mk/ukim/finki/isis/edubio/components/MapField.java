package mk.ukim.finki.isis.edubio.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;

import java.util.Map;

/**
 * Created by Aleksandar on 01-Oct-16.
 */
public class MapField {
    @Parameter(required=true)
    Map<String, String> map;

    @Parameter(required=true, allowNull=false, defaultPrefix = BindingConstants.LITERAL)
    String key;

    public String getMapValue() {
        return map.get(key);
    }

    public void setMapValue(String value) {
        map.put(key, value);
    }
}
