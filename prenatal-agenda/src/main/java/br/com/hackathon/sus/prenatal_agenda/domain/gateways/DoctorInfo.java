package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

/**
 * Dados do médico para exibição (nome e especialidade).
 * Retornado por DoctorGateway.findById.
 */
public record DoctorInfo(String name, String specialty) {
}
