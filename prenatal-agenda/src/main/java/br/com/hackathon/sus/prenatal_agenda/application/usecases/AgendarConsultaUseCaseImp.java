package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AgendarConsultaRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DiaSemana;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.ConsultaGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.GestanteResolver;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;

import java.util.List;

public class AgendarConsultaUseCaseImp implements AgendarConsultaUseCase {

    private final GestanteResolver gestanteResolver;
    private final MedicoResolver medicoResolver;
    private final ConsultaGateway consultaGateway;
    private final AgendaMedicoGateway agendaMedicoGateway;

    public AgendarConsultaUseCaseImp(GestanteResolver gestanteResolver,
                                     MedicoResolver medicoResolver,
                                     ConsultaGateway consultaGateway,
                                     AgendaMedicoGateway agendaMedicoGateway) {
        this.gestanteResolver = gestanteResolver;
        this.medicoResolver = medicoResolver;
        this.consultaGateway = consultaGateway;
        this.agendaMedicoGateway = agendaMedicoGateway;
    }

    @Override
    public Consulta execute(AgendarConsultaRequest req, Long unidadeId) {
        if (unidadeId == null) {
            throw new IllegalArgumentException("Unidade (UBS) é obrigatória. Envie o header X-Unidade-Id.");
        }
        Long gestanteId = resolveGestanteId(req);
        Long medicoId = resolveMedicoId(req, unidadeId);

        Consulta consulta = new Consulta(gestanteId, medicoId, unidadeId, req.data(), req.horario());

        var agenda = agendaMedicoGateway.buscarPorMedicoId(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para o médico informado"));

        DiaSemana diaSemana = DiaSemana.fromDayOfWeek(consulta.getData().getDayOfWeek());
        if (!agenda.atendeNoDia(diaSemana)) {
            throw new IllegalArgumentException("Médico não atende neste dia da semana");
        }

        if (!agenda.horarioDentroDoPeriodo(consulta.getHorario())) {
            throw new IllegalArgumentException("Horário fora do período de atendimento do médico");
        }

        List<Consulta> consultasExistentes = consultaGateway.buscarConsultasAgendadas(
                medicoId, consulta.getData(), consulta.getHorario());
        if (!consultasExistentes.isEmpty()) {
            throw new IllegalArgumentException("Horário já está ocupado");
        }

        return consultaGateway.salvar(consulta);
    }

    private Long resolveGestanteId(AgendarConsultaRequest req) {
        String cpf = req.gestanteCpf() != null ? req.gestanteCpf().trim() : "";
        if (cpf.isBlank()) {
            throw new IllegalArgumentException("CPF da gestante é obrigatório.");
        }
        return gestanteResolver.buscarPorCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Gestante não encontrada para o CPF informado."));
    }

    private Long resolveMedicoId(AgendarConsultaRequest req, Long unidadeId) {
        if (req.crm() != null && !req.crm().isBlank()) {
            return medicoResolver.buscarPorCrm(req.crm().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado para o CRM informado."));
        }
        if (req.medicoNome() != null && !req.medicoNome().isBlank()) {
            return medicoResolver.buscarPorNome(req.medicoNome().trim(), unidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: \"" + req.medicoNome() + "\"."));
        }
        if (req.especialidade() != null && !req.especialidade().isBlank()) {
            return medicoResolver.buscarPorEspecialidade(req.especialidade().trim(), unidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Nenhum médico encontrado para a especialidade: \"" + req.especialidade() + "\"."));
        }
        throw new IllegalArgumentException("Informe o nome, a especialidade ou o CRM do médico.");
    }
}
