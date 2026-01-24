package br.com.hackathon.sus.prenatal_agenda.infrastructure.config.dependency;

import br.com.hackathon.sus.prenatal_agenda.application.usecases.*;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.*;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers.AppointmentController;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers.AvailabilityController;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers.DoctorScheduleController;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers.PatientController;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways.*;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.AppointmentRepository;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.DoctorScheduleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjectionConfig {

    // Controllers
    @Bean
    public DoctorScheduleController doctorScheduleController(
            CreateDoctorScheduleUseCase createDoctorScheduleUseCase,
            UpdateDoctorScheduleUseCase updateDoctorScheduleUseCase,
            DeleteDoctorScheduleUseCase deleteDoctorScheduleUseCase,
            FindDoctorScheduleUseCase findDoctorScheduleUseCase,
            DoctorGateway doctorGateway
    ) {
        return new DoctorScheduleController(
                createDoctorScheduleUseCase,
                updateDoctorScheduleUseCase,
                deleteDoctorScheduleUseCase,
                findDoctorScheduleUseCase,
                doctorGateway
        );
    }

    @Bean
    public AppointmentController appointmentController(
            CreateAppointmentUseCase createAppointmentUseCase,
            CancelAppointmentUseCase cancelAppointmentUseCase
    ) {
        return new AppointmentController(createAppointmentUseCase, cancelAppointmentUseCase);
    }

    @Bean
    public AvailabilityController availabilityController(ListAvailabilityUseCase listAvailabilityUseCase) {
        return new AvailabilityController(listAvailabilityUseCase);
    }

    @Bean
    public PatientController patientController(
            FindAppointmentsByPatientUseCase findAppointmentsByPatientUseCase,
            PatientGateway patientGateway
    ) {
        return new PatientController(findAppointmentsByPatientUseCase, patientGateway);
    }

    // Gateways
    @Bean
    public DoctorScheduleGateway doctorScheduleGateway(DoctorScheduleRepository repository) {
        return new DoctorScheduleGatewayImpl(repository);
    }

    @Bean
    public AppointmentGateway appointmentGateway(AppointmentRepository repository) {
        return new AppointmentGatewayImpl(repository);
    }

    @Bean
    public DoctorGateway doctorGateway() {
        return new DoctorGatewayImpl();
    }

    @Bean
    public PatientGateway patientGateway() {
        return new PatientGatewayImpl();
    }

    // Use Cases - DoctorSchedule
    @Bean
    public CreateDoctorScheduleUseCase createDoctorScheduleUseCase(
            DoctorScheduleGateway doctorScheduleGateway,
            DoctorGateway doctorGateway
    ) {
        return new CreateDoctorScheduleUseCaseImp(doctorScheduleGateway, doctorGateway);
    }

    @Bean
    public FindDoctorScheduleUseCase findDoctorScheduleUseCase(DoctorScheduleGateway doctorScheduleGateway) {
        return new FindDoctorScheduleUseCaseImp(doctorScheduleGateway);
    }

    @Bean
    public UpdateDoctorScheduleUseCase updateDoctorScheduleUseCase(
            DoctorScheduleGateway doctorScheduleGateway,
            DoctorGateway doctorGateway
    ) {
        return new UpdateDoctorScheduleUseCaseImp(doctorScheduleGateway, doctorGateway);
    }

    @Bean
    public DeleteDoctorScheduleUseCase deleteDoctorScheduleUseCase(
            DoctorScheduleGateway doctorScheduleGateway,
            AppointmentGateway appointmentGateway,
            DoctorGateway doctorGateway
    ) {
        return new DeleteDoctorScheduleUseCaseImp(doctorScheduleGateway, appointmentGateway, doctorGateway);
    }

    // Use Cases - Appointment
    @Bean
    public CreateAppointmentUseCase createAppointmentUseCase(
            PatientGateway patientGateway,
            DoctorGateway doctorGateway,
            AppointmentGateway appointmentGateway,
            DoctorScheduleGateway doctorScheduleGateway
    ) {
        return new CreateAppointmentUseCaseImp(patientGateway, doctorGateway, appointmentGateway, doctorScheduleGateway);
    }

    @Bean
    public FindAppointmentsByPatientUseCase findAppointmentsByPatientUseCase(AppointmentGateway appointmentGateway) {
        return new FindAppointmentsByPatientUseCaseImp(appointmentGateway);
    }

    @Bean
    public CancelAppointmentUseCase cancelAppointmentUseCase(AppointmentGateway appointmentGateway) {
        return new CancelAppointmentUseCaseImp(appointmentGateway);
    }

    @Bean
    public ListAvailabilityUseCase listAvailabilityUseCase(
            DoctorScheduleGateway doctorScheduleGateway,
            AppointmentGateway appointmentGateway
    ) {
        return new ListAvailabilityUseCaseImp(doctorScheduleGateway, appointmentGateway);
    }
}
