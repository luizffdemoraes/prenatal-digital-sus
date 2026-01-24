package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AgendarConsultaRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.GestanteResolver;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;

public class AgendarConsultaPorIdentificacaoUseCaseImp implements AgendarConsultaPorIdentificacaoUseCase {

    private final GestanteResolver gestanteResolver;
    private final MedicoResolver medicoResolver;
    private final AgendarConsultaUseCase agendarConsultaUseCase;

    public AgendarConsultaPorIdentificacaoUseCaseImp(GestanteResolver gestanteResolver,
                                                     MedicoResolver medicoResolver,
                                                     AgendarConsultaUseCase agendarConsultaUseCase) {
        this.gestanteResolver = gestanteResolver;
        this.medicoResolver = medicoResolver;
        this.agendarConsultaUseCase = agendarConsultaUseCase;
    }

    @Override
    public Consulta execute(AgendarConsultaRequest req, Long unidadeId) {
        if (unidadeId == null) {
            throw new IllegalArgumentException("Unidade (UBS) é obrigatória. Envie o header X-Unidade-Id.");
        }
        Long gestanteId = resolveGestanteId(req);
        Long medicoId = resolveMedicoId(req, unidadeId);

        Consulta consulta = new Consulta(gestanteId, medicoId, unidadeId, req.data(), req.horario());
        return agendarConsultaUseCase.execute(consulta);
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
