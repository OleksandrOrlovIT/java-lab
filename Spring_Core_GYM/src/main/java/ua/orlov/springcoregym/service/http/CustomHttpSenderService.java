package ua.orlov.springcoregym.service.http;

import ua.orlov.springcoregym.model.HttpRequest;

import java.io.IOException;

public interface CustomHttpSenderService {

    boolean executeRequestWithEntity(HttpRequest request, String entity);

}
