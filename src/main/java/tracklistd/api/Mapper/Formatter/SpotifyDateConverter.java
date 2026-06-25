package tracklistd.api.Mapper.Formatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.springframework.stereotype.Component;

@Component
public class SpotifyDateConverter {

    // Define um formatador flexível que aceita diferentes precisões
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy")
            .optionalStart().appendPattern("-MM").optionalEnd()
            .optionalStart().appendPattern("-dd").optionalEnd()
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .toFormatter();

    public LocalDate toLocalDate(String dateString) {
        if (dateString == null || dateString.isEmpty())
            return null;
        return LocalDate.parse(dateString, FORMATTER);
    }
}