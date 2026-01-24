package br.com.hackathon.sus.prenatal_agenda.infrastructure.config.dependency;

import br.com.hackathon.sus.prenatal_agenda.application.usecases.*;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.ConsultaGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.GestanteResolver;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers.AgendaMedicoController;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers.ConsultaController;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers.DisponibilidadeController;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers.GestanteController;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways.AgendaMedicoGatewayImpl;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways.ConsultaGatewayImpl;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.AgendaMedicoRepository;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.ConsultaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjectionConfig {

    // Controllers
    @Bean
    public AgendaMedicoController agendaMedicoController(
            CriarAgendaMedicoUseCase criarAgendaMedicoUseCase,
            AtualizarAgendaMedicoUseCase atualizarAgendaMedicoUseCase,
            ExcluirAgendaMedicoUseCase excluirAgendaMedicoUseCase,
            BuscarAgendaMedicoUseCase buscarAgendaMedicoUseCase,
            MedicoResolver medicoResolver
    ) {
        return new AgendaMedicoController(
                criarAgendaMedicoUseCase,
                atualizarAgendaMedicoUseCase,
                excluirAgendaMedicoUseCase,
                buscarAgendaMedicoUseCase,
                medicoResolver
        );
    }

    @Bean
    public ConsultaController consultaController(
            AgendarConsultaUseCase agendarConsultaUseCase,
            CancelarConsultaUseCase cancelarConsultaUseCase
    ) {
        return new ConsultaController(agendarConsultaUseCase, cancelarConsultaUseCase);
    }

    @Bean
    public DisponibilidadeController disponibilidadeController(
            ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase
    ) {
        return new DisponibilidadeController(consultarDisponibilidadeUseCase);
    }

    @Bean
    public GestanteController gestanteController(
            BuscarConsultasPorGestanteUseCase buscarConsultasPorGestanteUseCase,
            GestanteResolver gestanteResolver
    ) {
        return new GestanteController(buscarConsultasPorGestanteUseCase, gestanteResolver);
    }

    // Gateways
    @Bean
    public AgendaMedicoGateway agendaMedicoGateway(AgendaMedicoRepository repository) {
        return new AgendaMedicoGatewayImpl(repository);
    }

    @Bean
    public ConsultaGateway consultaGateway(ConsultaRepository repository) {
        return new ConsultaGatewayImpl(repository);
    }

    // Use Cases - Agenda
    @Bean
    public CriarAgendaMedicoUseCase criarAgendaMedicoUseCase(
            AgendaMedicoGateway agendaMedicoGateway,
            MedicoResolver medicoResolver
    ) {
        return new CriarAgendaMedicoUseCaseImp(agendaMedicoGateway, medicoResolver);
    }

    @Bean
    public BuscarAgendaMedicoUseCase buscarAgendaMedicoUseCase(AgendaMedicoGateway agendaMedicoGateway) {
        return new BuscarAgendaMedicoUseCaseImp(agendaMedicoGateway);
    }

    @Bean
    public AtualizarAgendaMedicoUseCase atualizarAgendaMedicoUseCase(
            AgendaMedicoGateway agendaMedicoGateway,
            MedicoResolver medicoResolver
    ) {
        return new AtualizarAgendaMedicoUseCaseImp(agendaMedicoGateway, medicoResolver);
    }

    @Bean
    public ExcluirAgendaMedicoUseCase excluirAgendaMedicoUseCase(
            AgendaMedicoGateway agendaMedicoGateway,
            ConsultaGateway consultaGateway,
            MedicoResolver medicoResolver
    ) {
        return new ExcluirAgendaMedicoUseCaseImp(agendaMedicoGateway, consultaGateway, medicoResolver);
    }

    // Use Cases - Consulta
    @Bean
    public AgendarConsultaUseCase agendarConsultaUseCase(
            GestanteResolver gestanteResolver,
            MedicoResolver medicoResolver,
            ConsultaGateway consultaGateway,
            AgendaMedicoGateway agendaMedicoGateway
    ) {
        return new AgendarConsultaUseCaseImp(
                gestanteResolver, medicoResolver, consultaGateway, agendaMedicoGateway);
    }

    @Bean
    public BuscarConsultasPorGestanteUseCase buscarConsultasPorGestanteUseCase(ConsultaGateway consultaGateway) {
        return new BuscarConsultasPorGestanteUseCaseImp(consultaGateway);
    }

    @Bean
    public CancelarConsultaUseCase cancelarConsultaUseCase(ConsultaGateway consultaGateway) {
        return new CancelarConsultaUseCaseImp(consultaGateway);
    }

    @Bean
    public ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase(
            AgendaMedicoGateway agendaMedicoGateway,
            ConsultaGateway consultaGateway
    ) {
        return new ConsultarDisponibilidadeUseCaseImp(agendaMedicoGateway, consultaGateway);
    }
}
