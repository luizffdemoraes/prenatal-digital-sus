# Lógica de Alertas e E-mails – Pré-natal Digital SUS

Este documento descreve a lógica de alertas, regras por semana gestacional e a diferenciação de e-mails entre gestante e médico, alinhada às boas práticas do PNI e da Atenção Pré-natal.

---

## 0. Duração da gestação (referência clínica)

- **Ideal**: até **40 semanas** (nove meses).
- **A termo (segura)**: entre **37 e 42 semanas**.
- O acompanhamento considera gestações de 1 a 44 semanas para cobrir pré-termo, termo e pós-termo.

---

## 1. Regras de alerta por semana gestacional

| Exame/Vacina          | Janela gestacional   | Alvo e-mail | Responsabilidade                              |
|-----------------------|----------------------|-------------|-----------------------------------------------|
| **Translucência nucal** | 12ª–14ª semana       | Gestante    | Agendar ultrassom na UBS                      |
| **Ultrassom morfológico** | ≥ 20ª semana         | Gestante    | Agendar ultrassom morfológico                 |
| **Vacina antitetânica (dT/dTpa)** | ≥ 20ª semana         | Gestante + Médico | Gestante: procurar UBS; Médico: orientar e administrar |
| **Curva glicêmica**   | ≥ 28ª semana         | Gestante    | Realizar exame de rastreamento de DMG         |
| **Hepatite B**        | Qualquer fase        | Gestante + Médico | Gestante: iniciar/completar 3 doses; Médico: orientar |
| **Influenza (gripe)** | Qualquer fase        | Gestante + Médico | Gestante: 1 dose anual; Médico: orientar e administrar |
| **Consulta agendada** | Contínuo             | Médico      | Médico/equipe: garantir próxima consulta      |
| **Alto risco**        | Conforme exames      | Médico      | Médico: atenção clínica prioritária           |
| **Ecocardiograma fetal** | 20ª–24ª semana    | Médico      | Médico: solicitar exame – avaliação cardíaca fetal |
| **Exames de sangue**  | A partir da 8ª semana | Médico     | Médico: solicitar hemograma, tipagem, glicemia, sorologias |
| **Exames de urina**   | A partir da 8ª semana | Médico     | Médico: solicitar EAS, urocultura |

**Nota:** O ultrassom morfológico também gera alerta para o **médico** (solicitar, se ausente) além do alerta para a gestante.

---

## 2. Semana gestacional e tomada de decisões

### 2.1 Exames

- **12ª–14ª semana**: translucência nucal – rastreio de síndromes (ex.: Down). Fora da janela, o exame perde utilidade clínica.
- **20ª semana em diante**: ultrassom morfológico – avaliação anatômica fetal.
- **28ª semana em diante**: curva glicêmica – rastreio de diabetes mellitus gestacional.
- **20ª–24ª semana**: ecocardiograma fetal – avaliação cardíaca fetal (o médico deve solicitar).

**Exames de sangue e urina** (rotina 1ª consulta, a partir de ~8 semanas): hemograma, tipagem sanguínea, glicemia, sorologias (VDRL, HIV, hepatite, toxoplasmose), EAS e urocultura. O médico deve ser orientado a solicitar se não constarem no prontuário.

### 2.2 Vacinas (Calendário Nacional de Vacinação da Gestante – PNI)

- **Antitetânica (dTpa)**: indicada **a partir da 20ª semana até o final da gravidez** (idealmente até 40 semanas; termo 37–42 semanas); 1 dose por gestação; reduz tétano e coqueluche no recém-nascido.
- **Hepatite B**: qualquer fase; esquema de 3 doses (intervalos: 1 mês entre 1ª e 2ª; 6 meses entre 1ª e 3ª).
- **Influenza**: qualquer fase; 1 dose anual da vacina da temporada.

---

## 3. Diferenciação de e-mails por destinatário

### 3.1 Gestante (responsabilidades)

- Mensagens em linguagem clara e orientadora.
- Foco no que a gestante deve fazer: procurar UBS, agendar exames, tomar vacinas.
- Recomendação explícita: “procure sua unidade de saúde”.
- Canal de suporte: Disque 136 – OuvSUS.

### 3.2 Médico/profissional (responsabilidades)

- Linguagem clínica com severidade e ID do paciente.
- Foco em ações profissionais: verificar prontuário, solicitar exames, orientar e administrar vacinas, contatar gestante.
- Alertas que exigem decisão clínica: consulta não agendada, gestante de alto risco.
- Responsabilidade compartilhada em vacinas: reforçar orientação e oferta na consulta.

---

## 4. Tipos de alerta e destinos

| Tipo                     | Severidade | Destino     | Exemplo de mensagem (gestante)                    | Exemplo de mensagem (médico)                          |
|--------------------------|------------|-------------|---------------------------------------------------|------------------------------------------------------|
| MISSING_EXAM             | HIGH       | PATIENT     | “Ultrassom morfológico não encontrado”            | —                                                    |
| PENDING_VACCINE          | MEDIUM     | PATIENT     | “Vacina antitetânica pendente – procure UBS”      | —                                                    |
| PENDING_VACCINE          | MEDIUM     | PROFESSIONAL| —                                                 | “Gestante com vacina antitetânica pendente – orientar e administrar” |
| NO_APPOINTMENT_SCHEDULED | MEDIUM     | PROFESSIONAL| —                                                 | “Não há próxima consulta agendada”                   |
| HIGH_RISK_ATTENTION      | HIGH       | PROFESSIONAL| —                                                 | “Gestante de risco com exame crítico pendente”       |
| MISSING_EXAM             | HIGH       | PROFESSIONAL| —                                                 | “Solicitar ultrassom morfológico – não encontrado”   |
| MISSING_EXAM             | MEDIUM     | PROFESSIONAL| —                                                 | “Solicitar ecocardiograma fetal – janela 20ª–24ª sem” |
| MISSING_EXAM             | MEDIUM     | PROFESSIONAL| —                                                 | “Solicitar exames de sangue (hemograma, tipagem…)”   |
| MISSING_EXAM             | MEDIUM     | PROFESSIONAL| —                                                 | “Solicitar exames de urina (EAS, urocultura)”        |

---

## 5. Tipos de exame aceitos (tabela `documento.documento_medico`)

| Categoria    | Valores de `tipo_exame` aceitos                                                                 |
|--------------|-------------------------------------------------------------------------------------------------|
| Morfológico  | MORPHOLOGICAL_ULTRASOUND, ULTRASOUND                                                           |
| Ecocardiograma | ECOCARDIOGRAMA, ECO_CARDIACA, ECOCARDIOGRAMA_FETAL, ECHOCARDIOGRAM, FETAL_ECHO                |
| Sangue       | HEMOGRAMA, TIPAGEM_SANGUINEA, GLICEMIA, VDRL, HIV, SOROLOGIA, HEPATITE, TOXOPLASMOSE, SIFILIS   |
| Urina        | EAS, UROCULTURA, URINA, URINALISE, EXAME_URINA                                                  |

## 6. Tipos de vacina aceitos (tabela `documento.vacina`)

| Vacina       | Valores de `tipo_vacina` aceitos                          |
|--------------|-----------------------------------------------------------|
| Antitetânica | DTPA, DTAP, DT, DUPLA_ADULTO                              |
| Hepatite B   | HEPATITE_B, HEPATITEB, HB                                 |
| Influenza    | INFLUENZA, GRIPE, FLU                                     |

---

## 7. Assinatura e canal de suporte nos e-mails

- **Assinatura**: “Pré-natal Digital – SUS Digital – Ministério da Saúde”
- **Campo From**: “Pré-natal Digital SUS” \<noreply@…\>
- **Suporte**: Disque 136 – Ouvidoria-Geral do SUS (OuvSUS) – segunda a sexta, 8h às 20h
