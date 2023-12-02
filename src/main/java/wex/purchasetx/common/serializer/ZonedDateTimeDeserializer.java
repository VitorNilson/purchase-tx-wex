package wex.purchasetx.common.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;
import wex.purchasetx.common.exception.UnsupportedDateFormatException;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.ZonedDateTime;

@JsonComponent
public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    private final DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
            DateTimeFormatter.ISO_OFFSET_DATE_TIME
    };


    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {

        String dateValue = jsonParser.getText();

        for (DateTimeFormatter formatter : formatters) {
            try {
                return ZonedDateTime.parse(dateValue, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        throw new UnsupportedDateFormatException("Invalid date format: " + dateValue);
    }
}