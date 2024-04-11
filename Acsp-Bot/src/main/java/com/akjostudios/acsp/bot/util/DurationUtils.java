package com.akjostudios.acsp.bot.util;

import com.github.tonivade.purefun.type.Option;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class DurationUtils {
    private static final Pattern DURATION_PATTERN = Pattern.compile("^(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?$");

    public @NotNull Option<Duration> parse(@NotNull String value) {
        Matcher matcher = DURATION_PATTERN.matcher(value);
        if (!matcher.matches()) { return Option.none(); }

        long days = parseNumber(matcher.group(1));
        long hours = parseNumber(matcher.group(2));
        long minutes = parseNumber(matcher.group(3));
        long seconds = parseNumber(matcher.group(4));

        BigInteger totalSeconds = BigInteger.valueOf(days)
                .multiply(BigInteger.valueOf(24))
                .multiply(BigInteger.valueOf(60))
                .multiply(BigInteger.valueOf(60))
                .add(BigInteger.valueOf(hours)
                        .multiply(BigInteger.valueOf(60))
                        .multiply(BigInteger.valueOf(60))
                )
                .add(BigInteger.valueOf(minutes)
                        .multiply(BigInteger.valueOf(60))
                )
                .add(BigInteger.valueOf(seconds));
        if (totalSeconds.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) { return Option.none(); }

        return Option.some(Duration.ofDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds));
    }

    private static long parseNumber(String number) {
        return (number != null && !number.isEmpty()) ? Long.parseLong(number) : 0;
    }

    public @NotNull String format(@NotNull Duration duration) {
        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();
        long minutes = duration.minusDays(days).minusHours(hours).toMinutes();
        long seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).getSeconds();

        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }
}