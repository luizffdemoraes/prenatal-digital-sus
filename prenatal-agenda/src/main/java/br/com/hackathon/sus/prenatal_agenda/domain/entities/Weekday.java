package br.com.hackathon.sus.prenatal_agenda.domain.entities;

import java.time.DayOfWeek;
import java.util.Map;

/**
 * Weekday enum with mapping to Java DayOfWeek.
 * Table-driven design to avoid complex if/else.
 * Constants (SEGUNDA, TERCA, etc.) kept for DB compatibility (@Enumerated STRING).
 */
public enum Weekday {
    SEGUNDA(DayOfWeek.MONDAY, "SEGUNDA"),
    TERCA(DayOfWeek.TUESDAY, "TERÇA"),
    QUARTA(DayOfWeek.WEDNESDAY, "QUARTA"),
    QUINTA(DayOfWeek.THURSDAY, "QUINTA"),
    SEXTA(DayOfWeek.FRIDAY, "SEXTA"),
    SABADO(DayOfWeek.SATURDAY, "SÁBADO"),
    DOMINGO(DayOfWeek.SUNDAY, "DOMINGO");

    private final DayOfWeek dayOfWeek;
    private final String descricao;

    private static final Map<DayOfWeek, Weekday> BY_DAY_OF_WEEK = Map.of(
            DayOfWeek.MONDAY, SEGUNDA,
            DayOfWeek.TUESDAY, TERCA,
            DayOfWeek.WEDNESDAY, QUARTA,
            DayOfWeek.THURSDAY, QUINTA,
            DayOfWeek.FRIDAY, SEXTA,
            DayOfWeek.SATURDAY, SABADO,
            DayOfWeek.SUNDAY, DOMINGO
    );

    Weekday(DayOfWeek dayOfWeek, String descricao) {
        this.dayOfWeek = dayOfWeek;
        this.descricao = descricao;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Weekday fromDayOfWeek(DayOfWeek dayOfWeek) {
        return BY_DAY_OF_WEEK.get(dayOfWeek);
    }
}
