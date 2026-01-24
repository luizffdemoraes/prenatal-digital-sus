package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AgendarConsultaRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.GestanteResolver;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.UnidadeResolver;

public class AgendarConsultaPorIdentificacaoUseCaseImp implements AgendarConsultaPorIdentificacaoUseCase {

    private final GestanteResolver gestanteResolver;
    private final MedicoResolver medicoResolver;
    private final UnidadeResolver unidadeResolver;
    private final AgendarConsultaUseCase agendarConsultaUseCase;

    public AgendarConsultaPorIdentificacaoUseCaseImp(GestanteResolver gestanteResolver,
                                                     MedicoResolver medicoResolver,
                                                     UnidadeResolver unidadeResolver,
                                                     AgendarConsultaUseCase agendarConsultaUseCase) {
        this.gestanteResolver = gestanteResolver;
        this.medicoResolver = medicoResolver;
        this.unidadeResolver = unidadeResolver;
        this.agendarConsultaUseCase = agendarConsultaUseCase;
    }

    @Override
    public Consulta execute(AgendarConsultaRequest req) {
        Long gestanteId = resolveGestanteId(req);
        Long unidadeId = resolveUnidadeId(req);
        Long medicoId = resolveMedicoId(req, unidadeId);

        Consulta consulta = new Consulta(gestanteId, medicoId, unidadeId, req.data(), req.horario());
        return agendarConsultaUseCase.execute(consulta);
    }

    private Long resolveGestanteId(AgendarConsultaRequest req) {
        if (req.gestanteId() != null) {
            return req.gestanteId();
        }
        if (req.gestanteCpf() != null && !req.gestanteCpf().isBlank()) {
            return gestanteResolver.buscarPorCpf(req.gestanteCpf().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Gestante não encontrada para o CPF informado."));
        }
        if (req.gestanteEmail() != null && !req.gestanteEmail().isBlank()) {
            return gestanteResolver.buscarPorEmail(req.gestanteEmail().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Gestante não encontrada para o e-mail informado."));
        }
        throw new IllegalArgumentException("Informe o CPF ou e-mail da gestante, ou o ID se ela já estiver logada.");
    }

    private Long resolveUnidadeId(AgendarConsultaRequest req) {
        if (req.unidadeId() != null) {
            return req.unidadeId();
        }
        if (req.unidadeNome() != null && !req.unidadeNome().isBlank()) {
            return unidadeResolver.buscarPorNome(req.unidadeNome().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada: \"" + req.unidadeNome() + "\"."));
        }
        throw new IllegalArgumentException("Informe o nome da unidade de saúde ou o ID.");
    }

    private Long resolveMedicoId(AgendarConsultaRequest req, Long unidadeId) {
        if (req.medicoId() != null) {
            return req.medicoId();
        }
        if (req.medicoNome() != null && !req.medicoNome().isBlank()) {
            return medicoResolver.buscarPorNome(req.medicoNome().trim(), unidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: \"" + req.medicoNome() + "\"."));
        }
        if (req.especialidade() != null && !req.especialidade().isBlank()) {
            return medicoResolver.buscarPorEspecialidade(req.especialidade().trim(), unidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Nenhum médico encontrado para a especialidade: \"" + req.especialidade() + "\"."));
        }
        throw new IllegalArgumentException("Informe o nome ou a especialidade do médico.");
    }
}
