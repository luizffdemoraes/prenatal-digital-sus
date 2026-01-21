package br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions;

public class BusinessException extends RuntimeException{

    private String messageKey;
    private Object[] messageArgs;

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String messageKey, Object... messageArgs) {
        super(messageKey); // Mensagem padrão como fallback
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }

    public boolean hasMessageKey() {
        return messageKey != null;
    }
}