package com.lbraz.meusgastosapi.domain.exception;

public class ResourceBadRequestException extends RuntimeException {

    public ResourceBadRequestException(String mensagem){
        super(mensagem);
    }
}
