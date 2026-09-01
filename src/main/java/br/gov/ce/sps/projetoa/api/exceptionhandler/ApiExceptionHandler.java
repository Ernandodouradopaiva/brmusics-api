package br.gov.ce.sps.projetoa.api.exceptionhandler;

import br.gov.ce.sps.projetoa.core.security.UsuarioSecurityMessages;
import br.gov.ce.sps.projetoa.core.security.exception.TokenJwtInvalidoException;
import br.gov.ce.sps.projetoa.domain.exception.EntidadeEmUsoException;
import br.gov.ce.sps.projetoa.domain.exception.EntidadeNaoEncontradaException;
import br.gov.ce.sps.projetoa.domain.exception.InscritoEncaminhadoException;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.infrastructure.service.storage.StorageException;
import br.gov.ce.sps.projetoa.infrastructure.service.report.ReportExcepetion;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.LazyInitializationException;
import org.modelmapper.MappingException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String MSG_ERRO_GENERICA_USUARIO_FINAL
            = "Ocorreu um erro inesperado no sistema. Tente novamente e, se o problema persistir, "
            + "entre em contato com o administrador.";

    public static final String MSG_DADOS_INVALIDOS
            = "Um ou mais campos estão inválidos. Corrija o preenchimento e tente novamente.";

    public static final String MSG_CORPO_INVALIDO
            = "Os dados enviados estão incorretos ou incompletos. Verifique o formulário e tente novamente.";

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(status).headers(headers).build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleValidationInternal(ex, headers, status, request, ex.getBindingResult());
    }

    private ResponseEntity<Object> handleValidationInternal(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request,
            BindingResult bindingResult) {
        ProblemType problemType = ProblemType.DADOS_INVALIDOS;

        List<Problem.Object> problemObjects = bindingResult.getAllErrors().stream()
                .map(objectError -> {
                    String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
                    String name = objectError.getObjectName();
                    if (objectError instanceof FieldError fieldError) {
                        name = fieldError.getField();
                    }
                    return Problem.Object.builder()
                            .name(name)
                            .userMessage(message)
                            .build();
                })
                .collect(Collectors.toList());

        String userMessage = montarMensagemValidacao(problemObjects, MSG_DADOS_INVALIDOS);

        Problem problem = createProblemBuilder((HttpStatus) status, problemType, MSG_DADOS_INVALIDOS)
                .userMessage(userMessage)
                .objects(problemObjects)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private static String montarMensagemValidacao(List<Problem.Object> objects, String fallback) {
        if (objects == null || objects.isEmpty()) {
            return fallback;
        }
        if (objects.size() == 1) {
            String msg = objects.get(0).getUserMessage();
            return msg != null && !msg.isBlank() ? msg.trim() : fallback;
        }
        return fallback;
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleUsuarioInativo(DisabledException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ProblemType problemType = ProblemType.NAO_AUTENTICADO;
        String userMessage = mensagemUsuarioInativo(ex);

        if (log.isDebugEnabled()) {
            log.debug("Login negado — usuário inativo: {}", ex.getMessage());
        }

        Problem problem = createProblemBuilder(status, problemType, userMessage)
                .userMessage(userMessage)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Object> handleAccessDenied(RuntimeException ex, WebRequest request) {
        return buildAcessoNegadoProblem(ex, request);
    }

    @ExceptionHandler(ExpressionException.class)
    public ResponseEntity<Object> handleSecurityExpression(ExpressionException ex, WebRequest request) {
        log.warn("Erro ao avaliar expressão de segurança: {}", ex.getMessage());
        return buildAcessoNegadoProblem(ex, request);
    }

    private ResponseEntity<Object> buildAcessoNegadoProblem(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ProblemType problemType = ProblemType.ACESSO_NEGADO;
        String userMessage = SecurityExpressionMessageResolver.resolveUserMessage(ex);
        String detail = SecurityExpressionMessageResolver.resolveDetail(ex);

        if (SecurityExpressionMessageResolver.isSecurityExpressionFailure(ex)) {
            log.error("Falha na expressão @PreAuthorize (SpEL): {}", detail, ex);
        } else {
            log.warn("Acesso negado em {} — {}", request.getDescription(false), detail);
        }

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(userMessage)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<Object> handleLazyInitialization(LazyInitializationException ex, WebRequest request) {
        return buildLazyOrMappingProblem(ex, request, ex);
    }

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<Object> handleModelMapper(MappingException ex, WebRequest request) {
        Throwable root = ExceptionUtils.getRootCause(ex);
        if (root instanceof LazyInitializationException lazy) {
            return buildLazyOrMappingProblem(ex, request, lazy);
        }
        return handleUncaught(ex, request);
    }

    private ResponseEntity<Object> buildLazyOrMappingProblem(
            Exception ex, WebRequest request, LazyInitializationException lazy) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemType problemType = ProblemType.ERRO_DE_SISTEMA;
        String detail = LazyInitializationMessageResolver.resolve(lazy);

        log.warn(
                "Associação lazy acessada fora da sessão Hibernate em {}: {}",
                request.getDescription(false),
                lazy.getMessage(),
                lazy);

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<Object> handleInvalidDataAccessResourceUsage(
            InvalidDataAccessResourceUsageException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemType problemType = ProblemType.ERRO_DE_SISTEMA;
        String detail = SqlErrorMessageResolver.resolve(ex);
        if (detail == null) {
            detail = resolverMensagemExcecaoNaoMapeada(ex);
            log.error("Erro de acesso ao banco (SQL)", ex);
        } else {
            log.warn("Erro de SQL: {}", detail);
        }

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestNotUsableException(
            AsyncRequestNotUsableException ex, WebRequest request) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Conexão encerrada pelo cliente em {}: {}",
                    request.getDescription(false),
                    ex.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        if (ClientDisconnectDetector.isClientDisconnected(ex)) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Conexão encerrada pelo cliente em {}: {}",
                        request.getDescription(false),
                        ex.getMessage());
            }
            return ResponseEntity.noContent().build();
        }

        if (SecurityExpressionMessageResolver.isSecurityExpressionFailure(ex)) {
            return buildAcessoNegadoProblem(ex, request);
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemType problemType = ProblemType.ERRO_DE_SISTEMA;
        String detail = resolverMensagemExcecaoNaoMapeada(ex);

        log.error("Erro não mapeado na API", ex);

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<?> handleJpaSystemException(JpaSystemException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.ERRO_NEGOCIO;
        String detail = resolverMensagemExcecaoNaoMapeada(ex);

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
        String detail = String.format("O recurso %s não existe.", ex.getRequestURL());
        String userMessage = "A página ou recurso solicitado não foi encontrado.";

        Problem problem = createProblemBuilder((HttpStatus) status, problemType, detail)
                .userMessage(userMessage)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            return handleMethodArgumentTypeMismatch(mismatch, headers, (HttpStatus) status, request);
        }
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemType problemType = ProblemType.PARAMETRO_INVALIDO;

        String detail = String.format(
                "O parâmetro '%s' recebeu o valor '%s', que é inválido.",
                ex.getName(), ex.getValue());

        String userMessage = "O endereço ou identificador informado é inválido. Verifique os dados e tente novamente.";

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(userMessage)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if (rootCause instanceof InvalidFormatException invalidFormat) {
            return handleInvalidFormat(invalidFormat, headers, (HttpStatus) status, request);
        }
        if (rootCause instanceof PropertyBindingException propertyBinding) {
            return handlePropertyBinding(propertyBinding, headers, (HttpStatus) status, request);
        }

        ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
        String detail = "O corpo da requisição está inválido ou mal formatado.";

        Problem problem = createProblemBuilder((HttpStatus) status, problemType, detail)
                .userMessage(MSG_CORPO_INVALIDO)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private ResponseEntity<Object> handlePropertyBinding(
            PropertyBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String path = joinPath(ex.getPath());
        ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
        String detail = String.format("A propriedade '%s' não é reconhecida.", path);
        String userMessage = String.format("O campo '%s' não é válido para esta operação.", path);

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(userMessage)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private ResponseEntity<Object> handleInvalidFormat(
            InvalidFormatException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String path = joinPath(ex.getPath());
        ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
        String detail = String.format(
                "O campo '%s' recebeu o valor '%s', que não é compatível com o tipo esperado.",
                path, ex.getValue());
        String userMessage = String.format(
                "O valor informado no campo '%s' é inválido. Verifique e tente novamente.", path);

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(userMessage)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<?> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex, WebRequest request) {
        return buildProblemFromException(ex, HttpStatus.NOT_FOUND, ProblemType.RECURSO_NAO_ENCONTRADO, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        return buildProblemFromException(ex, HttpStatus.NOT_FOUND, ProblemType.RECURSO_NAO_ENCONTRADO, request);
    }

    @ExceptionHandler(EntidadeEmUsoException.class)
    public ResponseEntity<?> handleEntidadeEmUso(EntidadeEmUsoException ex, WebRequest request) {
        return buildProblemFromException(ex, HttpStatus.CONFLICT, ProblemType.ENTIDADE_EM_USO, request);
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<?> handleNegocio(NegocioException ex, WebRequest request) {
        return buildProblemFromException(ex, HttpStatus.BAD_REQUEST, ProblemType.ERRO_NEGOCIO, request);
    }


    @ExceptionHandler(ReportExcepetion.class)
    public ResponseEntity<?> handleReport(ReportExcepetion ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String detail = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage().trim()
                : "Não foi possível gerar o relatório. Tente novamente; se persistir, contate o suporte.";
        log.error("Falha ao gerar relatório", ex);
        Problem problem = createProblemBuilder(status, ProblemType.ERRO_DE_SISTEMA, detail)
                .userMessage(detail)
                .build();
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }


    @ExceptionHandler(TokenJwtInvalidoException.class)
    public ResponseEntity<?> handleTokenJwtInvalido(TokenJwtInvalidoException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ProblemType problemType = ProblemType.NAO_AUTENTICADO;
        String detail = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage().trim()
                : "Sessão inválida ou expirada. Faça login novamente.";

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorage(StorageException ex, WebRequest request) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        String detail = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage().trim()
                : "O serviço de armazenamento de arquivos está indisponível no momento.";
        String userMessage = "Não foi possível concluir a operação com arquivos. Tente novamente mais tarde.";

        log.error("Falha no storage em {}: {}", request.getDescription(false), detail, ex);

        Problem problem = createProblemBuilder(status, ProblemType.ERRO_DE_SISTEMA, detail)
                .userMessage(userMessage)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(InscritoEncaminhadoException.class)
    public ResponseEntity<?> handleInscritoEncaminhado(InscritoEncaminhadoException ex, WebRequest request) {
        return buildProblemFromException(ex, HttpStatus.CONFLICT, ProblemType.ERRO_NEGOCIO, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.DADOS_INVALIDOS;

        List<Problem.Object> problemObjects = ex.getConstraintViolations().stream()
                .map(constraintViolation -> Problem.Object.builder()
                        .name(constraintViolation.getPropertyPath().toString())
                        .userMessage(constraintViolation.getMessage())
                        .build())
                .collect(Collectors.toList());

        String userMessage = montarMensagemValidacao(problemObjects, MSG_DADOS_INVALIDOS);

        Problem problem = createProblemBuilder(status, problemType, MSG_DADOS_INVALIDOS)
                .userMessage(userMessage)
                .objects(problemObjects)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemType problemType = ProblemType.ENTIDADE_EM_USO;
        String detail = traduzirMensagemIntegridade(ex);

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    private ResponseEntity<Object> buildProblemFromException(
            Exception ex, HttpStatus status, ProblemType problemType, WebRequest request) {
        String detail = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage().trim()
                : MSG_ERRO_GENERICA_USUARIO_FINAL;

        Problem problem = createProblemBuilder(status, problemType, detail)
                .userMessage(detail)
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ClientDisconnectDetector.isClientDisconnected(ex)) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Cliente desconectou; resposta de erro omitida em {}: {}",
                        request.getDescription(false),
                        ex.getMessage());
            }
            return null;
        }

        if (body == null) {
            body = Problem.builder()
                    .timestamp(OffsetDateTime.now())
                    .title(HttpStatus.valueOf(status.value()).getReasonPhrase())
                    .status(status.value())
                    .userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
                    .build();
        } else if (body instanceof String stringBody) {
            body = Problem.builder()
                    .timestamp(OffsetDateTime.now())
                    .title(stringBody)
                    .status(status.value())
                    .userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
                    .build();
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private Problem.ProblemBuilder createProblemBuilder(HttpStatus status, ProblemType problemType, String detail) {
        return Problem.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail);
    }

    private String joinPath(List<Reference> references) {
        return references.stream()
                .map(Reference::getFieldName)
                .collect(Collectors.joining("."));
    }

    private String traduzirMensagemIntegridade(DataIntegrityViolationException ex) {
        String sqlMsg = SqlErrorMessageResolver.resolve(ex);
        if (sqlMsg != null) {
            return sqlMsg;
        }

        Throwable cause = ex.getCause();
        while (cause != null) {
            String causeMsg = cause.getMessage();
            if (causeMsg != null) {
                String lower = causeMsg.toLowerCase(Locale.ROOT);
                if (lower.contains("duplicate key") || lower.contains("unique constraint")) {
                    return "Não foi possível salvar: registro duplicado ou conflito de identificador.";
                }
                if (lower.contains("usuario_grupo")) {
                    if (lower.contains("table \"usuario\"") || lower.contains("on table \"usuario\"")) {
                        return "Não é possível excluir o usuário pois está vinculado a um ou mais grupos. "
                                + "Remova os vínculos antes de excluir.";
                    }
                    return "Não é possível excluir o grupo pois existem usuários vinculados. "
                            + "Remova os usuários do grupo antes de excluir.";
                }
                if (lower.contains("grupo_permissao")) {
                    return "Não é possível excluir o grupo ou a permissão: existem vínculos ativos.";
                }
            }
            cause = cause.getCause();
        }
        return "Não é possível excluir ou alterar: existem registros associados a este item. "
                + "Remova os vínculos antes de tentar novamente.";
    }

    private String resolverMensagemExcecaoNaoMapeada(Throwable ex) {
        String lazyMsg = LazyInitializationMessageResolver.resolveFromThrowable(ex);
        if (lazyMsg != null) {
            return lazyMsg;
        }

        String sqlMsg = SqlErrorMessageResolver.resolve(ex);
        if (sqlMsg != null) {
            return sqlMsg;
        }

        Throwable root = ExceptionUtils.getRootCause(ex);
        String rootMsg = root != null ? root.getMessage() : ex.getMessage();

        if (rootMsg != null) {
            String msg = rootMsg.trim();
            if (!msg.isBlank()) {
                return msg;
            }
        }

        return MSG_ERRO_GENERICA_USUARIO_FINAL;
    }

    private static String mensagemUsuarioInativo(DisabledException ex) {
        if (ex.getMessage() != null && !ex.getMessage().isBlank()) {
            return ex.getMessage().trim();
        }
        return UsuarioSecurityMessages.USUARIO_INATIVO;
    }
}
