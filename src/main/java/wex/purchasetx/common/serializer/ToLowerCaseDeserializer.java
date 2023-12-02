package wex.purchasetx.common.serializer;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class ToLowerCaseDeserializer extends StdConverter<String, String> {

    @Override
    public String convert(String value) {
        return value.toLowerCase();
    }
}
