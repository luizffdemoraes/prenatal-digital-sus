package br.com.hackathon.sus.prenatal_agenda.domain.entities;

import java.time.DayOfWeek;
import java.util.Map;

/**
 * Enum para dias da semana com mapeamento para DayOfWeek do Java
 * Usa table-driven design para evitar if/else complexos
 */
public enum DiaSemana {
    SEGUNDA(DayOfWeek.MONDAY, "SEGUNDA"),
    TERCA(DayOfWeek.TUESDAY, "TERÇA"),
    QUARTA(DayOfWeek.WEDNESDAY, "QUARTA"),
    QUINTA(DayOfWeek.THURSDAY, "QUINTA"),
    SEXTA(DayOfWeek.FRIDAY, "SEXTA"),
    SABADO(DayOfWeek.SATURDAY, "SÁBADO"),
    DOMINGO(DayOfWeek.SUNDAY, "DOMINGO");

    private final DayOfWeek dayOfWeek;
    private final String descricao;

    private static final Map<DayOfWeek, DiaSemana> BY_DAY_OF_WEEK = Map.of(
            DayOfWeek.MONDAY, SEGUNDA,
            DayOfWeek.TUESDAY, TERCA,
            DayOfWeek.WEDNESDAY, QUARTA,
            DayOfWeek.THURSDAY, QUINTA,
            DayOfWeek.FRIDAY, SEXTA,
            DayOfWeek.SATURDAY, SABADO,
            DayOfWeek.SUNDAY, DOMINGO
    );

    DiaSemana(DayOfWeek dayOfWeek, String descricao) {
        this.dayOfWeek = dayOfWeek;
        this.descricao = descricao;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte DayOfWeek para DiaSemana usando table lookup
     */
    public static DiaSemana fromDayOfWeek(DayOfWeek dayOfWeek) {
        return BY_DAY_OF_WEEK.get(dayOfWeek);
    }
}
